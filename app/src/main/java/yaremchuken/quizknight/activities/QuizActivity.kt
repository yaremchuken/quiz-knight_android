package yaremchuken.quizknight.activities

import android.annotation.SuppressLint
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
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yaremchuken.quizknight.App
import yaremchuken.quizknight.GameStateMachine
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.QuizTaskChecker
import yaremchuken.quizknight.R
import yaremchuken.quizknight.StateMachineType
import yaremchuken.quizknight.adapters.HealthBarAdapter
import yaremchuken.quizknight.adapters.QuizAnswerAssembleStringAdapter
import yaremchuken.quizknight.adapters.QuizAnswerWordOrEditableAdapter
import yaremchuken.quizknight.adapters.QuizProgressStarsAdapter
import yaremchuken.quizknight.databinding.ActivityQuizBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.model.ModuleLevel
import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizType
import yaremchuken.quizknight.providers.AnimationProvider
import yaremchuken.quizknight.providers.QuizzesProvider
import java.util.Locale

const val PROGRESS_BAR_SPEED = 2

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding

    private lateinit var tts: TextToSpeech

    private lateinit var level: ModuleLevel
    private lateinit var quizTask: QuizTask
    private var quizIdx = 0

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
            speakOut(quizTask.display)
        }
        binding.btnCheck.setOnClickListener {
            btnCheckClickListener()
        }
        binding.etBoardInput.addTextChangedListener {
            controlCheckBtnStatus(!it.isNullOrBlank())
        }
        binding.rgOptionsGroup.setOnCheckedChangeListener { _, _ ->
            controlCheckBtnStatus(true)
        }

        hideBoard()
        initLevel()
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

    private fun initLevel() {
        level = QuizzesProvider.getModuleLevels(GameStats.module)[GameStats.currentLevel.toInt()]
        AnimationProvider.preparePersonages(this@QuizActivity, listOf(PersonageType.HERO))
        AnimationProvider.preparePersonages(this@QuizActivity, level.opponents)
        randomizeOpponent()

        binding.pbQuizProgress.progress = 0
        val flexboxLayout = FlexboxLayoutManager(this)
        flexboxLayout.justifyContent = JustifyContent.SPACE_BETWEEN
        binding.rvQuizProgressStars.layoutManager = flexboxLayout

        val progress: MutableList<Boolean> = ArrayList()
        progress.add(false)
        for (task in level.tasks) { progress.add(false) }
        binding.rvQuizProgressStars.adapter = QuizProgressStarsAdapter(progress)

        GameStateMachine.registerActivity(this)
    }

    @SuppressLint("SetTextI18n")
    fun startQuiz() {
        quizTask = level.tasks[quizIdx]

        binding.tvQuizQuestion.text = quizTask.display

        if (quizTask.type != QuizType.WRITE_LISTENED_PHRASE &&
            quizTask.type != QuizType.INPUT_LISTENED_WORD_IN_STRING)
        {
            binding.tvQuizQuestion.visibility = View.VISIBLE
        }

        binding.pbQuizProgress.visibility = View.INVISIBLE
        binding.rvQuizProgressStars.visibility = View.INVISIBLE

        binding.llQuizBoard.visibility = View.VISIBLE

        binding.btnCheck.visibility = View.VISIBLE
        controlCheckBtnStatus(false)

        when (quizTask.type) {
            QuizType.WORD_TRANSLATION_INPUT -> {
                binding.rvQuizAnswerItems.visibility = View.VISIBLE
                binding.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.rvQuizAnswerItems.adapter =
                    QuizAnswerWordOrEditableAdapter(
                        this@QuizActivity, quizTask.options[0].split(" "))
            }

            QuizType.ASSEMBLE_TRANSLATION_STRING -> {
                binding.llAssembleString.visibility = View.VISIBLE
                binding.rvAssembleStringAnswer.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.rvAssembleStringOptions.layoutManager = FlexboxLayoutManager(this@QuizActivity)

                val words = quizTask.verifications[0].split(" ").toMutableList()
                words.addAll(quizTask.options)

                randomize(words)

                binding.rvAssembleStringOptions.adapter =
                    QuizAnswerAssembleStringAdapter(this@QuizActivity, words, "options")
                binding.rvAssembleStringAnswer.adapter =
                    QuizAnswerAssembleStringAdapter(this@QuizActivity, ArrayList(), "answer")
            }

            QuizType.CHOOSE_CORRECT_OPTION -> {
                binding.rgOptionsGroup.visibility = View.VISIBLE
                binding.rbOptionA.text = "A. ${quizTask.options[0]}"
                binding.rbOptionB.text = "B. ${quizTask.options[1]}"
                binding.rbOptionC.text = "C. ${quizTask.options[2]}"
                binding.rbOptionD.text = "D. ${quizTask.options[3]}"
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

                speakOut(quizTask.display)
            }

            QuizType.INPUT_LISTENED_WORD_IN_STRING -> {
                binding.ibQuizListenBtn.visibility = View.VISIBLE
                binding.rvQuizAnswerItems.visibility = View.VISIBLE
                binding.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.rvQuizAnswerItems.adapter =
                    QuizAnswerWordOrEditableAdapter(
                        this@QuizActivity, quizTask.options[0].split(" "))

                speakOut(quizTask.display)
            }
        }

        GameStateMachine.switchState(StateMachineType.QUIZ)
    }

    private fun endQuiz() {
        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(binding.rvQuizAnswerItems.rootView.applicationWindowToken, 0)

        if (quizTask.type == QuizType.CHOOSE_CORRECT_OPTION) {
            binding.rbOptionA.isChecked = false
            binding.rbOptionB.isChecked = false
            binding.rbOptionC.isChecked = false
            binding.rbOptionD.isChecked = false
        }
        binding.etBoardInput.text = null

        controlCheckBtnStatus(false)
        hideBoard()

        lifecycleScope.launch {
            var progress = ((100 / level.tasks.size-1) * quizIdx).toDouble()
            val target = ((100 / level.tasks.size-1) * (quizIdx+1)).toDouble()

            while (progress < target) {
                progress += PROGRESS_BAR_SPEED
                if (progress > target) {
                    progress = target
                }
                binding.pbQuizProgress.progress = progress.toInt()
                withContext(Dispatchers.IO) {
                    Thread.sleep(25)
                }
            }

            val stars: MutableList<Boolean> = ArrayList()
            stars.add(false)
            for (i in 0 until level.tasks.size) {
                stars.add(i <= quizIdx)
            }
            binding.rvQuizProgressStars.adapter = QuizProgressStarsAdapter(stars)

            if (quizIdx == level.tasks.size-1) {
                completeLevel()
            } else {
                quizIdx++
                randomizeOpponent()
                GameStateMachine.switchState(StateMachineType.CONTINUE_MOVING)
            }
        }

        binding.pbQuizProgress.visibility = View.VISIBLE
        binding.rvQuizProgressStars.visibility = View.VISIBLE
    }

    private fun randomizeOpponent() {
        GameStats.opponent = level.opponents[(Math.random() * level.opponents.size).toInt()]
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
        when (quizTask.type) {
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
            else -> throw RuntimeException("Unknown quiz type ${quizTask.type}")
        }
    }

    private fun btnCheckClickListener() {
        if (GameStateMachine.state == StateMachineType.COMPLETED) {
            onBackPressed()
            return
        }

        val answer = when (quizTask.type) {
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
        }

        if (QuizTaskChecker.checkAnswer(quizTask, answer)) {
            endQuiz()
        } else {
            GameStats.dropHeart()
            updateHealthBar()
        }
    }

    private fun updateHealthBar() {
        val healths: ArrayList<Boolean> = ArrayList(GameStats.maxHealth.toInt())
        for (i in 0 until GameStats.maxHealth) {
            healths.add(i < GameStats.health)
        }
        gameStatsBarBinding.rvHearts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        gameStatsBarBinding.rvHearts.adapter = HealthBarAdapter(healths)
    }

    private fun updateGold() {
        gameStatsBarBinding.tvGold.text = GameStats.gold.toString()
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun controlCheckBtnStatus(enabled: Boolean) {
        binding.btnCheck.isEnabled = enabled
        binding.btnCheck.alpha = if (enabled) 1F else .2F
    }

    private fun completeLevel() {
        GameStateMachine.switchState(StateMachineType.COMPLETED)

        lifecycleScope.launch {
            val moduleProgress =
                (application as App)
                    .db
                    .getModuleProgressDao()
                    .fetch(GameStats.game, GameStats.module)[0]

            if (moduleProgress.progress >= GameStats.currentLevel) {
                GameStats.currentLevel = -1
            }

            binding.tvLevelTribute.text = "+${level.tribute}"
            binding.llLevelCompleted.visibility = View.VISIBLE
            binding.btnCheck.text = resources.getString(R.string.complete_btn_title)
            controlCheckBtnStatus(true)
            binding.btnCheck.visibility = View.VISIBLE

            val gold = GameStats.gold + level.tribute

            (application as App)
                .db
                .getGameStatsDao()
                .updateGold(GameStats.game, gold)

            GameStats.gold = gold
            gameStatsBarBinding.tvGold.text = gold.toString()
        }
    }

    override fun onBackPressed() {
        if (quizIdx != level.tasks.size-1) {
            GameStats.currentLevel = -1
        }
        if (GameStateMachine.state != StateMachineType.EMPTY) {
            GameStateMachine.stopMachine()
        }
        super.onBackPressed()
    }
}