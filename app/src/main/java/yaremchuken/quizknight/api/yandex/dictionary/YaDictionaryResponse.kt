package yaremchuken.quizknight.api.yandex.dictionary

/**
 * Response fields described in online documentation https://yandex.ru/dev/dictionary/doc/dg/reference/lookup.html
 */
data class YaDictionaryResponse(val def: Array<YaDictionaryEntity>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YaDictionaryResponse

        if (!def.contentEquals(other.def)) return false

        return true
    }

    override fun hashCode(): Int {
        return def.contentHashCode()
    }
}