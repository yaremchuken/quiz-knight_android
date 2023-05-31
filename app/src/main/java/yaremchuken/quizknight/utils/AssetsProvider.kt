package yaremchuken.quizknight.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import yaremchuken.quizknight.draw.AnimationType

object AssetsProvider {

    fun getBitmap(context: Context, filePath: String): Bitmap =
        BitmapFactory.decodeStream(context.assets.open(filePath))

    fun getAnimations(context: Context, char: String): Map<AnimationType, List<Bitmap>> {
        val idles: ArrayList<Bitmap> = ArrayList()
        context.assets.list("animations/$char")?.forEach {
            if (it.startsWith("idle")) {
                idles.add(getBitmap(context, "animations/$char/$it"))
            }
        }

        return mapOf(
            Pair(AnimationType.IDLE, idles)
        )
    }
}