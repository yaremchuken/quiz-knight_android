package yaremchuken.quizknight.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("game_stats")
data class GameStatsEntity(
    @PrimaryKey
    val game: String,

    val order: Long,
    val original: Language,
    val studied: Language,

    var module: ModuleType,
    var gold: Long,
    var health: Double,
)