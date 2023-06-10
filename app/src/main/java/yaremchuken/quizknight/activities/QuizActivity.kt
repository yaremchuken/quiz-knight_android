package yaremchuken.quizknight.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.QuizProvider
import yaremchuken.quizknight.QuizTaskChecker
import yaremchuken.quizknight.StateMachineType
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.adapters.QuizAnswerAssembleStringAdapter
import yaremchuken.quizknight.adapters.QuizAnswerWordOrEditableAdapter
import yaremchuken.quizknight.databinding.ActivityQuizBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizTaskAssembleString
import yaremchuken.quizknight.model.QuizTaskChooseOption
import yaremchuken.quizknight.model.QuizTaskInputListenedWord
import yaremchuken.quizknight.model.QuizTaskTranslateWord
import yaremchuken.quizknight.model.QuizType
import java.util.Locale

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding

    private lateinit var tts: TextToSpeech

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

        tts = TextToSpeech(this, this)
        binding.ibQuizListenBtn.setOnClickListener {
            if (quizTask != null) {
                speakOut(quizTask!!.question)
            }
        }

        binding.btnCheck.setOnClickListener {
            checkAnswer()
        }

        hideBoard()
        GameStateMachine.getInstance().init(this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val lang = tts.setLanguage(Locale.US)
            if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported!")
            }
        } else {
            Log.e("TTS", "Initialization failed!")
        }
    }

    @SuppressLint("SetTextI18n")
    fun startQuiz() {
        quizTask = QuizProvider.getInstance().nextQuiz()
        if (quizTask != null) {
            binding.tvQuizQuestion.text = quizTask?.question

            if (quizTask?.type != QuizType.WRITE_LISTENED_PHRASE &&
                quizTask?.type != QuizType.INPUT_LISTENED_WORD_IN_STRING)
            {
                binding.tvQuizQuestion.visibility = View.VISIBLE
            }

            binding.llQuizBoard.visibility = View.VISIBLE

            binding.btnCheck.visibility = View.VISIBLE
            controlCheckBtnStatus(false)

            when (quizTask?.type) {
                QuizType.WORD_TRANSLATION_INPUT -> {
                    binding.rvQuizAnswerItems.visibility = View.VISIBLE
                    binding.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this)
                    binding.rvQuizAnswerItems.adapter =
                        QuizAnswerWordOrEditableAdapter(this,
                            (quizTask as QuizTaskTranslateWord).placeholder.split(" "))
                }

                QuizType.ASSEMBLE_TRANSLATION_STRING -> {
                    binding.llAssembleString.visibility = View.VISIBLE
                    binding.rvAssembleStringAnswer.layoutManager = FlexboxLayoutManager(this)
                    binding.rvAssembleStringOptions.layoutManager = FlexboxLayoutManager(this)

                    val split = ArrayList((quizTask as QuizTaskAssembleString).verifications[0].split(" "))
                    randomize(split)
                    binding.rvAssembleStringOptions.adapter =
                        QuizAnswerAssembleStringAdapter(this, split, "options")
                    binding.rvAssembleStringAnswer.adapter =
                        QuizAnswerAssembleStringAdapter(this, ArrayList(), "answer")
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

                QuizType.WRITE_LISTENED_PHRASE -> {
                    binding.ibQuizListenBtn.visibility = View.VISIBLE
                    binding.tilBoardInput.visibility = View.VISIBLE

                    speakOut(quizTask!!.question)
                }

                QuizType.INPUT_LISTENED_WORD_IN_STRING -> {
                    binding.ibQuizListenBtn.visibility = View.VISIBLE
                    binding.rvQuizAnswerItems.visibility = View.VISIBLE
                    binding.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this)
                    binding.rvQuizAnswerItems.adapter =
                        QuizAnswerWordOrEditableAdapter(this,
                            (quizTask as QuizTaskInputListenedWord).placeholder.split(" "))

                    speakOut(quizTask!!.question)
                }

                else -> throw RuntimeException("Unknown quiz type ${quizTask?.type}")
            }

            GameStateMachine.getInstance().switchState(StateMachineType.QUIZ)
        }
    }

    private fun endQuiz() {
        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(binding.rvQuizAnswerItems.rootView.applicationWindowToken, 0)

        hideBoard()
        GameStateMachine.getInstance().switchState(StateMachineType.CONTINUE_MOVING)
    }

    private fun hideBoard() {
        binding.tvQuizQuestion.visibility = View.INVISIBLE
        binding.ibQuizListenBtn.visibility = View.INVISIBLE

        binding.llQuizBoard.visibility = View.INVISIBLE
        binding.rvQuizAnswerItems.visibility = View.GONE
        binding.rgOptionsGroup.visibility = View.GONE
        binding.llAssembleString.visibility = View.GONE
        binding.tilBoardInput.visibility = View.GONE

        binding.btnCheck.visibility = View.INVISIBLE
    }

    private fun randomize(array: ArrayList<String>) {
        var tmp: String
        var rnd: Int
        for (i in array.indices) {
            rnd = (Math.random() * array.size).toInt()
            tmp = array[rnd]
            array[rnd] = array[i]
            array[i] = tmp
        }
    }

    fun adapterExchangeListener(
        from: QuizAnswerAssembleStringAdapter,
        position: Int,
        direction: String
    ) {
        when (quizTask?.type) {
            QuizType.ASSEMBLE_TRANSLATION_STRING -> {
                val changed = from.items.removeAt(position)
                if (direction == "options") {
                    (binding.rvAssembleStringAnswer.adapter as QuizAnswerAssembleStringAdapter).items.add(changed)
                } else {
                    (binding.rvAssembleStringOptions.adapter as QuizAnswerAssembleStringAdapter).items.add(changed)
                }
                binding.rvAssembleStringOptions.adapter?.notifyDataSetChanged()
                binding.rvAssembleStringAnswer.adapter?.notifyDataSetChanged()

            }
            else -> throw RuntimeException("Unknown quiz type ${quizTask?.type}")
        }
    }

    private fun checkAnswer() {
        if (quizTask == null) return

        val answer = when (quizTask?.type) {
            QuizType.WORD_TRANSLATION_INPUT -> {
                (binding.rvQuizAnswerItems.adapter as QuizAnswerWordOrEditableAdapter).playerInput
            }
            QuizType.CHOOSE_CORRECT_OPTION -> {
                findViewById<RadioButton>(binding.rgOptionsGroup.checkedRadioButtonId).text.toString()
            }
            QuizType.ASSEMBLE_TRANSLATION_STRING -> {
                (binding.rvAssembleStringAnswer.adapter as QuizAnswerAssembleStringAdapter).items.joinToString(" ")
            }
            QuizType.WRITE_LISTENED_PHRASE -> {
                binding.etBoardInput.text.toString()
            }
            QuizType.INPUT_LISTENED_WORD_IN_STRING -> {
                (binding.rvQuizAnswerItems.adapter as QuizAnswerWordOrEditableAdapter).playerInput
            }

            else -> throw RuntimeException("Unknown quiz type ${quizTask?.type}")
        }

        if (QuizTaskChecker.checkAnswer(quizTask!!, answer)) {
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

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun controlCheckBtnStatus(enabled: Boolean) {
        binding.btnCheck.isEnabled = enabled
        binding.btnCheck.alpha = if (enabled) 1F else .2F
    }
}