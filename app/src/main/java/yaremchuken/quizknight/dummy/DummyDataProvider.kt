package yaremchuken.quizknight.dummy

import yaremchuken.quizknight.OpponentType
import yaremchuken.quizknight.entity.ModuleLevelEntity
import yaremchuken.quizknight.entity.ModuleType
import yaremchuken.quizknight.entity.QuizTaskEntity
import yaremchuken.quizknight.entity.QuizType

class DummyDataProvider {
    companion object {
        fun dummyLevels() =
            listOf(
                ModuleLevelEntity(ModuleType.LAZYWOOD, 1, "Take me to the tour.", OpponentType.GOBLIN, 20),
                ModuleLevelEntity(ModuleType.LAZYWOOD, 2, "Too green, too pretty.", OpponentType.GOBLIN, 40),
                ModuleLevelEntity(ModuleType.CANDYVALE, 1, "There's a rose grows.", OpponentType.GOBLIN, 100)
            )

        fun dummyQuizzes() =
            listOf(
                QuizTaskEntity(
                    ModuleType.LAZYWOOD, 1, 1,
                    QuizType.ASSEMBLE_TRANSLATION_STRING,
                    "Ты смотрела тот фильм вчера?",
                    listOf("will", "tomorrow", "this"),
                    listOf("did you watch that movie yesterday")
                ),
                QuizTaskEntity(
                    ModuleType.LAZYWOOD, 1, 2,
                    QuizType.INPUT_LISTENED_WORD_IN_STRING,
                    "I clean this machine every day",
                    listOf("I clean this <answer> every day"),
                    listOf("machine")
                ),
                QuizTaskEntity(
                    ModuleType.LAZYWOOD, 1, 3,
                    QuizType.CHOOSE_CORRECT_OPTION,
                    "If you go on ........ me like this, i will never be able to finish writing my report.",
                    listOf("disturbing", "afflicting", "concerning", "affecting"),
                    listOf("disturbing")
                ),
                QuizTaskEntity(
                    ModuleType.LAZYWOOD, 2, 1,
                    QuizType.WRITE_LISTENED_PHRASE,
                    "Let's go play in the yard",
                    listOf(),
                    listOf("let's go play in the yard", "lets go play in the yard", "let us go play in the yard")
                ),
                QuizTaskEntity(
                    ModuleType.LAZYWOOD, 2, 2,
                    QuizType.WORD_TRANSLATION_INPUT,
                    "Мой босс любит приходить на работу утром.",
                    listOf("My boss <answer> to come to work in the morning."),
                    listOf("likes", "loves")
                ),

                QuizTaskEntity(
                    ModuleType.CANDYVALE, 1, 1,
                    QuizType.WORD_TRANSLATION_INPUT,
                    "Мне очень нравится это утро.",
                    listOf("I like this morning <answer> ."),
                    listOf("so much", "very much", "a lot")
                ),
                QuizTaskEntity(
                    ModuleType.CANDYVALE, 1, 2,
                    QuizType.CHOOSE_CORRECT_OPTION,
                    "Let's go for a walk ........ .",
                    listOf("in 5:00 p.m.", "on 5:00 p.m.", "at 5:00 p.m.", "as 5:00 p.m."),
                    listOf("at 5:00 p.m.")
                )
            )
    }
}