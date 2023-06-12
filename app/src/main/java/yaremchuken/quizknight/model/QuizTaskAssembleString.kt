package yaremchuken.quizknight.model

class QuizTaskAssembleString(
    question: String,
    verification: String,
    val trashWords: String
): QuizTask(QuizType.ASSEMBLE_TRANSLATION_STRING, question, arrayOf(verification))