package yaremchuken.quizknight.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import yaremchuken.quizknight.OpponentType

/**
 * Description of particular level from the module.
 * Used just as dictionary, module progress is maintained by `module_progress`
 */
@Entity("module_level")
data class ModuleLevelEntity(
    val module: ModuleType,
    val order: Long,
    val title: String,
    val opponent: OpponentType,
    val tribute: Long
) {
    @PrimaryKey(true)
    var id: Long = 0
}
