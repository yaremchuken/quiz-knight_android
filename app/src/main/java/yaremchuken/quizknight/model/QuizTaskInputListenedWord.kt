package yaremchuken.quizknight.model

class QuizTaskInputListenedWord(
    question: String,
    verification: String,
    val placeholder: String
): QuizTask(QuizType.INPUT_LISTENED_WORD_IN_STRING, question, arrayOf(verification))