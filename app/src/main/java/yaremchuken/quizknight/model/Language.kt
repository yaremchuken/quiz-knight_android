package yaremchuken.quizknight.model

/**
 * Language, that is used during learning process as original or studied language.
 * @param code represents ISO 639-1 code of name of language https://en.wikipedia.org/wiki/ISO_639-1
 */
enum class Language(val code: String) {
    EN("en"),
    RU("ru")
}