package yaremchuken.quizknight

import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizType

object QuizTaskChecker {
    fun checkAnswer(task: QuizTask, answer: String): Boolean {
        var corrected = answer.lowercase()
        if (task.type == QuizType.CHOOSE_CORRECT_OPTION) {
            corrected = corrected.substring(3, corrected.length)
        }
        task.verifications.forEach {
            if (it.lowercase() == corrected) {
                return true
            }
        }
        return false
    }
}