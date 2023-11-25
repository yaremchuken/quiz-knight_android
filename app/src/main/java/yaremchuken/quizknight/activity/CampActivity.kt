package yaremchuken.quizknight.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.compose.GameStatsBarModel
import yaremchuken.quizknight.compose.campactivity.CampAlchemyView
import yaremchuken.quizknight.compose.campactivity.CampBlacksmithView
import yaremchuken.quizknight.compose.campactivity.CampCrossroadsView
import yaremchuken.quizknight.compose.campactivity.CampWorldMapView
import yaremchuken.quizknight.compose.campactivity.ModuleProgression
import yaremchuken.quizknight.dao.GameStatsDao
import yaremchuken.quizknight.model.ModuleType
import yaremchuken.quizknight.provider.QuizzesProvider
import java.util.EnumMap
import javax.inject.Inject

enum class CampSceneType {
    WORLDMAP,
    CROSSROADS,
    BLACKSMITH,
    ALCHEMY
}

class CampActivity : AppCompatActivity() {

    @Inject
    lateinit var gameStatsDao: GameStatsDao

    private var currentScene: CampSceneType = CampSceneType.CROSSROADS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }

    fun switchModule(moduleType: ModuleType) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                gameStatsDao.switchModule(GameStats.game, moduleType)
            }
        }.invokeOnCompletion {
            GameStats.switchModule(moduleType)
            switchScene(CampSceneType.CROSSROADS)
            refreshView()
        }
    }

    private fun refreshView() {
        // Total amount of levels in current module
        val moduleLevelsAmount: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
        QuizzesProvider.getAllLevels().forEach {
            moduleLevelsAmount[it.module] = (moduleLevelsAmount[it.module] ?: 0) + 1
        }

        val moduleProgression =
            moduleLevelsAmount.map {
                ModuleProgression(
                    it.key,
                    (GameStats.progress[it.key] ?: -1) + 1,
                    moduleLevelsAmount[it.key] ?: -1)
            }

        val gameStatsModel = GameStatsBarModel.mapGameStats(GameStats)

        setContent {
            when (currentScene) {
                CampSceneType.CROSSROADS ->
                    CampCrossroadsView(
                        this::switchScene,
                        this::launchLevel,
                        gameStatsModel,
                        currentScene,
                        QuizzesProvider.getModuleLevels(GameStats.module),
                        GameStats.progress[GameStats.module] ?: -1
                    )
                CampSceneType.WORLDMAP ->
                    CampWorldMapView(
                        this::switchScene,
                        this::switchModule,
                        GameStats.original,
                        gameStatsModel,
                        currentScene,
                        moduleProgression
                    )
                CampSceneType.BLACKSMITH ->
                    CampBlacksmithView(
                        this::switchScene,
                        gameStatsModel,
                        currentScene
                    )
                CampSceneType.ALCHEMY ->
                    CampAlchemyView(
                        this::switchScene,
                        gameStatsModel,
                        currentScene
                    )
            }
        }
    }

    private fun switchScene(scene: CampSceneType) {
        currentScene = scene
        refreshView()
    }

    fun launchLevel(levelIdx: Long) {
        GameStats.currentLevel = levelIdx
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }
}