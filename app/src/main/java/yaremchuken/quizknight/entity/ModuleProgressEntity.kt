package yaremchuken.quizknight.entity

import androidx.room.Entity

@Entity(tableName = "module_progress", primaryKeys = ["game", "module"])
data class ModuleProgressEntity(
    val game: String,
    val module: ModuleType,
    var progress: Long
)