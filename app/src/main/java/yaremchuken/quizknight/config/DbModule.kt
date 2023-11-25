package yaremchuken.quizknight.config

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import yaremchuken.quizknight.database.AppDatabase
import javax.inject.Singleton

@Module
class DbModule (context: Context) {
    val db = Room
        .databaseBuilder(context, AppDatabase::class.java, AppDatabase.databaseName)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun gameStatsDao() = db.getGameStatsDao()

    @Provides
    @Singleton
    fun moduleProgressDao() = db.getModuleProgressDao()
}