package yaremchuken.quizknight.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.R
import yaremchuken.quizknight.activity.CampActivity
import yaremchuken.quizknight.databinding.ItemCampCrossroadsLevelBinding
import yaremchuken.quizknight.model.ModuleLevel

class CampCrossroadsAdapter(
    private val activity: CampActivity,
    val items: List<ModuleLevel>
): RecyclerView.Adapter<CampCrossroadsAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemCampCrossroadsLevelBinding): RecyclerView.ViewHolder(binding.root) {
        val topView = binding.llTopHolder
        val title = binding.tvCrossroadsLevelTitle
        val completedMark = binding.ivCrossroadsCompleted
        val portrait = binding.ivCrossroadsPortrait
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemCampCrossroadsLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = "${position+1}. ${items[position].title}"
        if (items[position].opponents.isNotEmpty()) {
            holder.portrait.setImageResource(
                when (items[position].opponents[0]) {
                    PersonageType.HERO -> -1
                    PersonageType.GOBLIN -> R.drawable.ic_portrait_goblin
                    PersonageType.PEASANT -> R.drawable.ic_portrait_peasant
                }
            )
        }
        val progress = GameStats.progress[GameStats.module]!! + 1
        holder.completedMark.visibility = if (position < progress) View.VISIBLE else View.INVISIBLE
        if (progress < position) {
            holder.topView.alpha = .7F
            holder.title.setTextColor(activity.getColor(R.color.dark_gray))
            holder.portrait.visibility = View.INVISIBLE
        } else {
            holder.topView.setOnClickListener {
                activity.launchLevel(position.toLong())
            }
        }
    }
}