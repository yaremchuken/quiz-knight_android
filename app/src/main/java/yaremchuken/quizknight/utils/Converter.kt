package yaremchuken.quizknight.utils

import android.content.Context
import android.util.DisplayMetrics

object Converter {
    fun convertDpToPixel(dp: Float, context: Context): Float =
        dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

    fun convertPixelsToDp(pixels: Float, context: Context): Float =
        pixels / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}