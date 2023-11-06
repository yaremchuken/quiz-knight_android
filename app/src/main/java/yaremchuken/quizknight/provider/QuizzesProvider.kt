package yaremchuken.quizknight.provider

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import yaremchuken.quizknight.model.ModuleLevel
import yaremchuken.quizknight.model.ModuleType

object QuizzesProvider {

    private lateinit var levels: QuizLevels

    fun preload(context: Context) {
        val mapper = ObjectMapper(YAMLFactory())
        levels =
            context.assets
                .open("quizzes.yaml")
                .use { mapper.readValue(it, QuizLevels::class.java) }
    }

    fun getAllLevels() = levels.quizzes

    fun getModuleLevels(module: ModuleType) = levels.quizzes.filter { it.module == module }
}

private class QuizLevels {
    lateinit var quizzes: List<ModuleLevel>
}
