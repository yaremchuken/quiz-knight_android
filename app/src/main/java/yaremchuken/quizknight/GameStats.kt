package yaremchuken.quizknight

import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.ModuleType
import java.util.EnumMap

class GameStats private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: GameStats? = null

        fun getInstance(): GameStats {
            synchronized(this) {
                val instance = INSTANCE ?: GameStats()
                INSTANCE = instance
                return instance
            }
        }
    }

    var maxHealth: Long = 3
        private set

    var health: Double = 0.0
        private set

    var gold: Long = 0
        private set

    var game: String = ""
        private set

    var module: ModuleType = ModuleType.LAZYWOOD
        private set

    var progress: Map<ModuleType, Long> = EnumMap(ModuleType::class.java)
        private set

    var level: Long = -1
        private set

    fun dropHeart() {
        health = 0.0.coerceAtLeast(health-1)
    }

    fun init(stats: GameStatsEntity, progress: Map<ModuleType, Long>) {
        health = stats.health
        gold = stats.gold
        game = stats.game
        this.progress = progress
        switchModule(stats.module)
    }

    fun switchModule(moduleType: ModuleType) {
        module = moduleType
        level = progress[module]!!
    }
}