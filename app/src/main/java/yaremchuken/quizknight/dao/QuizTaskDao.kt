package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import yaremchuken.quizknight.entity.ModuleType
import yaremchuken.quizknight.entity.QuizTaskEntity

@Dao
interface QuizTaskDao {
    @Insert
    suspend fun insert(entities: List<QuizTaskEntity>)

    @Query("select * from `quiz_task`")
    fun fetchAll(): Flow<List<QuizTaskEntity>>

    @Query("select * from `quiz_task` where module = :module and level = :level and `order` = :order")
    fun fetch(module: ModuleType, level: Long, order: Long): Flow<List<QuizTaskEntity>>
}