package yaremchuken.quizknight.activities

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.R
import yaremchuken.quizknight.adapters.CityCrossroadsAdapter
import yaremchuken.quizknight.adapters.CityWorldmapAdapter
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.databinding.ActivityCityBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.model.ModuleType
import yaremchuken.quizknight.providers.QuizzesProvider
import java.util.EnumMap

enum class CitySceneType {
    WORLDMAP,
    CROSSROADS,
    BLACKSMITH,
    ALCHEMY
}

class CityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding

    private lateinit var currentScene: CitySceneType

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityCityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSceneSwitchers()

        binding.rvWorldmapMarkers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCrossroadsLevels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        gameStatsBarBinding = FragmentGameStatsBarBinding.inflate(layoutInflater)
        binding.llCityTop.addView(gameStatsBarBinding.root)
        gameStatsBarBinding.tvModuleName.text = GameStats.module.name

        updateHealthBar()
        updateGold()

        switchScene(CitySceneType.CROSSROADS)
    }

    override fun onResume() {
        super.onResume()
        if (GameStats.currentLevel != -1L) {
            lifecycleScope.launch {
                val dao = (application as App).db.getModuleProgressDao()
                dao.updateProgress(
                    GameStats.game,
                    GameStats.module,
                    GameStats.currentLevel
                )
                GameStats.updateProgress()
            }.invokeOnCompletion {
                GameStats.currentLevel = -1
                switchScene(CitySceneType.CROSSROADS)
            }
        }
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
        val gameStatsDao = (application as App).db.getGameStatsDao()

        lifecycleScope.launch {
            gameStatsDao.switchModule(GameStats.game, moduleType)
        }.invokeOnCompletion {
            GameStats.switchModule(moduleType)
            gameStatsBarBinding.tvModuleName.text = moduleType.name
            switchScene(CitySceneType.CROSSROADS)
        }
    }

    private fun initSceneSwitchers() {
        binding.ibCityWorldmap.setOnClickListener {
            switchScene(CitySceneType.WORLDMAP)
        }
        binding.ibCityCrossroads.setOnClickListener {
            switchScene(CitySceneType.CROSSROADS)
        }
        binding.ibCityBlacksmith.setOnClickListener {
            switchScene(CitySceneType.BLACKSMITH)
        }
        binding.ibCityAlchemy.setOnClickListener {
            switchScene(CitySceneType.ALCHEMY)
        }
    }

    private fun switchScene(scene: CitySceneType) {
        currentScene = scene
        findViewById<RelativeLayout>(R.id.llCityTop).performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

        when(scene) {
            CitySceneType.CROSSROADS -> {
                binding.rvCrossroadsLevels.adapter =
                    CityCrossroadsAdapter(this@CityActivity, QuizzesProvider.getModuleLevels(GameStats.module))
            }
            CitySceneType.WORLDMAP -> {
                lifecycleScope.launch {
                    val modulesLevelsCounter: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
                    QuizzesProvider.getAllLevels().forEach {
                        modulesLevelsCounter[it.module] = (modulesLevelsCounter[it.module] ?: 0) + 1
                    }
                    binding.rvWorldmapMarkers.adapter =
                        CityWorldmapAdapter(
                            this@CityActivity, modulesLevelsCounter.keys.toList().sorted(), modulesLevelsCounter)
                }
            }
            else -> {}
        }

        binding.rvWorldmapMarkers.visibility = if (scene == CitySceneType.WORLDMAP) View.VISIBLE else View.GONE
        binding.rvCrossroadsLevels.visibility = if (scene == CitySceneType.CROSSROADS) View.VISIBLE else View.GONE

        adjustBackground()
        adjustSceneSwitchers()
    }

    private fun adjustBackground() {
        binding.ivCityBackground.setImageResource(
            when (currentScene) {
                CitySceneType.WORLDMAP -> R.drawable.city_worldmap
                CitySceneType.CROSSROADS -> R.drawable.city_crossroads
                CitySceneType.BLACKSMITH -> R.drawable.city_blacksmith
                CitySceneType.ALCHEMY -> R.drawable.city_alchemy
            }
        )
    }

    private fun adjustSceneSwitchers() {
        binding.ibCityWorldmap.alpha = if (currentScene == CitySceneType.WORLDMAP) 1f else .5f
        binding.ibCityCrossroads.alpha = if (currentScene == CitySceneType.CROSSROADS) 1f else .5f
        binding.ibCityBlacksmith.alpha = if (currentScene == CitySceneType.BLACKSMITH) 1f else .5f
        binding.ibCityAlchemy.alpha = if (currentScene == CitySceneType.ALCHEMY) 1f else .5f
    }

    fun launchLevel(levelIdx: Long) {
        GameStats.currentLevel = levelIdx
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }
}