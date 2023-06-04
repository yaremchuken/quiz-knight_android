package yaremchuken.quizknight

import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizTaskAssembleString
import yaremchuken.quizknight.model.QuizTaskChooseOption
import yaremchuken.quizknight.model.QuizTaskTranslateWord

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
        QuizTaskAssembleString(
            "Ты смотрела тот фильм вчера?",
            "did you watch that movie yesterday"
        ),
        QuizTaskChooseOption(
            "If you go on ........ me like this, i will never be able to finish writing my report.",
            "disturbing",
            arrayOf("disturbing", "afflicting", "concerning", "affecting")
        ),
        QuizTaskTranslateWord(
            "Я заканчиваю работу в четыре, поэтому мне нравится моё расписание больше.",
            arrayOf("schedule"),
            "I finish work at four, so I like my <answer> more."
        ),
        QuizTaskTranslateWord(
            "Мой босс любит приходить на работу утром.",
            arrayOf("likes", "loves"),
            "My boss <answer> to come to work in the morning."
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