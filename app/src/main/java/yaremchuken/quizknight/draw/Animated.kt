package yaremchuken.quizknight.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import yaremchuken.quizknight.utils.AssetsProvider
import java.util.EnumMap

open class Animated {

    private var animations: Map<AnimationType, Animation> = EnumMap(AnimationType::class.java)

    var xPos: Float = 0F

    protected lateinit var dimensions: Point
    protected lateinit var sceneDims: Point

    fun init(context: Context, sceneDims: Point, char: String) {
        this.sceneDims = sceneDims
        AssetsProvider.getAnimations(context, char).forEach { t, u ->
            (animations as EnumMap<AnimationType, Animation>)[t] = Animation(u)
        }
        dimensions = animations[AnimationType.IDLE]!!.getDimensions()
    }

    fun draw(canvas: Canvas, viewHeight: Int) {
        val frame = animations[AnimationType.IDLE]!!.getFrame()
        canvas.drawBitmap(frame, xPos, (viewHeight - frame.height - 50).toFloat(), null)
    }
}