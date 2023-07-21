package yaremchuken.quizknight.draw

import android.graphics.Point
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.StateMachineType

class Personage(personage: PersonageType, sceneDims: Point): Animated() {

    private lateinit var personage: PersonageType
    private var targetPos: Float = 0F

    init {
        this.sceneDims = sceneDims
        changePersonage(personage)
    }

    fun changePersonage(personage: PersonageType) {
        super.init(personage, sceneDims)
        this.personage = personage
        resetPos()
    }

    private fun resetPos() {
        if (personage == PersonageType.HERO) {
            xPos = 60F
        } else {
            targetPos = sceneDims.x - 50F - dimensions.x
            xPos = DrawView.WORLD_SPEED * 30 + targetPos
        }
    }

    fun updatePos() {
        if (personage == PersonageType.HERO) {
            throw IllegalArgumentException("Appropriate only for non-Hero character")
        }
        xPos -= DrawView.WORLD_SPEED
        if (xPos <= targetPos) {
            GameStateMachine.switchState(StateMachineType.START_QUIZ)
        }
    }
}