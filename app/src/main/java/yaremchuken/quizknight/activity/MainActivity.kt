package yaremchuken.quizknight.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.compose.gamesmanager.GamesManagerView
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.model.ModuleType
import yaremchuken.quizknight.provider.QuizzesProvider
import java.time.Instant
import java.util.EnumMap
import java.util.Locale

/**
 * Amount of games Player can create in application
 */
const val MAX_GAMES = 4

const val DEFAULT_MODULE_PROGRESS = -1L

class MainActivity : AppCompatActivity() {

    private var gamesCount = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        initGames()
        QuizzesProvider.preload(this)
    }

    override fun onResume() {
        super.onResume()
        initGames()

    }

    private fun initGames() {
        lifecycleScope.launch {
            var games: List<GameStatsEntity>
            withContext(Dispatchers.IO) {
                games = (application as App).db.getGameStatsDao().fetchAll()
            }
            gamesCount = games.size.toLong()
            setContent {
                GamesManagerView(games, this@MainActivity::runGame, this@MainActivity::createGame)
            }
        }
    }

    private fun runGame(game: GameStatsEntity) {
        val progress: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                (application as App).db
                    .getModuleProgressDao()
                    .fetch(game.game)
                    .forEach { progress[it.module] = it.progress }

                (application as App).db
                    .getGameStatsDao()
                    .markLaunched(game.game, Instant.now().epochSecond)
            }
        }.invokeOnCompletion {
            switchToGame(game, progress)
        }
    }

    private fun createGame(name: String, original: Locale, studied: Locale) {
        if (name.isNotBlank()) {
            val gameStatsDao = (application as App).db.getGameStatsDao()
            val moduleProgressDao = (application as App).db.getModuleProgressDao()

            val newGame = GameStatsEntity(
                name, original, studied, ModuleType.LAZYWOOD, 0, GameStats.maxHealth.toDouble(),
                Instant.now().epochSecond, Instant.now().epochSecond)

            val progress: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
            val progressEntities = ArrayList<ModuleProgressEntity>()
            ModuleType.values().forEach {
                progress[it] = DEFAULT_MODULE_PROGRESS
                progressEntities.add(ModuleProgressEntity(newGame.game, it, DEFAULT_MODULE_PROGRESS))
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    gameStatsDao.insert(newGame)
                    moduleProgressDao.insert(progressEntities)
                }
            }.invokeOnCompletion {
                switchToGame(newGame, progress)
            }
        }
    }

    private fun switchToGame(game: GameStatsEntity, progress: MutableMap<ModuleType, Long>) {
        GameStats.init(game, progress)
        val intent = Intent(this, CampActivity::class.java)
        startActivity(intent)
    }
}