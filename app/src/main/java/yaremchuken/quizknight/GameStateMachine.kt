package yaremchuken.quizknight

import yaremchuken.quizknight.activities.QuizActivity

object GameStateMachine {

    var state: StateMachineType = StateMachineType.INITIALIZING
        private set

    private lateinit var activity: QuizActivity

    fun init(activity: QuizActivity) {
        this.activity = activity
    }

    fun startMachine() {
        state = StateMachineType.MOVING
    }

    fun switchState(state: StateMachineType) {
        this.state = state
        if (state == StateMachineType.START_QUIZ) {
            activity.runOnUiThread {
                activity.startQuiz()
            }
        }
    }
}