package yaremchuken.quizknight.model

data class QuizTask(
    val type: QuizType,
    val display: String,
    val options: Array<String>,
    val verifications: Array<String>
)