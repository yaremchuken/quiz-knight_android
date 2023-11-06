package yaremchuken.quizknight

import yaremchuken.quizknight.activity.QuizActivity
import yaremchuken.quizknight.draw.DrawView

object GameStateMachine {

    var state: StateMachineType = StateMachineType.EMPTY
        private set

    var drawer: DrawView? = null

    private lateinit var activity: QuizActivity

    /**
     * Flags that shows that canvas is ready and animations loaded
     */
    var canvasReady = false
    private var levelReady = false

    private var started = false

    fun registerActivity(activity: QuizActivity) {
        this.activity = activity
        levelReady = true
        startMachine()
    }

    fun startMachine() {
        if (canvasReady && levelReady && !started) {
            switchState(StateMachineType.PREPARE_ASSETS)
            started = true
        }
    }

    fun switchState(state: StateMachineType) {
        this.state = state
        if (state == StateMachineType.START_QUIZ) {
            activity.runOnUiThread {
                activity.startQuiz()
            }
        }
        drawer?.propagateStateChanged()
    }

    fun stopMachine() {
        state = StateMachineType.EMPTY
        started = false
        levelReady = false
        canvasReady = false
    }
}