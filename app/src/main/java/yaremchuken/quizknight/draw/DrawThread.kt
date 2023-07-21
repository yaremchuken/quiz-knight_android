package yaremchuken.quizknight.draw

import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import java.lang.Exception
import kotlin.math.roundToLong


class DrawThread(private val surface: SurfaceHolder, private val drawView: DrawView): Thread() {
    companion object {
        const val FPS = 24L
    }

    private val frameRate: Long = (1000.0 / FPS).roundToLong()

    var running: Boolean = false

    override fun run() {
        var canvas: Canvas
        while (running) {
            try {
                canvas = surface.lockCanvas()
                synchronized(surface) {
                    drawView.draw(canvas)
                }
                surface.unlockCanvasAndPost(canvas)
            } catch (e: Exception) {
                Log.e("Draw Thread", "Unable to lock canvas ${e.message}")
            }
            sleep(frameRate)
        }
    }
}