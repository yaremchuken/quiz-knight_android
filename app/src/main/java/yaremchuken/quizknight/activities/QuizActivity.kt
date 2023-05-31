package yaremchuken.quizknight.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.StateMachineType
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.databinding.ActivityQuizBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.model.QuizTask

const val MIN_ANSWER_SPACE = 3

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding
    private lateinit var quizAnswer: TextView

    private var quizTask: QuizTask? = null

    private var playerInput: ArrayList<Char> = ArrayList()
    private var startIdx: Int = 0

    private val underline = UnderlineSpan()
    private val colored = ForegroundColorSpan(Color.BLUE)

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameStatsBarBinding = FragmentGameStatsBarBinding.inflate(layoutInflater)
        binding.flQuizTop.addView(gameStatsBarBinding.root)

        updateHealthBar()
        updateGold()

        quizAnswer = binding.tvQuizAnswer
        quizAnswer.setOnClickListener {
            val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            service.showSoftInput(quizAnswer.rootView, 0)
        }

        binding.btnCheck.setOnClickListener {
            checkAnswer()
        }

        binding.tvQuizQuestion.visibility = View.INVISIBLE
        binding.tvQuizAnswer.visibility = View.INVISIBLE
        binding.btnCheck.visibility = View.INVISIBLE

        GameStateMachine.getInstance().init(this)
    }

    fun startQuiz() {
        quizTask = GameStats.getInstance().nextQuiz()
        if (quizTask != null) {
            binding.tvQuizQuestion.text = quizTask!!.question
            playerInput = ArrayList()
            fillAnswerField()

            binding.tvQuizQuestion.visibility = View.VISIBLE
            binding.tvQuizAnswer.visibility = View.VISIBLE
            binding.btnCheck.visibility = View.VISIBLE

            GameStateMachine.getInstance().switchState(StateMachineType.QUIZ)
        }
    }

    private fun endQuiz() {
        binding.tvQuizQuestion.visibility = View.INVISIBLE
        binding.tvQuizAnswer.visibility = View.INVISIBLE
        binding.btnCheck.visibility = View.INVISIBLE

        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(quizAnswer.rootView.applicationWindowToken, 0)

        GameStateMachine.getInstance().switchState(StateMachineType.CONTINUE_MOVING)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (quizTask == null || event == null) return true

        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            checkAnswer()
        } else if (playerInput.isNotEmpty() && (keyCode == KeyEvent.KEYCODE_DEL)) {
            playerInput.removeLast()
        } else {
            val char: Char = event.unicodeChar.toChar().lowercaseChar()
            if (char.isLetter()) {
                playerInput.add(char)
            }
        }

        fillAnswerField()

        return true
    }

    private fun fillAnswerField() {
        if (quizTask == null) return

        startIdx = quizTask!!.placeholder.indexOf("<")

        val empties = StringBuilder()
        for (i in 0 ..0.coerceAtLeast(MIN_ANSWER_SPACE - playerInput.size)) empties.append("\u00A0")

        val spanned = SpannableString(quizTask!!.placeholder.replace("<answer>", playerInput.joinToString("") + empties.toString()))
        spanned.setSpan(underline, startIdx, endIdx(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spanned.setSpan(colored, startIdx, endIdx(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        quizAnswer.text = spanned
    }

    private fun endIdx() =
        startIdx + MIN_ANSWER_SPACE.coerceAtLeast(playerInput.size)

    private fun checkAnswer() {
        if (quizTask == null) return

        var correct = false
        val answer = playerInput.joinToString("")
        quizTask!!.correct.forEach {
            if (it == answer) correct = true
        }

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