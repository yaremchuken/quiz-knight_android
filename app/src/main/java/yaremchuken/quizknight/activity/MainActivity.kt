package yaremchuken.quizknight.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.compose.GamesManagerView
import yaremchuken.quizknight.databinding.DialogCreateGameBinding
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.model.ModuleType
import yaremchuken.quizknight.provider.QuizzesProvider
import java.util.EnumMap
import java.util.Locale

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

    @SuppressLint("SetTextI18n")
    private fun initGames() {
        lifecycleScope.launch {
            var games: List<GameStatsEntity>
            withContext(Dispatchers.IO) {
                games = (application as App).db.getGameStatsDao().fetchAll()
            }
            gamesCount = games.size.toLong()
            setContent {
                GamesManagerView(activity = this@MainActivity, games = games)
            }
        }
    }

    fun gameEntityClickListener(game: GameStatsEntity?) {
        if (game != null) startGame(game) else createGameDialog()
    }

    private fun createGameDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogCreateGameBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnDone.setOnClickListener {
            if (dialogBinding.etGameName.text != null && dialogBinding.etGameName.text!!.isNotBlank()) {
                val gameStatsDao = (application as App).db.getGameStatsDao()
                val moduleProgressDao = (application as App).db.getModuleProgressDao()

                val original = toLocale(dialogBinding.rgOriginalGroup.checkedRadioButtonId)
                val studied = toLocale(dialogBinding.rgStudiedGroup.checkedRadioButtonId)

                val newGame = GameStatsEntity(
                    dialogBinding.etGameName.text.toString(), gamesCount, original, studied,
                    ModuleType.LAZYWOOD, 0, GameStats.maxHealth.toDouble())

                val progress: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
                val progressEntities = ArrayList<ModuleProgressEntity>()
                ModuleType.values().forEach {
                    progress[it] = DEFAULT_MODULE_PROGRESS
                    progressEntities.add(ModuleProgressEntity(newGame.game, it, DEFAULT_MODULE_PROGRESS))
                }

                lifecycleScope.launch {
                    gameStatsDao.insert(newGame)
                    moduleProgressDao.insert(progressEntities)
                }.invokeOnCompletion {
                    dialog.dismiss()
                    switchToGame(newGame, progress)
                }
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startGame(game: GameStatsEntity) {
        val progress: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                (application as App).db
                    .getModuleProgressDao()
                    .fetch(game.game)
                    .forEach { pr ->
                        progress[pr.module] = pr.progress
                    }
            }
        }.invokeOnCompletion {
            switchToGame(game, progress)
        }
    }

    private fun switchToGame(game: GameStatsEntity, progress: MutableMap<ModuleType, Long>) {
        GameStats.init(game, progress)
        val intent = Intent(this, CityActivity::class.java)
        startActivity(intent)
    }

    private fun toLocale(btnId: Int) = Locale(findViewById<RadioButton>(btnId).text.toString().lowercase())
}