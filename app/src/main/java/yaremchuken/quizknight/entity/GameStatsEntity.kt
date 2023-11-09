package yaremchuken.quizknight.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import yaremchuken.quizknight.model.ModuleType
import java.util.Locale

@Entity("game_stats")
data class GameStatsEntity(
    @PrimaryKey
    val game: String,

    val order: Long,
    val original: Locale,
    val studied: Locale,

    var module: ModuleType,
    var gold: Long,
    var health: Double,
)