package yaremchuken.quizknight.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import yaremchuken.quizknight.App
import yaremchuken.quizknight.databinding.ActivityMainBinding
import yaremchuken.quizknight.entity.ModuleType
import yaremchuken.quizknight.entity.QuizTaskEntity
import yaremchuken.quizknight.entity.QuizType

// FIXME: Canvas is blacked out when app is suspends (when middle btn clicked)

// TODO: Hide gamestats bar on keyboard appear

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDB()

        binding.btnStart.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeDB() {
        val dao = (application as App).db.getQuizTaskDao()
        lifecycleScope.launch {
            dao.fetchAll().collect {
                if (it.isEmpty()) {
                    dao.insert(
                        listOf(
                            QuizTaskEntity(
                                ModuleType.LAZYWOOD, 1, 1,
                                QuizType.ASSEMBLE_TRANSLATION_STRING,
                                "Ты смотрела тот фильм вчера?",
                                listOf("will", "tomorrow", "this"),
                                listOf("did you watch that movie yesterday"),
                                5
                            ),
                            QuizTaskEntity(
                                ModuleType.LAZYWOOD, 1, 2,
                                QuizType.INPUT_LISTENED_WORD_IN_STRING,
                                "I clean this machine every day",
                                listOf("I clean this <answer> every day"),
                                listOf("machine"),
                                5
                            ),
                            QuizTaskEntity(
                                ModuleType.LAZYWOOD, 1, 3,
                                QuizType.CHOOSE_CORRECT_OPTION,
                                "If you go on ........ me like this, i will never be able to finish writing my report.",
                                listOf("disturbing", "afflicting", "concerning", "affecting"),
                                listOf("disturbing"),
                                5
                            ),
                            QuizTaskEntity(
                                ModuleType.LAZYWOOD, 1, 4,
                                QuizType.WRITE_LISTENED_PHRASE,
                                "Let's go play in the yard",
                                listOf(),
                                listOf("let's go play in the yard", "lets go play in the yard", "let us go play in the yard"),
                                5
                            ),
                            QuizTaskEntity(
                                ModuleType.LAZYWOOD, 1, 5,
                                QuizType.WORD_TRANSLATION_INPUT,
                                "Мой босс любит приходить на работу утром.",
                                listOf("My boss <answer> to come to work in the morning."),
                                listOf("likes", "loves"),
                                5
                            )
                        )
                    )
                }
            }
        }
    }
}