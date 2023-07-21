package yaremchuken.quizknight.draw

import android.graphics.Bitmap
import android.graphics.Point

class Animation(private val frames: List<Bitmap>) {
    private var frameIdx: Int = 0

    fun getDimensions() = Point(frames[0].width, frames[0].height)

    fun reset() {
        frameIdx = 0
    }

    fun getFrame(): Bitmap {
        frameIdx++
        if (frameIdx >= frames.size) frameIdx = 0
        return frames[frameIdx]
    }
}