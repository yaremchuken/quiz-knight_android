package yaremchuken.quizknight.config

import dagger.Component
import yaremchuken.quizknight.activity.CampActivity
import yaremchuken.quizknight.activity.MainActivity
import yaremchuken.quizknight.activity.QuizActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, DbModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: CampActivity)
    fun inject(activity: QuizActivity)
}