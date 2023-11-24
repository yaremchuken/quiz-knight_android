package yaremchuken.quizknight.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.annotation.DimenRes
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import yaremchuken.quizknight.R

object UIUtils {
    fun convertDpToPixel(dp: Float, context: Context): Float =
        dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

    fun convertPixelsToDp(pixels: Float, context: Context): Float =
        pixels / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}