package yaremchuken.quizknight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yaremchuken.quizknight.R
import yaremchuken.quizknight.databinding.ItemQuizCompletedRatingBinding
import yaremchuken.quizknight.fragment.QuizLevelCompletedFragment

class QuizCompletedRatingsAdapter(
    private val fragment: QuizLevelCompletedFragment,
    private val items: List<Boolean>
): RecyclerView.Adapter<QuizCompletedRatingsAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemQuizCompletedRatingBinding): RecyclerView.ViewHolder(binding.root) {
        val ratingImg = binding.ivRating
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemQuizCompletedRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val params = holder.ratingImg.layoutParams as MarginLayoutParams
        params.setMargins(0, if (position % 2 == 0) 60 else 0, 0, 0)

        holder.ratingImg.setImageResource(
            if (items[position]) R.drawable.ui_rating_full else R.drawable.ui_rating_empty
        )

        holder.ratingImg.scaleY = 0F
        holder.ratingImg.scaleY = 0F

        val animScale: Animation = AnimationUtils.loadAnimation(fragment.context, R.anim.anim_scale)

        fragment.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                Thread.sleep(position * 300L)
                holder.ratingImg.scaleY = 1F
                holder.ratingImg.scaleY = 1F
                holder.ratingImg.startAnimation(animScale)
            }
        }
    }
}