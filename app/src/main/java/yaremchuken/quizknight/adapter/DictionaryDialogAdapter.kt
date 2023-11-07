package yaremchuken.quizknight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.activity.QuizActivity
import yaremchuken.quizknight.api.yandex.dictionary.YaDictionaryEntity
import yaremchuken.quizknight.databinding.ItemDictionaryEntityBinding

class DictionaryDialogAdapter(
    private val quizActivity: QuizActivity,
    private val items: Array<YaDictionaryEntity>
): RecyclerView.Adapter<DictionaryDialogAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemDictionaryEntityBinding): RecyclerView.ViewHolder(binding.root) {
        val tvSource = binding.tvSourceWord
        val tvTranscription = binding.tvTranscription
        val tvPartOfSpeech = binding.tvPartOfSpeech
        val rvTranslations = binding.rvTranslations
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemDictionaryEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val translation = items[position]

        holder.tvSource.text = translation.text
        holder.tvTranscription.text = if (translation.ts == null) null else "[${translation.ts}]"
        holder.tvPartOfSpeech.text = translation.pos

        holder.rvTranslations.layoutManager = LinearLayoutManager(quizActivity, LinearLayoutManager.VERTICAL, false)
        holder.rvTranslations.adapter = DictionaryDialogTranslationAdapter(translation.tr)
    }
}