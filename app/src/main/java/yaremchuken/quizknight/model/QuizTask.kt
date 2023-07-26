package yaremchuken.quizknight.model

class QuizTask {
    lateinit var type: QuizType
    lateinit var display: String
    lateinit var options: List<String>
    lateinit var verifications: List<String>
}