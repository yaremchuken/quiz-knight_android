package yaremchuken.quizknight.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.R
import yaremchuken.quizknight.databinding.ActivityMainBinding
import yaremchuken.quizknight.databinding.ButtonGameStartBinding
import yaremchuken.quizknight.databinding.DialogCreateGameBinding
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.model.Language
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.model.ModuleType
import yaremchuken.quizknight.providers.QuizzesProvider
import java.util.EnumMap

const val MAX_GAMES = 4

const val DEFAULT_MODULE_PROGRESS = -1L

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        QuizzesProvider.preload(this)
    }

    override fun onResume() {
        super.onResume()
        initializeGameButtons()
    }

    private fun initializeGameButtons() {
        lifecycleScope.launch {
            binding.buttonsHolder.removeAllViews()
            val games = (application as App).db.getGameStatsDao().fetchAll()
            games.forEach { game ->
                val gameBtn = ButtonGameStartBinding.inflate(layoutInflater)
                gameBtn.root.text = "${game.game} - ${game.original}/${game.studied}"
                gameBtn.root.setOnClickListener {
                    startGame(game)
                }
                binding.buttonsHolder.addView(gameBtn.root)
            }
            for(i in games.size until  MAX_GAMES) {
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
            (application as App).db
                .getModuleProgressDao()
                .fetch(game.game)
                .forEach { pr ->
                    progress[pr.module] = pr.progress
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
}