package yaremchuken.quizknight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.api.yandex.dictionary.YaDictionaryTransactionEntity
import yaremchuken.quizknight.databinding.ItemDictionaryTranslationLineBinding

class DictionaryDialogTranslationAdapter(
    private val items: Array<YaDictionaryTransactionEntity>
): RecyclerView.Adapter<DictionaryDialogTranslationAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemDictionaryTranslationLineBinding): RecyclerView.ViewHolder(binding.root) {
        val tvTranslations = binding.tvTranslations
        val tvSynonyms = binding.tvSynonyms
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemDictionaryTranslationLineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val translateSynonyms = items[position]
        var translations = translateSynonyms.text
        if (!translateSynonyms.syn.isNullOrEmpty()) {
            translations = "$translations, ${translateSynonyms.syn.joinToString { it.text }}"
        }

        holder.tvTranslations.text = translations

        if (translateSynonyms.mean.isNullOrEmpty()) {
            holder.tvSynonyms.visibility = View.GONE
        } else {
            holder.tvSynonyms.text = "(${translateSynonyms.mean.joinToString { it.text }})"
        }
    }
}