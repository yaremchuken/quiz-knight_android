package yaremchuken.quizknight.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import yaremchuken.quizknight.dao.GameStatsDao
import yaremchuken.quizknight.dao.ModuleProgressDao
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.ModuleProgressEntity

@Database(
    version = 12,
    entities = [
        GameStatsEntity::class,
        ModuleProgressEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getGameStatsDao(): GameStatsDao
    abstract fun getModuleProgressDao(): ModuleProgressDao

    companion object {
        const val databaseName = "app_database"
    }
}