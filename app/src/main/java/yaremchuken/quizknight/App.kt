package yaremchuken.quizknight

import android.app.Application
import yaremchuken.quizknight.database.AppDatabase

class App: Application() {
    val db by lazy {
        AppDatabase.getInstance(this)
    }
}