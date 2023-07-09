package yaremchuken.quizknight.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.R
import yaremchuken.quizknight.adapters.CityCrossroadsAdapter
import yaremchuken.quizknight.adapters.CityWorldmapAdapter
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.databinding.ActivityCityBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.entity.ModuleLevelEntity
import yaremchuken.quizknight.entity.ModuleType
import yaremchuken.quizknight.utils.Tuple
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

        val modulesData: HashMap<ModuleType, Long> = HashMap()
        lifecycleScope.launch {
            (application as App).db
                .getModuleLevelDao()
                .fetchAll()
                .collect {
                    it.forEach { entity ->
                        modulesData[entity.module] = (modulesData[entity.module] ?: 0) + 1
                    }
                    binding.rvWorldmapMarkers.adapter =
                        CityWorldmapAdapter(this@CityActivity, modulesData.keys.toList(), modulesData)
                }
        }

        binding.rvCrossroadsLevels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        switchScene(CitySceneType.CROSSROADS)

        gameStatsBarBinding = FragmentGameStatsBarBinding.inflate(layoutInflater)
        binding.llCityTop.addView(gameStatsBarBinding.root)
        gameStatsBarBinding.tvModuleName.text = GameStats.getInstance().module.name

        updateHealthBar()
        updateGold()
    }

    private fun updateHealthBar() {
        val healths: ArrayList<Boolean> = ArrayList(GameStats.getInstance().maxHealth.toInt())
        for (i in 0 until GameStats.getInstance().maxHealth) {
            healths.add(i < GameStats.getInstance().health)
        }
        gameStatsBarBinding.rvHearts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        gameStatsBarBinding.rvHearts.adapter = HealthBarAdapter(healths)
    }

    private fun updateGold() {
        gameStatsBarBinding.tvGold.text = GameStats.getInstance().gold.toString()
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

        if (scene == CitySceneType.CROSSROADS) {
            val levelDao = (application as App).db.getModuleLevelDao()
            lifecycleScope.launch {
                levelDao.fetch(GameStats.getInstance().module).collect {
                    binding.rvCrossroadsLevels.adapter = CityCrossroadsAdapter(this@CityActivity, it)
                }
            }
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

    fun switchModule(moduleType: ModuleType) {
        val gameStatsDao = (application as App).db.getGameStatsDao()

        lifecycleScope.launch {
            gameStatsDao.switchModule(GameStats.getInstance().game, moduleType)
        }.invokeOnCompletion {
            GameStats.getInstance().switchModule(moduleType)
            gameStatsBarBinding.tvModuleName.text = moduleType.name
            switchScene(CitySceneType.CROSSROADS)
        }
    }

    fun launchLevel(level: ModuleLevelEntity) {
        GameStats.getInstance().currentLevel = level.order
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }
}