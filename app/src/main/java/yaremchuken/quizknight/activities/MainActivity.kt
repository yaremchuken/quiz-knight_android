package yaremchuken.quizknight.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.R
import yaremchuken.quizknight.databinding.ActivityMainBinding
import yaremchuken.quizknight.databinding.ButtonGameStartBinding
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.Language
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.entity.ModuleType
import yaremchuken.quizknight.entity.QuizTaskEntity
import yaremchuken.quizknight.entity.QuizType
import java.util.EnumMap

// FIXME: Canvas is blacked out when app is suspends (when middle btn clicked)

// TODO: Hide gamestats bar on keyboard appear

const val MAX_GAMES = 4

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeQuizzes()

        val gameStatsDao = (application as App).db.getGameStatsDao()
        lifecycleScope.launch {
            gameStatsDao.fetchAll().collect {
                it.forEach { game ->
                    val gameBtn = ButtonGameStartBinding.inflate(layoutInflater)
                    gameBtn.root.text = "${game.game} - ${game.studied}"
                    gameBtn.root.setOnClickListener {
                        startGame(game)
                    }
                    binding.buttonsHolder.addView(gameBtn.root)
                }
                for(i in it.size until  MAX_GAMES) {
                    val gameBtn = ButtonGameStartBinding.inflate(layoutInflater)
                    gameBtn.root.text = resources.getString(R.string.new_game_btn_title)
                    gameBtn.root.setOnClickListener {
                        startGame(null, i.toLong())
                    }
                    binding.buttonsHolder.addView(gameBtn.root)
                }
            }
        }
    }

    private fun startGame(game: GameStatsEntity?, order: Long? = null) {
        val progress: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
        val moduleProgressDao = (application as App).db.getModuleProgressDao()

        binding.buttonsHolder.visibility = View.INVISIBLE

        if (game == null) {
            val gameStatsDao = (application as App).db.getGameStatsDao()

            val newGame = GameStatsEntity(
                "CALL ME $order", order!!, Language.RU, Language.EN, 0,
                ModuleType.LAZYWOOD, GameStats.getInstance().maxHealth.toDouble())

            val progressEntities = ArrayList<ModuleProgressEntity>()
            ModuleType.values().forEach {
                progress[it] = 0
                progressEntities.add(ModuleProgressEntity(newGame.game, newGame.module, 0))
            }

            lifecycleScope.launch {
                gameStatsDao.insert(newGame)
                moduleProgressDao.insert(progressEntities)
            }

            switchToGame(newGame, progress)
        } else {
            lifecycleScope.launch {
                moduleProgressDao.fetch(game.game).collect {
                    it.forEach { pr ->
                        progress[pr.module] = pr.progress
                    }
                    switchToGame(game, progress)
                }
            }
        }
    }

    private fun switchToGame(game: GameStatsEntity, progress: MutableMap<ModuleType, Long>) {
        GameStats.getInstance().init(game, progress)
        val intent = Intent(this@MainActivity, QuizActivity::class.java)
        startActivity(intent)
    }

    private fun initializeQuizzes() {
        val dao = (application as App).db.getQuizTaskDao()
        lifecycleScope.launch {
            dao.fetchAll().collect {
                if (it.isEmpty()) {
                    dao.insert(dummyQuizzes())
                }
            }
        }
    }

    private fun dummyQuizzes() =
        listOf(
            QuizTaskEntity(
                ModuleType.LAZYWOOD, 1, 1,
                QuizType.ASSEMBLE_TRANSLATION_STRING,
                "Ты смотрела тот фильм вчера?",
                listOf("will", "tomorrow", "this"),
                listOf("did you watch that movie yesterday"),
                5
            ),
            QuizTaskEntity(
                ModuleType.LAZYWOOD, 1, 2,
                QuizType.INPUT_LISTENED_WORD_IN_STRING,
                "I clean this machine every day",
                listOf("I clean this <answer> every day"),
                listOf("machine"),
                5
            ),
            QuizTaskEntity(
                ModuleType.LAZYWOOD, 1, 3,
                QuizType.CHOOSE_CORRECT_OPTION,
                "If you go on ........ me like this, i will never be able to finish writing my report.",
                listOf("disturbing", "afflicting", "concerning", "affecting"),
                listOf("disturbing"),
                5
            ),
            QuizTaskEntity(
                ModuleType.LAZYWOOD, 1, 4,
                QuizType.WRITE_LISTENED_PHRASE,
                "Let's go play in the yard",
                listOf(),
                listOf("let's go play in the yard", "lets go play in the yard", "let us go play in the yard"),
                5
            ),
            QuizTaskEntity(
                ModuleType.LAZYWOOD, 1, 5,
                QuizType.WORD_TRANSLATION_INPUT,
                "Мой босс любит приходить на работу утром.",
                listOf("My boss <answer> to come to work in the morning."),
                listOf("likes", "loves"),
                5
            )
        )
}