package yaremchuken.quizknight

import yaremchuken.quizknight.entity.QuizTaskEntity
import yaremchuken.quizknight.entity.QuizType

object QuizTaskChecker {
    fun checkAnswer(task: QuizTaskEntity, answer: String): Boolean {
        var corrected = answer.lowercase()
        if (task.type == QuizType.ASSEMBLE_TRANSLATION_STRING) {
            return answer == task.verifications.joinToString(" ")
        }
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