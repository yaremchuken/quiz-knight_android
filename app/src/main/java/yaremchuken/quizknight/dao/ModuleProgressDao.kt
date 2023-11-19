package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.model.ModuleType


@Dao
interface ModuleProgressDao {
    @Insert
    fun insert(progresses: List<ModuleProgressEntity>)

    @Query("select * from module_progress where game = :game")
    fun fetch(game: String): List<ModuleProgressEntity>

    @Query("select * from module_progress where game = :game and module = :module")
    fun fetch(game: String, module: ModuleType): List<ModuleProgressEntity>

    @Query("update module_progress set progress = :progress where game = :game and module = :module")
    fun updateProgress(game: String, module: ModuleType, progress: Long)
}