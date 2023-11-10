package yaremchuken.quizknight.adapter

import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.QuizActivity
import yaremchuken.quizknight.databinding.ItemQuizWordOrEditableBinding
import java.util.Locale

class QuizWordOrEditableAdapter(
    private val activity: QuizActivity,
    private val items: List<String>,
    private val lang: Locale,
    private val textColor: Int? = null
): RecyclerView.Adapter<QuizWordOrEditableAdapter.ViewHolder>() {

    companion object {
        private const val SENTENCES_TO_TRANSLATE_SIZE = 3
    }

    var playerInput: String = ""

    class ViewHolder(binding: ItemQuizWordOrEditableBinding): RecyclerView.ViewHolder(binding.root) {
        val word = binding.tvWord
        val input = binding.tvInput
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder =
            ViewHolder(ItemQuizWordOrEditableBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        holder.input.addTextChangedListener {
            playerInput = it.toString()
        }
        return holder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = items[position]
        if (word == "<answer>") {
            holder.word.text = ""
            holder.word.visibility = View.GONE
            holder.input.visibility = View.VISIBLE
            holder.input.addTextChangedListener {
                activity.controlCheckBtnStatus(!it.isNullOrBlank())
            }
            holder.input.postDelayed(Runnable {
                holder.input.dispatchTouchEvent(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0F, 0F, 0))
                holder.input.dispatchTouchEvent(
                    MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0F, 0F, 0))
            }, 200)
        } else {
            holder.word.text = word
            holder.word.visibility = View.VISIBLE
            holder.input.visibility = View.GONE
            holder.word.setOnClickListener {
                activity.showDictionaryDialog(word, lang)
            }
        }
        if (textColor != null) holder.word.setTextColor(textColor)
    }

    /**
     * Collect sentences to translate.
     * If sentence is "Happy day to all of you!" user clicks "all" then we need translation for strings:
     * ["all", "all of", "all of you", ... etc. until SENTENCES_TO_TRANSLATE_SIZE]
     */
    private fun collectTextsToTranslate(startedIdx: Int): Array<String> {
        if (isPunctuation(items[startedIdx])) return arrayOf()

        val sentences: MutableList<String> = ArrayList()
        sentences.add(items[startedIdx])

        for (i in startedIdx+1 until startedIdx + SENTENCES_TO_TRANSLATE_SIZE) {
            if (i == items.size || items[i] == "<answer>" || isPunctuation(items[i])) break
            sentences.add("${sentences[sentences.size-1]} ${items[i]}")
        }

        return sentences.toTypedArray()
    }

    private fun isPunctuation(text: String) = text.trim().length == 1 && !text.trim().single().isLetter()
}