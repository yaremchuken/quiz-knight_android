package yaremchuken.quizknight.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.QuizTaskChecker
import yaremchuken.quizknight.StateMachineType
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.adapters.QuizAnswerAssembleStringAdapter
import yaremchuken.quizknight.adapters.QuizAnswerWordOrEditableAdapter
import yaremchuken.quizknight.databinding.ActivityQuizBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.entity.QuizTaskEntity
import yaremchuken.quizknight.entity.QuizType
import java.util.Locale

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding

    private lateinit var tts: TextToSpeech

    private var quizTaskEntity: QuizTaskEntity? = null

    private lateinit var quizzes: List<QuizTaskEntity>

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
            if (quizTaskEntity != null) {
                speakOut(quizTaskEntity!!.display)
            }
        }
        binding.btnCheck.setOnClickListener {
            checkAnswer()
        }
        binding.etBoardInput.addTextChangedListener {
            controlCheckBtnStatus(!it.isNullOrBlank())
        }
        binding.rgOptionsGroup.setOnCheckedChangeListener { _, _ ->
            controlCheckBtnStatus(true)
        }

        GameStateMachine.getInstance().init(this)

        hideBoard()
        initQuizzes()
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

    private fun initQuizzes() {
        val dao = (application as App).db.getQuizTaskDao()
        lifecycleScope.launch {
            dao.fetch(GameStats.getInstance().module, GameStats.getInstance().currentLevel)
                .collect {
                    quizzes = it
                    GameStateMachine.getInstance().startMachine()
                }
        }
    }

    @SuppressLint("SetTextI18n")
    fun startQuiz() {
        quizTaskEntity = quizzes.find { it.order == (quizTaskEntity?.order ?: 0) + 1 }

        binding.tvQuizQuestion.text = quizTaskEntity?.display

        if (quizTaskEntity?.type != QuizType.WRITE_LISTENED_PHRASE &&
            quizTaskEntity?.type != QuizType.INPUT_LISTENED_WORD_IN_STRING)
        {
            binding.tvQuizQuestion.visibility = View.VISIBLE
        }

        binding.llQuizBoard.visibility = View.VISIBLE

        binding.btnCheck.visibility = View.VISIBLE
        controlCheckBtnStatus(false)

        when (quizTaskEntity?.type) {
            QuizType.WORD_TRANSLATION_INPUT -> {
                binding.rvQuizAnswerItems.visibility = View.VISIBLE
                binding.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.rvQuizAnswerItems.adapter =
                    QuizAnswerWordOrEditableAdapter(
                        this@QuizActivity, quizTaskEntity!!.options[0].split(" "))
            }

            QuizType.ASSEMBLE_TRANSLATION_STRING -> {
                binding.llAssembleString.visibility = View.VISIBLE
                binding.rvAssembleStringAnswer.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.rvAssembleStringOptions.layoutManager = FlexboxLayoutManager(this@QuizActivity)

                val words = quizTaskEntity!!.verifications[0].split(" ").toMutableList()
                words.addAll(quizTaskEntity!!.options)

                randomize(words)

                binding.rvAssembleStringOptions.adapter =
                    QuizAnswerAssembleStringAdapter(this@QuizActivity, words, "options")
                binding.rvAssembleStringAnswer.adapter =
                    QuizAnswerAssembleStringAdapter(this@QuizActivity, ArrayList(), "answer")
            }

            QuizType.CHOOSE_CORRECT_OPTION -> {
                binding.rgOptionsGroup.visibility = View.VISIBLE
                binding.rbOptionA.text = "A. ${quizTaskEntity!!.options[0]}"
                binding.rbOptionB.text = "B. ${quizTaskEntity!!.options[1]}"
                binding.rbOptionC.text = "C. ${quizTaskEntity!!.options[2]}"
                binding.rbOptionD.text = "D. ${quizTaskEntity!!.options[3]}"
            }

            QuizType.WRITE_LISTENED_PHRASE -> {
                binding.ibQuizListenBtn.visibility = View.VISIBLE
                binding.tilBoardInput.visibility = View.VISIBLE

                binding.etBoardInput.postDelayed(Runnable {
                    binding.etBoardInput.dispatchTouchEvent(
                        MotionEvent.obtain(
                            SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0F, 0F, 0))
                    binding.etBoardInput.dispatchTouchEvent(
                        MotionEvent.obtain(
                            SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0F, 0F, 0))
                }, 200)

                speakOut(quizTaskEntity!!.display)
            }

            QuizType.INPUT_LISTENED_WORD_IN_STRING -> {
                binding.ibQuizListenBtn.visibility = View.VISIBLE
                binding.rvQuizAnswerItems.visibility = View.VISIBLE
                binding.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.rvQuizAnswerItems.adapter =
                    QuizAnswerWordOrEditableAdapter(
                        this@QuizActivity, quizTaskEntity!!.options[0].split(" "))

                speakOut(quizTaskEntity!!.display)
            }

            else -> throw RuntimeException("Unknown quiz type ${quizTaskEntity?.type}")
        }

        GameStateMachine.getInstance().switchState(StateMachineType.QUIZ)
    }

    private fun endQuiz() {
        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(binding.rvQuizAnswerItems.rootView.applicationWindowToken, 0)

        if (quizTaskEntity?.type == QuizType.CHOOSE_CORRECT_OPTION) {
            binding.rbOptionA.isChecked = false
            binding.rbOptionB.isChecked = false
            binding.rbOptionC.isChecked = false
            binding.rbOptionD.isChecked = false
        }
        binding.etBoardInput.text = null

        controlCheckBtnStatus(false)
        hideBoard()

        if (quizTaskEntity?.order == quizzes[quizzes.size-1].order) {
            completeLevel()
        } else {
            GameStateMachine.getInstance().switchState(StateMachineType.CONTINUE_MOVING)
        }
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

    private fun randomize(array: MutableList<String>) {
        var tmp: String
        var rnd: Int
        for (i in array.indices) {
            rnd = (Math.random() * array.size).toInt()
            tmp = array[rnd]
            array[rnd] = array[i]
            array[i] = tmp
        }
    }

    fun adaptersExchanger(
        from: QuizAnswerAssembleStringAdapter,
        position: Int,
        direction: String
    ) {
        when (quizTaskEntity?.type) {
            QuizType.ASSEMBLE_TRANSLATION_STRING -> {
                val changed = from.items.removeAt(position)
                val answerAdapter = (binding.rvAssembleStringAnswer.adapter as QuizAnswerAssembleStringAdapter)
                val optionsAdapter = (binding.rvAssembleStringOptions.adapter as QuizAnswerAssembleStringAdapter)

                if (direction == "options") {
                    answerAdapter.items.add(changed)
                } else {
                    optionsAdapter.items.add(changed)
                }

                optionsAdapter.notifyDataSetChanged()
                answerAdapter.notifyDataSetChanged()

                controlCheckBtnStatus(answerAdapter.items.size > 0)
            }
            else -> throw RuntimeException("Unknown quiz type ${quizTaskEntity?.type}")
        }
    }

    private fun checkAnswer() {
        if (quizTaskEntity == null) return

        val answer = when (quizTaskEntity?.type) {
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

            else -> throw RuntimeException("Unknown quiz type ${quizTaskEntity?.type}")
        }

        if (QuizTaskChecker.checkAnswer(quizTaskEntity!!, answer)) {
            endQuiz()
        } else {
            GameStats.getInstance().dropHeart()
            updateHealthBar()
        }
    }

    private fun updateHealthBar() {
        val healths: ArrayList<Boolean> = ArrayList(GameStats.getInstance().maxHealth.toInt())
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

    private fun completeLevel() {
        val dao = (application as App).db.getModuleProgressDao()
        lifecycleScope.launch {
            dao.fetch(GameStats.getInstance().game).collect {
                val progress = it.find { gs -> gs.module == GameStats.getInstance().module }
                val existedProgress = progress?.progress ?: 0
                if (existedProgress < GameStats.getInstance().currentLevel) {
                    dao.updateProgress(
                        GameStats.getInstance().game,
                        GameStats.getInstance().module,
                        GameStats.getInstance().currentLevel
                    )
                    backToCity()
                } else {
                    backToCity()
                }
            }
        }
    }

    private fun backToCity() {
        GameStats.getInstance().currentLevel = -1
        val intent = Intent(this, CityActivity::class.java)
        startActivity(intent)
    }
}