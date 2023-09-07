package yaremchuken.quizknight

enum class StateMachineType {
    EMPTY,
    PREPARE_ASSETS,
    MOVING,
    START_QUIZ,
    QUIZ,
    CONTINUE_MOVING,
    COMPLETED
}