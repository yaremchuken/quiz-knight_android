package yaremchuken.quizknight.model

import java.util.Locale

/**
 * Modules are represents different topics to learn, they are same as Game locations for hero adventures.
 */
enum class ModuleType(
    private val titles: Map<Locale, String>,
    private val descriptions: Map<Locale, String>
) {
    LAZYWOOD(
        titles = mapOf(
            Pair(Locale.ENGLISH, "Lazywood"),
            Pair(Locale("ru"), "Деревяшкино")
        ),
        descriptions = mapOf(
            Pair(Locale.ENGLISH, "Our adventure begins in this small village"),
            Pair(Locale("ru"), "Наше путишествие начинается с этой небольшой деревушки")
        )
    ),
    CANDYVALE(
        titles = mapOf(
            Pair(Locale.ENGLISH, "Candyvale"),
            Pair(Locale("ru"), "Конфетово")
        ),
        descriptions = mapOf(
            Pair(Locale.ENGLISH, "Monsters surrounding this village are ready to give you theirs warmest welcome"),
            Pair(Locale("ru"), "Монстры окружившие эту деревушку готовы оказать своё тёплое приветствие")
        )
    );

    companion object {
        fun title(type: ModuleType, locale: Locale) =
            type.titles[locale] ?: throw IllegalArgumentException()

        fun description(type: ModuleType, locale: Locale) =
            type.descriptions[locale] ?: throw IllegalArgumentException()
    }
}