package yaremchuken.quizknight.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.R
import yaremchuken.quizknight.adapter.CampCrossroadsAdapter
import yaremchuken.quizknight.adapter.CampWorldmapAdapter
import yaremchuken.quizknight.adapter.HealthBarAdapter
import yaremchuken.quizknight.databinding.ActivityCampBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.model.ModuleType
import yaremchuken.quizknight.provider.QuizzesProvider
import java.util.EnumMap

enum class CampSceneType {
    WORLDMAP,
    CROSSROADS,
    BLACKSMITH,
    ALCHEMY
}

class CampActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCampBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding

    private lateinit var currentScene: CampSceneType

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityCampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSceneSwitchers()

        binding.rvWorldmapMarkers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCrossroadsLevels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        gameStatsBarBinding = FragmentGameStatsBarBinding.inflate(layoutInflater)
        binding.llCampTop.addView(gameStatsBarBinding.root)
        gameStatsBarBinding.tvModuleName.text = GameStats.module.name

        switchScene(CampSceneType.CROSSROADS)
    }

    override fun onResume() {
        super.onResume()
        if (GameStats.currentLevel != -1L) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    (application as App)
                        .db
                        .getModuleProgressDao()
                        .updateProgress(
                            GameStats.game,
                            GameStats.module,
                            GameStats.currentLevel
                        )
                }
                GameStats.updateProgress()
            }.invokeOnCompletion {
                GameStats.currentLevel = -1
                switchScene(CampSceneType.CROSSROADS)
            }
        }

        updateHealthBar()
        updateGold()
    }

    private fun updateHealthBar() {
        val healths: ArrayList<Boolean> = ArrayList(GameStats.maxHealth.toInt())
        for (i in 0 until GameStats.maxHealth) {
            healths.add(i < GameStats.health)
        }
        gameStatsBarBinding.rvHearts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        gameStatsBarBinding.rvHearts.adapter = HealthBarAdapter(healths)
    }

    private fun updateGold() {
        gameStatsBarBinding.tvGold.text = GameStats.gold.toString()
    }

    fun switchModule(moduleType: ModuleType) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                (application as App)
                    .db
                    .getGameStatsDao()
                    .switchModule(GameStats.game, moduleType)
            }
        }.invokeOnCompletion {
            GameStats.switchModule(moduleType)
            gameStatsBarBinding.tvModuleName.text = moduleType.name
            switchScene(CampSceneType.CROSSROADS)
        }
    }

    private fun initSceneSwitchers() {
        binding.ibCampWorldmap.setOnClickListener {
            switchScene(CampSceneType.WORLDMAP)
        }
        binding.ibCampCrossroads.setOnClickListener {
            switchScene(CampSceneType.CROSSROADS)
        }
        binding.ibCampBlacksmith.setOnClickListener {
            switchScene(CampSceneType.BLACKSMITH)
        }
        binding.ibCampAlchemy.setOnClickListener {
            switchScene(CampSceneType.ALCHEMY)
        }
    }

    private fun switchScene(scene: CampSceneType) {
        currentScene = scene
        when(scene) {
            CampSceneType.CROSSROADS -> {
                binding.rvCrossroadsLevels.adapter =
                    CampCrossroadsAdapter(this@CampActivity, QuizzesProvider.getModuleLevels(GameStats.module))
            }
            CampSceneType.WORLDMAP -> {
                lifecycleScope.launch {
                    val modulesLevelsCounter: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
                    QuizzesProvider.getAllLevels().forEach {
                        modulesLevelsCounter[it.module] = (modulesLevelsCounter[it.module] ?: 0) + 1
                    }
                    binding.rvWorldmapMarkers.adapter =
                        CampWorldmapAdapter(
                            this@CampActivity, modulesLevelsCounter.keys.toList().sorted(), modulesLevelsCounter)
                }
            }
            else -> {}
        }

        binding.rvWorldmapMarkers.visibility = if (scene == CampSceneType.WORLDMAP) View.VISIBLE else View.GONE
        binding.rvCrossroadsLevels.visibility = if (scene == CampSceneType.CROSSROADS) View.VISIBLE else View.GONE

        adjustBackground()
        adjustSceneSwitchers()
    }

    private fun adjustBackground() {
        binding.ivCampBackground.setImageResource(
            when (currentScene) {
                CampSceneType.WORLDMAP -> R.drawable.camp_worldmap
                CampSceneType.CROSSROADS -> R.drawable.camp_crossroads
                CampSceneType.BLACKSMITH -> R.drawable.camp_blacksmith
                CampSceneType.ALCHEMY -> R.drawable.camp_alchemy
            }
        )
    }

    private fun adjustSceneSwitchers() {
        binding.ibCampWorldmap.alpha = if (currentScene == CampSceneType.WORLDMAP) 1f else .5f
        binding.ibCampCrossroads.alpha = if (currentScene == CampSceneType.CROSSROADS) 1f else .5f
        binding.ibCampBlacksmith.alpha = if (currentScene == CampSceneType.BLACKSMITH) 1f else .5f
        binding.ibCampAlchemy.alpha = if (currentScene == CampSceneType.ALCHEMY) 1f else .5f
    }

    fun launchLevel(levelIdx: Long) {
        GameStats.currentLevel = levelIdx
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }
}