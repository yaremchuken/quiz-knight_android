package yaremchuken.quizknight.compose

import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.model.ModuleType

data class GameStatsBarModel(val health: Double, val maxHealth: Long, val name: String, val gold: Long) {
    companion object {
        fun mapGameStats(stats: GameStats) =
            GameStatsBarModel(
                stats.health,
                stats.maxHealth,
                ModuleType.title(stats.module, stats.original),
                stats.gold
            )
    }
}