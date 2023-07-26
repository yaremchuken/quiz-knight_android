package yaremchuken.quizknight.model

import yaremchuken.quizknight.PersonageType

class ModuleLevel {
    lateinit var module: ModuleType
    lateinit var title: String
    lateinit var opponents: List<PersonageType>
    lateinit var tasks: List<QuizTask>
    var tribute: Long = 0
}
