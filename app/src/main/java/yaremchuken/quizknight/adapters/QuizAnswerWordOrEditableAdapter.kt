package yaremchuken.quizknight.adapters

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.activities.QuizActivity
import yaremchuken.quizknight.databinding.ItemAnswerFieldWordOrEditableBinding

class QuizAnswerWordOrEditableAdapter(
    private val activity: QuizActivity,
    private val items: List<String>
): RecyclerView.Adapter<QuizAnswerWordOrEditableAdapter.ViewHolder>() {

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
}