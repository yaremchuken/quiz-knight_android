package yaremchuken.quizknight.model

class QuizTaskChooseOption(
    question: String,
    verification: String,
    val options: Array<String>
): QuizTask(QuizType.CHOOSE_CORRECT_OPTION, question, arrayOf(verification))