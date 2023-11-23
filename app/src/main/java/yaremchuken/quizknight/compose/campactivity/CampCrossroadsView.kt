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
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.CampSceneType
import yaremchuken.quizknight.compose.GameStatsBar
import yaremchuken.quizknight.compose.GameStatsBarModel
import yaremchuken.quizknight.model.ModuleLevel
import yaremchuken.quizknight.model.ModuleType

@Preview
@Composable
fun CampCrossroadsView(
    switchScene: (scene: CampSceneType) -> Unit = {},
    launchLevel: (levelIdx: Long) -> Unit = {},
    gameStats: GameStatsBarModel = gameStatsStub,
    scene: CampSceneType = CampSceneType.CROSSROADS,
    levels: List<ModuleLevel> = listOf(moduleLevelStub, moduleLevelStub, moduleLevelStub),
    progress: Long = 0
) {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.camp_crossroads),
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
                levels.forEachIndexed { idx, it ->
                    LevelSelectionButton(
                        { launchLevel(idx.toLong()) },
                        it.title,
                        it.opponents.first(),
                        idx <= progress,
                        idx > progress+1
                    )
                }
            }
            CampNavigationBar(switchScene, scene)
        }
    }
}

val gameStatsStub = GameStatsBarModel(1.4, 3, "Lazywood", 120)

val moduleLevelStub =
    ModuleLevel.builder(
        ModuleType.LAZYWOOD,
        "Adventure is magnificent",
        listOf(PersonageType.PEASANT),
        listOf(),
        100)