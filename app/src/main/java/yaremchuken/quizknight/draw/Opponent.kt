package yaremchuken.quizknight.draw

import android.content.Context
import android.graphics.Point
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.StateMachineType

class Opponent(context: Context, sceneDims: Point): Animated() {

    private var targetPos: Float = 0F

    init {
        super.init(context, sceneDims, "goblin")
    }

    fun resetPos() {
        targetPos = sceneDims.x - 50F - dimensions.x
        xPos = DrawView.WORLD_SPEED * 30 + targetPos
    }

    fun updatePos() {
        xPos -= DrawView.WORLD_SPEED
        if (xPos <= targetPos) {
            GameStateMachine.getInstance().switchState(StateMachineType.START_QUIZ)
        }
    }
}