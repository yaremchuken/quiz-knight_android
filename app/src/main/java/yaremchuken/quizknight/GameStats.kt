package yaremchuken.quizknight

import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.model.ModuleType
import java.util.EnumMap
import java.util.Locale

object GameStats {

    var maxHealth: Long = 3
        private set

    var health: Double = 0.0
        private set

    var gold: Long = 0

    var game: String = ""
        private set

    var original: Locale = Locale.ENGLISH
        private set

    var studied: Locale = Locale.ENGLISH
        private set

    var module: ModuleType = ModuleType.LAZYWOOD
        private set

    var progress: MutableMap<ModuleType, Long> = EnumMap(ModuleType::class.java)
        private set

    var currentLevel: Long = -1

    /**
     * Initializing with goblin to preheat draw view
     */
    var opponent: PersonageType = PersonageType.GOBLIN

    fun dropHeart() {
        health = 0.0.coerceAtLeast(health-1)
    }

    fun init(stats: GameStatsEntity, progress: MutableMap<ModuleType, Long>) {
        health = stats.health
        gold = stats.gold
        game = stats.game
        original = stats.original
        studied = stats.studied
        this.progress = progress
        switchModule(stats.module)
    }

    fun updateProgress() {
        progress[module] = currentLevel
    }

    fun switchModule(moduleType: ModuleType) {
        module = moduleType
    }
}