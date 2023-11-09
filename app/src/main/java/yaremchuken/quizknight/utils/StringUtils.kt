package yaremchuken.quizknight.utils

object StringUtils {

    /**
     * Clear any punctuation, whitespaces and etc. from word.
     * @return Only letter characters in strict order.
     */
    fun onlyLetters(word: String): String {
        val builder = StringBuilder(word.length)
        for (char: Char in word.toCharArray()) {
            if (char.isLetter()) builder.append(char)
        }
        return builder.toString()
    }
}