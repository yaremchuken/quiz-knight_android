package yaremchuken.quizknight.model

abstract class QuizTask(val type: QuizType, val question: String, val verifications: Array<String>)