package yaremchuken.quizknight.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("quiz_task")
data class QuizTaskEntity(
    val module: ModuleType,
    val level: Long,
    val order: Long,
    val type: QuizType,
    val display: String,
    val options: List<String>,
    val verifications: List<String>,
    val tribute: Long,
) {
    @PrimaryKey(true)
    var id: Long = 0
}