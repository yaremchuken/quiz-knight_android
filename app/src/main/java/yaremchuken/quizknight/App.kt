package yaremchuken.quizknight

import android.app.Application
import yaremchuken.quizknight.config.ApiModule
import yaremchuken.quizknight.config.AppComponent
import yaremchuken.quizknight.config.DaggerAppComponent
import yaremchuken.quizknight.config.DbModule

class App: Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .apiModule(ApiModule(resources))
            .dbModule(DbModule(applicationContext))
            .build()
    }
}