package yaremchuken.quizknight

import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizType

object QuizTaskChecker {
    fun checkAnswer(task: QuizTask, answer: String): Boolean =
        when (task.type) {
            QuizType.WORD_TRANSLATION_INPUT -> task.verifications.contains(answer)
            QuizType.CHOOSE_CORRECT_OPTION -> task.verifications.contains(answer.substring(3, answer.length))
            else -> throw RuntimeException("Unknown task type ${task.type}")
        }
}