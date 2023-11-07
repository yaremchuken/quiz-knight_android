package yaremchuken.quizknight.api.yandex.translate

data class YaTranslateResponse(val translations: Array<YaTranslateEntity>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YaTranslateResponse

        if (!translations.contentEquals(other.translations)) return false

        return true
    }

    override fun hashCode(): Int {
        return translations.contentHashCode()
    }
}