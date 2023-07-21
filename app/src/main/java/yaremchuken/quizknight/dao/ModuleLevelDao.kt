package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import yaremchuken.quizknight.entity.ModuleLevelEntity
import yaremchuken.quizknight.entity.ModuleType

@Dao
interface ModuleLevelDao {
    @Insert
    suspend fun insert(entities: List<ModuleLevelEntity>)

    @Query("select * from module_level")
    fun fetchAll(): Flow<List<ModuleLevelEntity>>

    @Query("select * from module_level where module = :module")
    suspend fun fetch(module: ModuleType): List<ModuleLevelEntity>
}