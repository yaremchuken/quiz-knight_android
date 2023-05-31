package yaremchuken.quizknight

import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizType

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
        QuizTask(
            QuizType.WORD_TRANSLATION_INPUT,
            "Я заканчиваю работу в четыре, поэтому мне нравится моё расписание больше.",
            "I finish work at four, so I like my <answer> more.",
            arrayOf("schedule")
        ),
        QuizTask(
            QuizType.WORD_TRANSLATION_INPUT,
            "Мой босс любит приходить на работу утром.",
            "My boss <answer> to come to work in the morning.",
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

    fun adjustHealth(amount: Int) {
        health = 0.coerceAtLeast(maxHealth.coerceAtMost(health + amount))
    }
}