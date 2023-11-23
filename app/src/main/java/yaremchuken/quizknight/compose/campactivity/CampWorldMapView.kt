package yaremchuken.quizknight.compose.campactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.CampSceneType
import yaremchuken.quizknight.compose.GameStatsBar
import yaremchuken.quizknight.compose.GameStatsBarModel
import yaremchuken.quizknight.model.ModuleType
import java.util.Locale

data class ModuleProgression(val module: ModuleType, val completed: Long, val total: Long)

@Preview
@Composable
fun CampWorldMapView(
    switchScene: (scene: CampSceneType) -> Unit = {},
    switchModule: (type: ModuleType) -> Unit = {},
    locale: Locale = Locale.ENGLISH,
    gameStats: GameStatsBarModel = gameStatsStub,
    scene: CampSceneType = CampSceneType.WORLDMAP,
    modules: List<ModuleProgression> = listOf(moduleProgressionStub, moduleProgressionStub)
) {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.camp_worldmap),
                contentScale = ContentScale.FillHeight
            )
    ) {
        GameStatsBar(gameStats)
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                modules.forEach {
                    ModuleSelectionButton(
                        { switchModule(it.module) },
                        ModuleType.title(it.module, locale),
                        ModuleType.description(it.module, locale),
                        it.completed,
                        it.total
                    )
                }
            }
            CampNavigationBar(switchScene, scene)
        }
    }
}

val moduleProgressionStub = ModuleProgression(ModuleType.LAZYWOOD, 2, 12)