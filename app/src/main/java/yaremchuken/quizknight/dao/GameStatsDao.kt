package yaremchuken.quizknight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.model.ModuleType

@Dao
interface GameStatsDao {
    @Insert
    fun insert(gameStats: GameStatsEntity)

    @Query("select * from game_stats")
    fun fetchAll(): List<GameStatsEntity>

    @Query("update game_stats set module = :moduleType where game = :game")
    fun switchModule(game: String, moduleType: ModuleType)

    @Query("update game_stats set gold = :gold where game = :game")
    fun updateGold(game: String, gold: Long)

    @Query("update game_stats set playedAt = :playedAt where game = :game")
    fun markLaunch(game: String, playedAt: Long)
}