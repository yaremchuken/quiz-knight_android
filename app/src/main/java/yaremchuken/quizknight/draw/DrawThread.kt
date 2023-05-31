package yaremchuken.quizknight.draw

import android.graphics.Canvas
import android.view.SurfaceHolder
import kotlin.math.roundToLong


class DrawThread(private val surface: SurfaceHolder, private val drawView: DrawView): Thread() {
    companion object {
        const val FPS = 12L
    }

    private val frameRate: Long = (1000.0 / FPS).roundToLong()

    var running: Boolean = false

    override fun run() {
        var canvas: Canvas
        while (running) {
            canvas = surface.lockCanvas()
            synchronized(surface) {
                drawView.draw(canvas)
            }
            surface.unlockCanvasAndPost(canvas)
            sleep(frameRate)
        }
    }
}