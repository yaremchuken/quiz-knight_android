package yaremchuken.quizknight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.R
import yaremchuken.quizknight.databinding.ItemQuizProgressBinding

class QuizProgressStarsAdapter(
    private val items: List<Boolean>
): RecyclerView.Adapter<QuizProgressStarsAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemQuizProgressBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivStar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemQuizProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.image.visibility = View.INVISIBLE
        } else {
            holder.image.setImageResource(
                if (items[position]) R.drawable.ui_star_full else R.drawable.ui_star_empty
            )
        }
    }
}