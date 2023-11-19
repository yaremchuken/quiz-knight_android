package yaremchuken.quizknight.fragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.fragment.app.Fragment
import yaremchuken.quizknight.R

data class StatsBarModel(val health: Int, val maxHealth: Int, val location: String, val gold: Int)

class GameStatsBarFragment: Fragment() {
    @Composable
    fun StatsBar(stats: StatsBarModel) {
        Row {
            for (i in 0..stats.maxHealth) {
                Image(bitmap = ImageBitmap.imageResource(R.drawable.ui_heart_full), contentDescription = "")
            }
            Text(text = stats.location)
            Row {
                Image(bitmap = ImageBitmap.imageResource(R.drawable.ui_gold), contentDescription = "")
                Text(text = "${stats.gold}")
            }
        }
    }
}