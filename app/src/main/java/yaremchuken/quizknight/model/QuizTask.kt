package yaremchuken.quizknight.model

data class QuizTask(val type: QuizType, val question: String, val placeholder: String, val answers: Array<String>)