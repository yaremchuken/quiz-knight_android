package yaremchuken.quizknight.compose.campactivity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import yaremchuken.quizknight.R
import yaremchuken.quizknight.dimensions.FontDimensions
import yaremchuken.quizknight.dimensions.UIDefaults
import yaremchuken.quizknight.dimensions.UIDimensions

@Preview
@Composable
fun ModuleSelectionButton(
    onClick: () -> Unit = {},
    title: String = "Lazywood",
    description: String = "Starting module to understand how it works.",
    completed: Long = 2,
    total: Long = 12
) {
    Column(
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = UIDimensions.PADDING_DEFAULT, vertical = UIDimensions.PADDING_SMALL)
            .background(colorResource(R.color.palette_a5), UIDefaults.ROUNDED_CORNER)
            .border(1.dp, colorResource(R.color.white), UIDefaults.ROUNDED_CORNER)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = UIDimensions.PADDING_DEFAULT,
                    top = UIDimensions.PADDING_DEFAULT,
                    end = UIDimensions.PADDING_DEFAULT,
                    bottom = UIDimensions.PADDING_SMALL
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = FontDimensions.LARGE,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white)
            )
            Text(
                text = "$completed / $total",
                fontSize = FontDimensions.LARGE,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.yellow)
            )
        }
        Divider(
            Modifier.padding(horizontal = UIDimensions.PADDING_DEFAULT)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = UIDimensions.PADDING_DEFAULT, vertical = UIDimensions.PADDING_SMALL)
        ) {
            Text(
                text = description,
                fontSize = FontDimensions.SMALL_X,
                color = colorResource(id = R.color.light_blue)
            )
        }
    }
}