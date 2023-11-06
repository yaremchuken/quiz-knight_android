package yaremchuken.quizknight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.activity.QuizActivity
import yaremchuken.quizknight.databinding.ItemTranslationLineBinding

class TranslationDialogAdapter(
    private val quizActivity: QuizActivity,
    private val items: List<Pair<String, String>>
): RecyclerView.Adapter<TranslationDialogAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemTranslationLineBinding): RecyclerView.ViewHolder(binding.root) {
        val playBtn = binding.ibPlay
        val tvSource = binding.tvSourceSentence
        val tvTranslation = binding.tvTranslatedSentence
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTranslationLineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.playBtn.setOnClickListener {
            quizActivity.speakOut(items[position].first)
        }
        holder.tvSource.text = items[position].first
        holder.tvTranslation.text = items[position].second
    }
}