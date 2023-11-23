package yaremchuken.quizknight.model

import yaremchuken.quizknight.PersonageType

class ModuleLevel {
    lateinit var module: ModuleType
    lateinit var title: String
    lateinit var opponents: List<PersonageType>
    lateinit var tasks: List<QuizTask>
    var tribute: Long = 0

    companion object {
        fun builder(
            module: ModuleType,
            title: String,
            opponents: List<PersonageType>,
            tasks: List<QuizTask>,
            tribute: Long
        ): ModuleLevel {
            val obj = ModuleLevel()
            obj.module = module
            obj.title = title
            obj.opponents = opponents
            obj.tasks = tasks
            obj.tribute = tribute
            return obj
        }
    }
}
