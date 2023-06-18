package yaremchuken.quizknight.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import yaremchuken.quizknight.dao.GameStatsDao
import yaremchuken.quizknight.dao.ModuleProgressDao
import yaremchuken.quizknight.dao.QuizTaskDao
import yaremchuken.quizknight.entity.GameStatsEntity
import yaremchuken.quizknight.entity.ModuleProgressEntity
import yaremchuken.quizknight.entity.QuizTaskEntity


@Database(entities = [QuizTaskEntity::class, GameStatsEntity::class, ModuleProgressEntity::class], version = 3)
@TypeConverters(StringConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getQuizTaskDao(): QuizTaskDao
    abstract fun getGameStatsDao(): GameStatsDao
    abstract fun getModuleProgressDao(): ModuleProgressDao

    companion object {
        const val databaseName = "app_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                val instance =
                    INSTANCE ?:
                    Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, databaseName)
                        .fallbackToDestructiveMigration()
                        .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}