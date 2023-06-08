package yaremchuken.quizknight.model

class QuizTaskWriteListenedPhrase(
    question: String,
    verifications: Array<String> = arrayOf()
): QuizTask(
    QuizType.WRITE_LISTENED_PHRASE,
    question,
    if (verifications.isNotEmpty()) verifications else arrayOf(question))