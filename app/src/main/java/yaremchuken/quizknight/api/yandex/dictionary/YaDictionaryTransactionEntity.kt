package yaremchuken.quizknight.api.yandex.dictionary

/**
 * 
 */
data class YaDictionaryTransactionEntity(
    /**
     * Translation.
     */
    val text: String,

    /**
     * Synonyms for current translation.
     */
    val syn: Array<YaDictionarySynonym>?,

    /**
     * Synonym for original word in means of current translation.
     */
    val mean: Array<YaDictionarySynonym>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YaDictionaryTransactionEntity

        if (text != other.text) return false
        if (!syn.contentEquals(other.syn)) return false
        if (!mean.contentEquals(other.mean)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + syn.contentHashCode()
        result = 31 * result + mean.contentHashCode()
        return result
    }
}