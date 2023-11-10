package yaremchuken.quizknight.utils

object StringUtils {

    /**
     * Clear any punctuation, whitespaces and etc. from word.
     * @return Only letter characters in strict order.
     */
    fun onlyLetters(word: String): String {
        val cleared = word.replace("`s", "").replace("'s", "")
        val builder = StringBuilder(cleared.length)
        for (char: Char in cleared.toCharArray()) {
            if (char.isLetter()) builder.append(char.lowercase())
        }
        return builder.toString()
    }
}