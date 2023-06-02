package yaremchuken.quizknight.model

class QuizTaskTranslateWord(
    question: String,
    verifications: Array<String>,
    val placeholder: String,
): QuizTask(QuizType.WORD_TRANSLATION_INPUT, question, verifications)