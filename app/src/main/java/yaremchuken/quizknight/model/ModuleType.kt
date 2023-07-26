package yaremchuken.quizknight.model

/**
 * Modules are represents different topics to learn, they are same as Game locations for hero adventures.
 */
enum class ModuleType {
    LAZYWOOD,
    CANDYVALE;

    companion object {
        fun description(type: ModuleType) =
            when (type) {
                LAZYWOOD -> "Begin your adventure in this small village"
                CANDYVALE -> "Monsters surrounding this city are ready to give you theirs warmest welcome"
            }
    }
}