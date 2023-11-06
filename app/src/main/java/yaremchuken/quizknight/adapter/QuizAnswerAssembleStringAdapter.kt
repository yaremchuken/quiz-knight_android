package yaremchuken.quizknight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.activity.QuizActivity
import yaremchuken.quizknight.databinding.ItemAnswerFieldWordBorderedBinding

class QuizAnswerAssembleStringAdapter(
    private val activity: QuizActivity,
    val items: MutableList<String>,
    private val source: String
): RecyclerView.Adapter<QuizAnswerAssembleStringAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemAnswerFieldWordBorderedBinding): RecyclerView.ViewHolder(binding.root) {
        val word = binding.tvWord
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemAnswerFieldWordBorderedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.word.text = items[position]
        holder.word.setOnClickListener {
            activity.adaptersExchanger(this, position, source)
        }
    }
}