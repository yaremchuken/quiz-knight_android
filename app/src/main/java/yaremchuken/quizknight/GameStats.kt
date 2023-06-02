package yaremchuken.quizknight

import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizTaskChooseOption
import yaremchuken.quizknight.model.QuizTaskTranslateWord

class GameStats private constructor(){

    companion object {
        @Volatile
        private var INSTANCE: GameStats? = null

        fun getInstance(): GameStats {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = GameStats()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }

    var maxHealth: Int = 3
        private set
    var health: Int = 2
        private set

    var gold: Int = 150
        private set

    private var quizIdx = -1
    private val quizes = listOf<QuizTask>(
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

    fun adjustHealth(amount: Int) {
        health = 0.coerceAtLeast(maxHealth.coerceAtMost(health + amount))
    }
}