package yaremchuken.quizknight.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import yaremchuken.quizknight.dao.QuizTaskDao
import yaremchuken.quizknight.entity.QuizTaskEntity


@Database(entities = [QuizTaskEntity::class], version = 2)
@TypeConverters(StringConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getQuizTaskDao(): QuizTaskDao

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