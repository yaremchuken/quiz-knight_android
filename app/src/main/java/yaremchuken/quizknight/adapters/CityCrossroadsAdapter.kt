package yaremchuken.quizknight.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activities.CityActivity
import yaremchuken.quizknight.databinding.ItemCityCrossroadsLevelBinding
import yaremchuken.quizknight.model.ModuleLevel

class CityCrossroadsAdapter(
    private val activity: CityActivity,
    val items: List<ModuleLevel>
): RecyclerView.Adapter<CityCrossroadsAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemCityCrossroadsLevelBinding): RecyclerView.ViewHolder(binding.root) {
        val viewHolder = binding.llCrossroadsLevelHolder
        val title = binding.tvCrossroadsLevelTitle
        val completedMark = binding.ivCrossroadsCompleted
        val portrait = binding.ivCrossroadsPortrait
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemCityCrossroadsLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewHolder.setOnClickListener {
            activity.launchLevel(position.toLong())
        }
        holder.title.text = "${position+1}. ${items[position].title}"
        if (items[position].opponents.isNotEmpty()) {
            holder.portrait.setImageResource(
                when (items[position].opponents[0]) {
                    PersonageType.HERO -> -1
                    PersonageType.GOBLIN -> R.drawable.ic_portrait_goblin
                }
            )
        }
        val progress = GameStats.progress[GameStats.module]!! + 1
        holder.completedMark.visibility = if (position < progress) View.VISIBLE else View.INVISIBLE
    }
}