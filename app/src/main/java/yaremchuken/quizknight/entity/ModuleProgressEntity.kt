package yaremchuken.quizknight.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("module_progress")
data class ModuleProgressEntity(
    val game: String,
    val module: ModuleType,
    val progress: Long
) {
    @PrimaryKey(true)
    var id: Long = 0
}