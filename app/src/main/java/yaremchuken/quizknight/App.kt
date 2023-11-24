package yaremchuken.quizknight

import android.app.Application
import yaremchuken.quizknight.config.AppComponent
import yaremchuken.quizknight.config.AppModule
import yaremchuken.quizknight.config.DaggerAppComponent
import yaremchuken.quizknight.database.AppDatabase

class App: Application() {
    val db by lazy {
        AppDatabase.getInstance(this)
    }

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(resources)).build()
    }
}