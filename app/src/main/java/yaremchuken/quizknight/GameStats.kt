package yaremchuken.quizknight

import yaremchuken.quizknight.entity.ModuleType

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

    var maxHealth: Int = 3
        private set
    var health: Int = 2
        private set

    var gold: Int = 150
        private set

    var module: ModuleType = ModuleType.LAZYWOOD
        private set

    var level: Long = 1
        private set

    fun adjustHealth(amount: Int) {
        health = 0.coerceAtLeast(maxHealth.coerceAtMost(health + amount))
    }
}