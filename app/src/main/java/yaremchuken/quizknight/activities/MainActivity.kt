package yaremchuken.quizknight.activities

import android.annotation.SuppressLint
import android.app.Dialog
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
import yaremchuken.quizknight.databinding.DialogCreateGameBinding
import yaremchuken.quizknight.dummy.DummyDataProvider
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.Language
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.entity.ModuleType
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

        initializeDictionaries()

        val gameStatsDao = (application as App).db.getGameStatsDao()
        lifecycleScope.launch {
            gameStatsDao.fetchAll().collect {
                it.forEach { game ->
                    val gameBtn = ButtonGameStartBinding.inflate(layoutInflater)
                    gameBtn.root.text = "${game.game} - ${game.original}/${game.studied}"
                    gameBtn.root.setOnClickListener {
                        startGame(game)
                    }
                    binding.buttonsHolder.addView(gameBtn.root)
                }
                for(i in it.size until  MAX_GAMES) {
                    val gameBtn = ButtonGameStartBinding.inflate(layoutInflater)
                    gameBtn.root.text = resources.getString(R.string.new_game_btn_title)
                    gameBtn.root.setOnClickListener {
                        createGameDialog(i.toLong())
                    }
                    gameBtn.root.setBackgroundColor(resources.getColor(R.color.palette_6b))
                    binding.buttonsHolder.addView(gameBtn.root)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.buttonsHolder.visibility = View.VISIBLE
    }

    private fun createGameDialog(idx: Long) {
        val dialog = Dialog(this)
        val dialogBinding = DialogCreateGameBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnDone.setOnClickListener {
            if (dialogBinding.etGameName.text != null && dialogBinding.etGameName.text!!.isNotBlank()) {
                val gameStatsDao = (application as App).db.getGameStatsDao()
                val moduleProgressDao = (application as App).db.getModuleProgressDao()

                val original = Language.RU
                val learned = Language.EN

                val newGame = GameStatsEntity(
                    dialogBinding.etGameName.text.toString(), idx, original, learned,
                    ModuleType.LAZYWOOD, 0, GameStats.getInstance().maxHealth.toDouble())

                val progress: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
                val progressEntities = ArrayList<ModuleProgressEntity>()
                ModuleType.values().forEach {
                    progress[it] = 0
                    progressEntities.add(ModuleProgressEntity(newGame.game, it, 0))
                }

                lifecycleScope.launch {
                    gameStatsDao.insert(newGame)
                    moduleProgressDao.insert(progressEntities)
                }.invokeOnCompletion {
                    binding.buttonsHolder.visibility = View.INVISIBLE
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
        val moduleProgressDao = (application as App).db.getModuleProgressDao()

        binding.buttonsHolder.visibility = View.INVISIBLE

        lifecycleScope.launch {
            moduleProgressDao.fetch(game.game).collect {
                it.forEach { pr ->
                    progress[pr.module] = pr.progress
                }
                switchToGame(game, progress)
            }
        }
    }

    private fun switchToGame(game: GameStatsEntity, progress: MutableMap<ModuleType, Long>) {
        GameStats.getInstance().init(game, progress)
        val intent = Intent(this@MainActivity, CityActivity::class.java)
        startActivity(intent)
    }

    private fun initializeDictionaries() {
        val levelDao = (application as App).db.getModuleLevelDao()
        val quizDao = (application as App).db.getQuizTaskDao()
        lifecycleScope.launch {
            levelDao.fetchAll().collect {
                if (it.isEmpty()) {
                    levelDao.insert(DummyDataProvider.dummyLevels())
                    quizDao.insert(DummyDataProvider.dummyQuizzes())
                }
            }
        }
    }
}