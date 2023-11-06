package yaremchuken.quizknight.api.yandex.translate

data class YaTranslateRequest(
    val texts: Array<String>,
    val sourceLanguageCode: String,
    val targetLanguageCode: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YaTranslateRequest

        if (!texts.contentEquals(other.texts)) return false
        if (sourceLanguageCode != other.sourceLanguageCode) return false
        if (targetLanguageCode != other.targetLanguageCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = texts.contentHashCode()
        result = 31 * result + sourceLanguageCode.hashCode()
        result = 31 * result + targetLanguageCode.hashCode()
        return result
    }

    override fun toString() = "{from: $sourceLanguageCode, to: $targetLanguageCode, texts: ${texts.joinToString()}}"
}