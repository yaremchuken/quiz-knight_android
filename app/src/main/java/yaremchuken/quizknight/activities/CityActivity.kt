package yaremchuken.quizknight.activities

import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
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
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.adapters.WorldmapAdapter
import yaremchuken.quizknight.databinding.ActivityCityBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.entity.ModuleType

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

        switchScene(CitySceneType.WORLDMAP)

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

        binding.rvWorldmapMarkers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvWorldmapMarkers.adapter = WorldmapAdapter(this, listOf(ModuleType.LAZYWOOD, ModuleType.CANDYVALE))

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

    private fun switchScene(scene: CitySceneType) {
        currentScene = scene
        findViewById<RelativeLayout>(R.id.llCityTop).performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

        binding.rvWorldmapMarkers.visibility = if (scene == CitySceneType.WORLDMAP) View.VISIBLE else View.INVISIBLE

        adjustBackground()
        adjustButtons()
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

    private fun adjustButtons() {
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
}