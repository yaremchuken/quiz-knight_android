package yaremchuken.quizknight.draw

import android.graphics.Canvas
import android.graphics.Point
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.provider.AnimationProvider
import java.util.EnumMap

open class Animated {

    private var animations: Map<ActionType, Animation> = EnumMap(ActionType::class.java)

    var xPos: Float = 0F

    protected lateinit var dimensions: Point
    protected lateinit var sceneDims: Point

    private lateinit var actionType: ActionType

    fun init(personage: PersonageType, sceneDims: Point) {
        this.sceneDims = sceneDims
        animations = AnimationProvider.getAnimation(personage)
        actionType = ActionType.IDLE
        switchAction(actionType)
    }

    fun switchAction(type: ActionType) {
        actionType = type
        dimensions = animations[type]?.getDimensions() ?: Point()
        animations[type]?.reset()
    }

    fun draw(canvas: Canvas, viewHeight: Int) {
        val frame = animations[actionType]!!.getFrame()
        canvas.drawBitmap(frame, xPos, (viewHeight - frame.height - 70).toFloat(), null)
    }
}