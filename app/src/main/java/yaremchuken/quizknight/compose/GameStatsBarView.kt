package yaremchuken.quizknight.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import yaremchuken.quizknight.R

data class GameStatsBarModel(val health: Long, val maxHealth: Long, val module: String, val gold: Long)

@Composable
fun GameStatsBar(stats: GameStatsBarModel) {
    Row {
        for (i in 0..stats.maxHealth) {
            val heartImg = if (stats.health > i) R.drawable.ui_heart_full else R.drawable.ui_heart_empty
            Image(bitmap = ImageBitmap.imageResource(heartImg), contentDescription = "")
        }
        Text(text = stats.module)
        Row {
            Image(bitmap = ImageBitmap.imageResource(R.drawable.ui_gold), contentDescription = "")
            Text(text = "${stats.gold}")
        }
    }
}