package yaremchuken.quizknight.activities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.QuizTaskChecker
import yaremchuken.quizknight.StateMachineType
import yaremchuken.quizknight.adapters.AnswerPartAdapter
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.databinding.ActivityQuizBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.model.QuizTask

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding

    private var quizTask: QuizTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameStatsBarBinding = FragmentGameStatsBarBinding.inflate(layoutInflater)
        binding.flQuizTop.addView(gameStatsBarBinding.root)

        updateHealthBar()
        updateGold()

        binding.btnCheck.setOnClickListener {
            checkAnswer()
        }

        binding.tvQuizQuestion.visibility = View.INVISIBLE
        binding.rvAnswerField.visibility = View.INVISIBLE
        binding.btnCheck.visibility = View.INVISIBLE

        GameStateMachine.getInstance().init(this)
    }

    fun startQuiz() {
        quizTask = GameStats.getInstance().nextQuiz()
        if (quizTask != null) {
            binding.tvQuizQuestion.text = quizTask!!.question

            binding.tvQuizQuestion.visibility = View.VISIBLE
            binding.rvAnswerField.visibility = View.VISIBLE
            binding.btnCheck.visibility = View.VISIBLE

            binding.rvAnswerField.layoutManager = FlexboxLayoutManager(this)
            binding.rvAnswerField.adapter = AnswerPartAdapter(quizTask!!.placeholder.split(" "))

            GameStateMachine.getInstance().switchState(StateMachineType.QUIZ)
        }
    }

    private fun endQuiz() {
        binding.tvQuizQuestion.visibility = View.INVISIBLE
        binding.rvAnswerField.visibility = View.INVISIBLE
        binding.btnCheck.visibility = View.INVISIBLE

        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(binding.rvAnswerField.rootView.applicationWindowToken, 0)

        GameStateMachine.getInstance().switchState(StateMachineType.CONTINUE_MOVING)
    }

    private fun checkAnswer() {
        if (quizTask == null) return

        val correct =
            QuizTaskChecker.checkAnswer(
                quizTask!!,
                (binding.rvAnswerField.adapter as AnswerPartAdapter).playerInput)

        if (correct) {
            endQuiz()
        } else {
            GameStats.getInstance().adjustHealth(-1)
            updateHealthBar()
        }
    }

    private fun updateHealthBar() {
        val healths: ArrayList<Boolean> = ArrayList(GameStats.getInstance().maxHealth)
        for (i in 0 until GameStats.getInstance().maxHealth) {
            healths.add(i < GameStats.getInstance().health)
        }
        gameStatsBarBinding.rvHearts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        gameStatsBarBinding.rvHearts.adapter = HealthBarAdapter(healths)
    }

    private fun updateGold() {
        gameStatsBarBinding.tvGold.text = GameStats.getInstance().gold.toString()
    }
}