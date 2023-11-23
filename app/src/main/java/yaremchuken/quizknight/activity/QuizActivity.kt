package yaremchuken.quizknight.activity

import android.annotation.SuppressLint
import android.app.Dialog
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
import yaremchuken.quizknight.adapter.DictionaryDialogAdapter
import yaremchuken.quizknight.adapter.HealthBarAdapter
import yaremchuken.quizknight.adapter.QuizAnswerAssembleStringAdapter
import yaremchuken.quizknight.adapter.QuizWordOrEditableAdapter
import yaremchuken.quizknight.adapter.TranslationDialogAdapter
import yaremchuken.quizknight.api.yandex.dictionary.YaDictionaryClient
import yaremchuken.quizknight.api.yandex.translate.YaTranslateClient
import yaremchuken.quizknight.databinding.ActivityQuizBinding
import yaremchuken.quizknight.databinding.DialogDictionaryBinding
import yaremchuken.quizknight.databinding.DialogTranslationBinding
import yaremchuken.quizknight.databinding.FragmentGameStatsBarBinding
import yaremchuken.quizknight.fragment.QuizLevelCompletedFragment
import yaremchuken.quizknight.model.ModuleLevel
import yaremchuken.quizknight.model.QuizTask
import yaremchuken.quizknight.model.QuizType
import yaremchuken.quizknight.model.QuizType.ASSEMBLE_TRANSLATION_STRING
import yaremchuken.quizknight.model.QuizType.CHOOSE_CORRECT_OPTION
import yaremchuken.quizknight.model.QuizType.INPUT_LISTENED_WORD_IN_STRING
import yaremchuken.quizknight.model.QuizType.WORD_TRANSLATION_INPUT
import yaremchuken.quizknight.model.QuizType.WRITE_LISTENED_PHRASE
import yaremchuken.quizknight.provider.AnimationProvider
import yaremchuken.quizknight.provider.QuizzesProvider
import yaremchuken.quizknight.utils.StringUtils
import java.util.Locale

const val PROGRESS_BAR_ANIMATION_SPEED = 4

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var gameStatsBarBinding: FragmentGameStatsBarBinding

    private lateinit var tts: TextToSpeech

    private lateinit var translateClient: YaTranslateClient
    private lateinit var dialogTranslationBinding: DialogTranslationBinding

    private lateinit var dictionaryClient: YaDictionaryClient
    private lateinit var dialogDictionaryBinding: DialogDictionaryBinding

    private lateinit var quizLevelCompleted: QuizLevelCompletedFragment

    private lateinit var level: ModuleLevel
    private lateinit var quizTask: QuizTask
    private var quizIdx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameStatsBarBinding = FragmentGameStatsBarBinding.inflate(layoutInflater)
        binding.rlStatsBar.addView(gameStatsBarBinding.root)

        quizLevelCompleted = supportFragmentManager.findFragmentById(R.id.fcwQuizCompleted) as QuizLevelCompletedFragment

        updateHealthBar()
        updateGold()

        tts = TextToSpeech(this, this)
        translateClient = YaTranslateClient(resources.getString(R.string.YA_TRANSLATE_API_KEY))
        dictionaryClient = YaDictionaryClient(resources.getString(R.string.YA_DICTIONARY_API_KEY))

        binding.incQuestionArea.ibQuizListenBtn.setOnClickListener {
            speakOut(quizTask.display)
        }
        binding.btnCheck.setOnClickListener {
            btnCheckClickListener()
        }
        binding.incPhrase.etBoardInput.addTextChangedListener {
            controlCheckBtnStatus(!it.isNullOrBlank())
        }
        binding.incOption.rgOptionsGroup.setOnCheckedChangeListener { _, _ ->
            controlCheckBtnStatus(true)
        }

        hideBoard()
        initLevel()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val lang = tts.setLanguage(GameStats.studied)
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

        binding.incProgressBar.pbQuizProgress.progress = 0
        binding.incProgressBar.pbQuizProgress.visibility = View.INVISIBLE

        GameStateMachine.registerActivity(this)
    }

    fun startQuiz() {
        quizTask = level.tasks[quizIdx]

        fillQuizQuestionArea()

        binding.incProgressBar.pbQuizProgress.visibility = View.INVISIBLE

        binding.llQuizBoard.visibility = View.VISIBLE

        binding.btnCheck.visibility = View.VISIBLE

        fillQuizAnswerBoard()

        controlCheckBtnStatus(false)

        GameStateMachine.switchState(StateMachineType.QUIZ)
    }

    private fun fillQuizQuestionArea() {
        binding.incQuestionArea.root.visibility = View.VISIBLE
        if (QuizType.isAudition(quizTask.type)) {
            binding.incQuestionArea.ibQuizListenBtn.visibility = View.VISIBLE
        } else {
            binding.incQuestionArea.llWordsDisplay.visibility = View.VISIBLE
            binding.incQuestionArea.rvWordItems.layoutManager = FlexboxLayoutManager(this@QuizActivity)
            binding.incQuestionArea.rvWordItems.adapter =
                QuizWordOrEditableAdapter(
                    this@QuizActivity,
                    quizTask.display.split(" "),
                    if (quizTask.type == WORD_TRANSLATION_INPUT) GameStats.original else GameStats.studied,
                    resources.getColor(R.color.white))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillQuizAnswerBoard() {
        when (quizTask.type) {
            WORD_TRANSLATION_INPUT -> {
                binding.incWord.rvQuizAnswerItems.visibility = View.VISIBLE
                binding.incWord.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.incWord.rvQuizAnswerItems.adapter =
                    QuizWordOrEditableAdapter(
                        this@QuizActivity,
                        quizTask.options[0].split(" "),
                        GameStats.studied)
            }

            ASSEMBLE_TRANSLATION_STRING -> {
                binding.incAssemble.llAssembleString.visibility = View.VISIBLE
                binding.incAssemble.rvAssembleStringAnswer.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.incAssemble.rvAssembleStringOptions.layoutManager = FlexboxLayoutManager(this@QuizActivity)

                val words = quizTask.verifications[0].split(" ").toMutableList()
                words.addAll(quizTask.options)

                randomize(words)

                binding.incAssemble.rvAssembleStringOptions.adapter =
                    QuizAnswerAssembleStringAdapter(this@QuizActivity, words, "options")
                binding.incAssemble.rvAssembleStringAnswer.adapter =
                    QuizAnswerAssembleStringAdapter(this@QuizActivity, ArrayList(), "answer")
            }

            CHOOSE_CORRECT_OPTION -> {
                binding.incOption.rgOptionsGroup.visibility = View.VISIBLE
                binding.incOption.rbOptionA.text = "A. ${quizTask.options[0]}"
                binding.incOption.rbOptionB.text = "B. ${quizTask.options[1]}"
                binding.incOption.rbOptionC.text = "C. ${quizTask.options[2]}"
                binding.incOption.rbOptionD.text = "D. ${quizTask.options[3]}"
            }

            WRITE_LISTENED_PHRASE -> {
                binding.incPhrase.root.visibility = View.VISIBLE
                binding.incPhrase.etBoardInput.postDelayed(Runnable {
                    binding.incPhrase.etBoardInput.dispatchTouchEvent(
                        MotionEvent.obtain(
                            SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0F, 0F, 0))
                    binding.incPhrase.etBoardInput.dispatchTouchEvent(
                        MotionEvent.obtain(
                            SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0F, 0F, 0))
                }, 200)

                speakOut(quizTask.display)
            }

            INPUT_LISTENED_WORD_IN_STRING -> {
                binding.incWord.rvQuizAnswerItems.visibility = View.VISIBLE
                binding.incWord.rvQuizAnswerItems.layoutManager = FlexboxLayoutManager(this@QuizActivity)
                binding.incWord.rvQuizAnswerItems.adapter =
                    QuizWordOrEditableAdapter(
                        this@QuizActivity,
                        quizTask.options[0].split(" "),
                        GameStats.studied)

                speakOut(quizTask.display)
            }
        }
    }

    private fun endQuiz() {
        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(binding.incWord.rvQuizAnswerItems.rootView.applicationWindowToken, 0)

        if (quizTask.type == CHOOSE_CORRECT_OPTION) {
            binding.incOption.rbOptionA.isChecked = false
            binding.incOption.rbOptionB.isChecked = false
            binding.incOption.rbOptionC.isChecked = false
            binding.incOption.rbOptionD.isChecked = false
        }
        binding.incPhrase.etBoardInput.text = null

        controlCheckBtnStatus(false)
        hideBoard()

        lifecycleScope.launch {
            animateProgressBar()
            checkLevelCompleted()
        }

        binding.incProgressBar.pbQuizProgress.visibility = View.VISIBLE
    }

    private suspend fun animateProgressBar() {
        val step = 100.0 / level.tasks.size
        var current = step * quizIdx
        val target = current + step

        while (current < target) {
            current += PROGRESS_BAR_ANIMATION_SPEED
            if (current > target) {
                current = target
            }
            binding.incProgressBar.pbQuizProgress.progress = current.toInt()
            withContext(Dispatchers.IO) {
                Thread.sleep(25)
            }
        }
    }

    private fun checkLevelCompleted() {
        if (quizIdx == level.tasks.size-1) {
            completeLevel()
        } else {
            quizIdx++
            randomizeOpponent()
            GameStateMachine.switchState(StateMachineType.CONTINUE_MOVING)
        }
    }

    private fun hideBoard() {
        binding.incQuestionArea.root.visibility = View.INVISIBLE
        binding.incQuestionArea.ibQuizListenBtn.visibility = View.GONE
        binding.incQuestionArea.llWordsDisplay.visibility = View.GONE

        binding.llQuizBoard.visibility = View.INVISIBLE
        binding.incWord.rvQuizAnswerItems.visibility = View.GONE
        binding.incOption.rgOptionsGroup.visibility = View.GONE
        binding.incAssemble.llAssembleString.visibility = View.GONE
        binding.incPhrase.root.visibility = View.GONE

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
            ASSEMBLE_TRANSLATION_STRING -> {
                val changed = from.items.removeAt(position)
                val answerAdapter = (binding.incAssemble.rvAssembleStringAnswer.adapter as QuizAnswerAssembleStringAdapter)
                val optionsAdapter = (binding.incAssemble.rvAssembleStringOptions.adapter as QuizAnswerAssembleStringAdapter)

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
            WORD_TRANSLATION_INPUT -> {
                (binding.incWord.rvQuizAnswerItems.adapter as QuizWordOrEditableAdapter).playerInput
            }
            CHOOSE_CORRECT_OPTION -> {
                findViewById<RadioButton>(binding.incOption.rgOptionsGroup.checkedRadioButtonId).text.toString()
            }
            ASSEMBLE_TRANSLATION_STRING -> {
                (binding.incAssemble.rvAssembleStringAnswer.adapter as QuizAnswerAssembleStringAdapter).items.joinToString(" ")
            }
            WRITE_LISTENED_PHRASE -> {
                binding.incPhrase.etBoardInput.text.toString()
            }
            INPUT_LISTENED_WORD_IN_STRING -> {
                (binding.incWord.rvQuizAnswerItems.adapter as QuizWordOrEditableAdapter).playerInput
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

    fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun showTranslationDialog(texts: Array<String>) {
        if (texts.isEmpty()) return

        val dialog = Dialog(this)

        dialogTranslationBinding = DialogTranslationBinding.inflate(layoutInflater)
        dialog.setContentView(dialogTranslationBinding.root)

        dialogTranslationBinding.rvTranslations.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        dialogTranslationBinding.btnBack.setOnClickListener { dialog.dismiss()}

        dialog.show()

        lifecycleScope.launch {
            try {
                val translations = translateClient.translate(texts, GameStats.studied, GameStats.original)
                dialogTranslationBinding.llLoader.visibility = View.GONE
                dialogTranslationBinding.tvErrorMessage.visibility = View.GONE
                val sorted = translations.toList().sortedBy { it.first.length }
                dialogTranslationBinding.rvTranslations.adapter = TranslationDialogAdapter(this@QuizActivity, sorted)
            } catch (ex: Exception) {
                dialogTranslationBinding.llLoader.visibility = View.GONE
                dialogTranslationBinding.rvTranslations.visibility = View.GONE
                dialogTranslationBinding.tvErrorMessage.text = getString(R.string.translation_not_available)
            }
        }
    }

    fun showDictionaryDialog(text: String, sourceLang: Locale) {
        val clearedText = StringUtils.onlyLetters(text)
        if (clearedText.isBlank()) return

        val dialog = Dialog(this)

        dialogDictionaryBinding = DialogDictionaryBinding.inflate(layoutInflater)
        dialogDictionaryBinding.btnBack.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(dialogDictionaryBinding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val isOriginal = sourceLang == GameStats.original

        lifecycleScope.launch {
            try {
                val translations =
                    dictionaryClient.lookup(
                        clearedText,
                        sourceLang,
                        if (isOriginal) GameStats.studied else GameStats.original)

                dialogDictionaryBinding.llLoader.visibility = View.GONE

                if (translations.isEmpty()) {
                    dialogDictionaryBinding.rvDictionaryEntities.visibility = View.GONE
                    dialogDictionaryBinding.tvErrorMessage.text = getString(R.string.translation_not_found)
                } else {
                    dialogDictionaryBinding.tvErrorMessage.visibility = View.GONE
                    dialogDictionaryBinding.rvDictionaryEntities.layoutManager =
                        LinearLayoutManager(this@QuizActivity, LinearLayoutManager.VERTICAL, false)
                    dialogDictionaryBinding.rvDictionaryEntities.adapter =
                        DictionaryDialogAdapter(this@QuizActivity, translations, !isOriginal)
                }
            } catch (ex: Exception) {
                dialogDictionaryBinding.rvDictionaryEntities.visibility = View.GONE
                dialogDictionaryBinding.tvErrorMessage.text = getString(R.string.translation_not_available)
            }
        }
    }

    fun controlCheckBtnStatus(enabled: Boolean) {
        binding.btnCheck.isEnabled = enabled
        binding.btnCheck.alpha = if (enabled) 1F else .2F
    }

    private fun completeLevel() {
        GameStateMachine.switchState(StateMachineType.COMPLETED)
        GameStats.currentLevel = -1

        val gold = GameStats.gold + level.tribute

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                (application as App)
                    .db
                    .getGameStatsDao()
                    .updateGold(GameStats.game, gold)

                updateModuleProgress()
            }

            quizLevelCompleted.setTribute(level.tribute)
            quizLevelCompleted.requireView().visibility = View.VISIBLE
            quizLevelCompleted.runAnimations()

            binding.btnCheck.text = resources.getString(R.string.complete_btn_title)
            controlCheckBtnStatus(true)
            binding.btnCheck.visibility = View.VISIBLE


            GameStats.gold = gold
            gameStatsBarBinding.tvGold.text = gold.toString()
        }
    }

    private fun randomizeOpponent() {
        GameStats.opponent = level.opponents[(Math.random() * level.opponents.size).toInt()]
    }

    /**
     * Check if we have to update player progress for that module, or he just replayed old one.
     */
    private suspend fun updateModuleProgress() {
        val moduleProgress =
            (application as App)
                .db
                .getModuleProgressDao()
                .fetch(GameStats.game, GameStats.module)[0]

        if (GameStats.currentLevel > moduleProgress.progress) {
            withContext(Dispatchers.IO) {
                (application as App)
                    .db
                    .getModuleProgressDao()
                    .updateProgress(
                        GameStats.game,
                        GameStats.module,
                        GameStats.currentLevel
                    )
            }
            GameStats.updateProgress()
        }
    }

    override fun onBackPressed() {
        if (GameStateMachine.state != StateMachineType.EMPTY) {
            GameStateMachine.stopMachine()
        }
        super.onBackPressed()
    }
}