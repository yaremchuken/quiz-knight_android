package yaremchuken.quizknight.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.activity.QuizActivity
import yaremchuken.quizknight.databinding.ItemAnswerFieldWordOrEditableBinding

class QuizAnswerWordOrEditableAdapter(
    private val activity: QuizActivity,
    private val items: List<String>
): RecyclerView.Adapter<QuizAnswerWordOrEditableAdapter.ViewHolder>() {

    companion object {
        private const val SENTENCES_TO_TRANSLATE_SIZE = 3
    }

    var playerInput: String = ""

    class ViewHolder(binding: ItemAnswerFieldWordOrEditableBinding): RecyclerView.ViewHolder(binding.root) {
        val word = binding.tvWord
        val input = binding.tvInput
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(
            ItemAnswerFieldWordOrEditableBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
        } else {
            holder.word.text = items[position]
            holder.word.visibility = View.VISIBLE
            holder.input.visibility = View.GONE
            holder.word.setOnClickListener {
                activity.showTranslationDialog(collectTextsToTranslate(position))
            }
        }
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