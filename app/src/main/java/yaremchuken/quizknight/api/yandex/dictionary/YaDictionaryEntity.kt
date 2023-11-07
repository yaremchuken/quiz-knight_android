package yaremchuken.quizknight.api.yandex.dictionary

data class YaDictionaryEntity(
    /**
     * Original word to translate.
     */
    val text: String,
    /**
     * Part of speech.
     */
    val pos: String?,

    /**
     * Transcription.
     */
    val ts: String?,

    /**
     * Array of translations.
     */
    val tr: Array<YaDictionaryTransactionEntity>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YaDictionaryEntity

        if (pos != other.pos) return false
        if (ts != other.ts) return false
        if (!tr.contentEquals(other.tr)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pos?.hashCode() ?: 0
        result = 31 * result + (ts?.hashCode() ?: 0)
        result = 31 * result + tr.contentHashCode()
        return result
    }
}