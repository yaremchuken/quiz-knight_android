package yaremchuken.quizknight.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import yaremchuken.quizknight.R
import yaremchuken.quizknight.dimensions.FontDimensions
import yaremchuken.quizknight.dimensions.UIDimensions

@Preview
@Composable
fun GameStatsBar(
    stats: GameStatsBarModel = GameStatsBarModel(1.4, 3, "Lazywood", 120)
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(all = UIDimensions.PADDING_DEFAULT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            for (i in 0 until stats.maxHealth) {
                val heartImg = if (stats.health.toLong() > i) R.drawable.ui_heart_full else R.drawable.ui_heart_empty
                Image(
                    bitmap = ImageBitmap.imageResource(heartImg),
                    contentDescription = null,
                    Modifier.height(20.dp).padding(end = UIDimensions.PADDING_TINY)
                )
            }
        }
        Text(
            text = stats.name,
            color = colorResource(id = R.color.text_primary),
            fontSize = FontDimensions.LARGE,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium.copy(
                shadow = Shadow(
                    color = colorResource(id = R.color.light_blue),
                    blurRadius = 3F
                )
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = ImageBitmap.imageResource(R.drawable.ui_gold),
                contentDescription = null,
                Modifier.height(20.dp).padding(end = 10.dp)
            )
            Text(
                text = "${stats.gold}",
                color = colorResource(id = R.color.gold),
                fontSize = FontDimensions.LARGE,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = Shadow(
                        color = colorResource(id = R.color.black),
                        offset = Offset(2F, 2F),
                        blurRadius = 2F
                    )
                )
            )
        }
    }
}