package yaremchuken.quizknight.compose.campactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.R
import yaremchuken.quizknight.dimensions.FontDimensions
import yaremchuken.quizknight.dimensions.UIDefaults

@Preview
@Composable
fun LevelSelectionButton(
    onClick: () -> Unit = {},
    title: String = "Adventure starts here",
    boss: PersonageType = PersonageType.PEASANT,
    completed: Boolean = false,
    disabled: Boolean = true
) {
    val portrait =
        when (boss) {
            PersonageType.HERO -> -1
            PersonageType.GOBLIN -> R.drawable.ic_portrait_goblin
            PersonageType.PEASANT -> R.drawable.ic_portrait_peasant
        }

    val borderClr =
        if (disabled) colorResource(R.color.dark_gray)
        else if (completed) colorResource(R.color.yellow)
        else colorResource(R.color.white)

    val bgClr =
        if (disabled) colorResource(R.color.palette_6b)
        else if (completed) colorResource(R.color.palette_cb)
        else colorResource(R.color.button_primary)

    Row(
        Modifier
            .clickable { if (!disabled) onClick() }
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_default),
                vertical = dimensionResource(R.dimen.padding_small)
            )
            .background(bgClr, UIDefaults.ROUNDED_CORNER)
            .border(1.dp, borderClr, UIDefaults.ROUNDED_CORNER)
            .alpha(if (disabled) .2F else 1F)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (completed) {
                    Image(
                        bitmap = ImageBitmap.imageResource(R.drawable.ic_module_level_completed),
                        contentDescription = null,
                        Modifier.height(42.dp)
                    )
                }
                Text(
                    text = title,
                    Modifier.padding(start = if (completed) 0.dp else dimensionResource(R.dimen.padding_default)),
                    fontSize = FontDimensions.MEDIUM,
                    color = colorResource(R.color.white)
                )
            }
            Image(
                bitmap = ImageBitmap.imageResource(portrait),
                contentDescription = null,
                Modifier.padding(end = dimensionResource(R.dimen.padding_small))
            )
        }
    }
}