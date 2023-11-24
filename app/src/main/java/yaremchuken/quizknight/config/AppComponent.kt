package yaremchuken.quizknight.config

import dagger.Component
import yaremchuken.quizknight.activity.QuizActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(quizActivity: QuizActivity)
}