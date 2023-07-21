package yaremchuken.quizknight

import yaremchuken.quizknight.activities.QuizActivity
import yaremchuken.quizknight.draw.DrawView

object GameStateMachine {

    var state: StateMachineType = StateMachineType.EMPTY
        private set

    var drawer: DrawView? = null

    private lateinit var activity: QuizActivity

    fun init(activity: QuizActivity) {
        this.activity = activity
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
}