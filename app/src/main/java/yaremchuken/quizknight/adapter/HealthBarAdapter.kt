package yaremchuken.quizknight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.R
import yaremchuken.quizknight.databinding.ItemHealthbarStatBinding

class HealthBarAdapter(
    private val items: List<Boolean>
): RecyclerView.Adapter<HealthBarAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemHealthbarStatBinding): RecyclerView.ViewHolder(binding.root) {
        val heartImg = binding.ivHeart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemHealthbarStatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.heartImg.setImageResource(
            if (items[position]) R.drawable.ui_heart_full else R.drawable.ui_heart_empty
        )
    }
}