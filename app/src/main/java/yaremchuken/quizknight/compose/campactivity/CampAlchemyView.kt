package yaremchuken.quizknight.compose.campactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Preview
@Composable
fun CampAlchemyView(
    switchScene: (scene: CampSceneType) -> Unit = {},
    gameStats: GameStatsBarModel = gameStatsStub,
    scene: CampSceneType = CampSceneType.ALCHEMY
) {
    Column(
        Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.camp_alchemy),
                contentScale = ContentScale.FillHeight
            )
    ) {
        GameStatsBar(gameStats)
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier.fillMaxWidth()
            ) {
                // Placeholder
            }
            CampNavigationBar(switchScene, scene)
        }
    }
}