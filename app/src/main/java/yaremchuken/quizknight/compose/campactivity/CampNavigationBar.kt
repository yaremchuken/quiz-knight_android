package yaremchuken.quizknight.compose.campactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.CampSceneType
import yaremchuken.quizknight.dimensions.UIDefaults
import yaremchuken.quizknight.dimensions.UIDimensions

@Preview
@Composable
fun CampNavigationBar(
    switchScene: (scene: CampSceneType) -> Unit = {},
    scene: CampSceneType = CampSceneType.CROSSROADS
) {
    Row (
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.palette_cb))
            .border(1.dp, colorResource(id = R.color.black)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CampSceneType.values().map { type ->
            val btnBG =
                when(type) {
                    CampSceneType.WORLDMAP -> R.drawable.camp_ui_worldmap
                    CampSceneType.CROSSROADS -> R.drawable.camp_ui_crossroads
                    CampSceneType.ALCHEMY -> R.drawable.camp_ui_alchemy
                    CampSceneType.BLACKSMITH -> R.drawable.camp_ui_blacksmith
                }

            val borderClr = if (scene == type) R.color.palette_6b else R.color.light_blue

            Image(
                bitmap = ImageBitmap.imageResource(btnBG),
                contentDescription = null,
                Modifier
                    .clickable { switchScene(type) }
                    .width(72.dp)
                    .padding(vertical = UIDimensions.PADDING_DEFAULT)
                    .border(2.dp, colorResource(id = borderClr), UIDefaults.ROUNDED_CORNER)
                    .background(colorResource(id = R.color.palette_dd), UIDefaults.ROUNDED_CORNER)
                    .clip(UIDefaults.ROUNDED_CORNER)
            )
        }
    }
}