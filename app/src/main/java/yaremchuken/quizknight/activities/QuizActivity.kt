package yaremchuken.quizknight.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.QuizTaskChecker
import yaremchuken.quizknight.StateMachineType
import yaremchuken.quizknight.adapters.AnswerTranslateWordAdapter
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.databinding.ActivityQuizBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizTaskChooseOption
import yaremchuken.quizknight.model.QuizTaskTranslateWord
import yaremchuken.quizknight.model.QuizType
import java.lang.RuntimeException

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
        binding.rvAnswerBoard.visibility = View.INVISIBLE
        binding.rgOptionsGroup.visibility = View.INVISIBLE
        binding.btnCheck.visibility = View.INVISIBLE

        GameStateMachine.getInstance().init(this)
    }

    @SuppressLint("SetTextI18n")
    fun startQuiz() {
        quizTask = GameStats.getInstance().nextQuiz()
        if (quizTask != null) {
            binding.tvQuizQuestion.text = quizTask?.question

            binding.tvQuizQuestion.visibility = View.VISIBLE
            binding.btnCheck.visibility = View.VISIBLE

            when (quizTask?.type) {
                QuizType.WORD_TRANSLATION_INPUT -> {
                    binding.rvAnswerBoard.visibility = View.VISIBLE
                    binding.rvAnswerBoard.layoutManager = FlexboxLayoutManager(this)
                    binding.rvAnswerBoard.adapter =
                        AnswerTranslateWordAdapter(
                            (quizTask as QuizTaskTranslateWord).placeholder.split(" "))
                }

                QuizType.CHOOSE_CORRECT_OPTION -> {
                    binding.rgOptionsGroup.visibility = View.VISIBLE
                    val quiz = quizTask as QuizTaskChooseOption

                    binding.rbOptionA.isChecked = false
                    binding.rbOptionB.isChecked = false
                    binding.rbOptionC.isChecked = false
                    binding.rbOptionD.isChecked = false

                    binding.rbOptionA.text = "A. ${quiz.options[0]}"
                    binding.rbOptionB.text = "B. ${quiz.options[1]}"
                    binding.rbOptionC.text = "C. ${quiz.options[2]}"
                    binding.rbOptionD.text = "D. ${quiz.options[3]}"
                }

                else -> throw RuntimeException("Unknown quiz type ${quizTask?.type}")
            }

            GameStateMachine.getInstance().switchState(StateMachineType.QUIZ)
        }
    }

    private fun endQuiz() {
        binding.tvQuizQuestion.visibility = View.INVISIBLE
        binding.rvAnswerBoard.visibility = View.INVISIBLE
        binding.rgOptionsGroup.visibility = View.INVISIBLE
        binding.btnCheck.visibility = View.INVISIBLE

        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(binding.rvAnswerBoard.rootView.applicationWindowToken, 0)

        GameStateMachine.getInstance().switchState(StateMachineType.CONTINUE_MOVING)
    }

    private fun checkAnswer() {
        if (quizTask == null) return

        val isCorrect = when (quizTask?.type) {
            QuizType.WORD_TRANSLATION_INPUT ->
                QuizTaskChecker.checkAnswer(
                    quizTask!!,
                    (binding.rvAnswerBoard.adapter as AnswerTranslateWordAdapter).playerInput
                )

            QuizType.CHOOSE_CORRECT_OPTION ->
                QuizTaskChecker.checkAnswer(
                    quizTask!!,
                    findViewById<RadioButton>(binding.rgOptionsGroup.checkedRadioButtonId).text.toString()
                )

            else -> throw RuntimeException("Unknown quiz type ${quizTask?.type}")
        }

        if (isCorrect) {
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
        gameStatsBarBinding.rvHearts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        gameStatsBarBinding.rvHearts.adapter = HealthBarAdapter(healths)
    }

    private fun updateGold() {
        gameStatsBarBinding.tvGold.text = GameStats.getInstance().gold.toString()
    }
}