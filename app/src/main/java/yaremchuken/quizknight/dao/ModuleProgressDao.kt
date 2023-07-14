package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.entity.ModuleType


@Dao
interface ModuleProgressDao {
    @Insert
    suspend fun insert(progresses: List<ModuleProgressEntity>)

    @Query("select * from module_progress where game = :game")
    fun fetch(game: String): Flow<List<ModuleProgressEntity>>

    @Query("select * from module_progress where game = :game and module = :module")
    fun fetch(game: String, module: ModuleType): Flow<List<ModuleProgressEntity>>

    @Query("update module_progress set progress = :progress where game = :game and module = :module")
    suspend fun updateProgress(game: String, module: ModuleType, progress: Long)
}