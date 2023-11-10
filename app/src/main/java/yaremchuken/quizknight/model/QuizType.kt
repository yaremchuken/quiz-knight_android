package yaremchuken.quizknight.model

/**
 * Types of quiz tasks, that Player have to solve to progress in game.
 */
enum class QuizType {
    /**
     * Quiz where Player have to input single word translation.
     */
    WORD_TRANSLATION_INPUT,

    /**
     * Quiz where Player have to choose on of four options to fill empty spot in question sentence.
     */
    CHOOSE_CORRECT_OPTION,

    /**
     * Quiz where Player have to build sentence from predefined words.
     * The question is sentence in original language.
     */
    ASSEMBLE_TRANSLATION_STRING,

    /**
     * Quiz where Player have to write listened phrase.
     */
    WRITE_LISTENED_PHRASE,

    /**
     * Quiz where Player have to write listened word to complete sentence.
     */
    INPUT_LISTENED_WORD_IN_STRING;

    companion object {
        fun isAudition(type: QuizType) = type == WRITE_LISTENED_PHRASE || type == INPUT_LISTENED_WORD_IN_STRING
    }
}