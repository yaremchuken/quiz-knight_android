package yaremchuken.quizknight

import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizType

class QuizProvider private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: QuizProvider? = null

        fun getInstance(): QuizProvider {
            synchronized(this) {
                val instance = INSTANCE ?: QuizProvider()
                INSTANCE = instance
                return instance
            }
        }
    }

    private var quizIdx = -1
    private val quizes = listOf<QuizTask>(
        QuizTask(
            QuizType.ASSEMBLE_TRANSLATION_STRING,
            "Ты смотрела тот фильм вчера?",
            arrayOf("will", "tomorrow", "this"),
            arrayOf("did you watch that movie yesterday")
        ),
        QuizTask(
            QuizType.INPUT_LISTENED_WORD_IN_STRING,
            "I clean this machine every day",
            arrayOf("I clean this <answer> every day"),
            arrayOf("machine")
        ),
        QuizTask(
            QuizType.CHOOSE_CORRECT_OPTION,
            "If you go on ........ me like this, i will never be able to finish writing my report.",
            arrayOf("disturbing", "afflicting", "concerning", "affecting"),
            arrayOf("disturbing")
        ),
        QuizTask(
            QuizType.WRITE_LISTENED_PHRASE,
            "Let's go play in the yard",
            arrayOf(),
            arrayOf("let's go play in the yard", "lets go play in the yard", "let us go play in the yard")
        ),
        QuizTask(
            QuizType.WORD_TRANSLATION_INPUT,
            "Мой босс любит приходить на работу утром.",
            arrayOf("My boss <answer> to come to work in the morning."),
            arrayOf("likes", "loves")
        )
    )

    fun nextQuiz(): QuizTask? {
        quizIdx++
        if (quizIdx == quizes.size) {
            quizIdx = 0
        }
        return quizes[quizIdx]
    }
}