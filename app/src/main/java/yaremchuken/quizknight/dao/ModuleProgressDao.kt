package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import yaremchuken.quizknight.entity.ModuleProgressEntity


@Dao
interface ModuleProgressDao {
    @Insert
    suspend fun insert(progresses: List<ModuleProgressEntity>)

    @Query("select * from module_progress where game = :game")
    fun fetch(game: String): Flow<List<ModuleProgressEntity>>
}