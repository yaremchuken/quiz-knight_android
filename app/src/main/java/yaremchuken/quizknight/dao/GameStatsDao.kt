package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.ModuleType

@Dao
interface GameStatsDao {
    @Insert
    suspend fun insert(gameStats: GameStatsEntity)

    @Query("select * from game_stats")
    suspend fun fetchAll(): List<GameStatsEntity>

    @Query("update game_stats set module = :moduleType where game = :game")
    suspend fun switchModule(game: String, moduleType: ModuleType)

    @Query("update game_stats set gold = :gold where game = :game")
    suspend fun updateGold(game: String, gold: Long)
}