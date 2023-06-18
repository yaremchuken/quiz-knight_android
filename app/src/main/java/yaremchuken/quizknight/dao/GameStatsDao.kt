package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import yaremchuken.quizknight.entity.GameStatsEntity

@Dao
interface GameStatsDao {
    @Insert
    suspend fun insert(gameStats: GameStatsEntity)

    @Query("select * from `game_stats`")
    fun fetchAll(): Flow<List<GameStatsEntity>>
}