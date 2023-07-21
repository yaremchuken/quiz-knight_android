package yaremchuken.quizknight.entity

import androidx.room.Entity
import yaremchuken.quizknight.PersonageType

/**
 * Description of particular level from the module.
 * Used just as dictionary, module progress is maintained by `module_progress`
 */
@Entity(tableName = "module_level", primaryKeys = ["module", "order"])
data class ModuleLevelEntity(
    val module: ModuleType,
    val order: Long,
    val title: String,
    val opponents: List<PersonageType>,
    val tribute: Long
)
