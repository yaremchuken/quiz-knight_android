package yaremchuken.quizknight.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.GameStats
import yaremchuken.quizknight.activities.CityActivity
import yaremchuken.quizknight.databinding.ItemCityWorldmapMarkerBinding
import yaremchuken.quizknight.entity.ModuleType

class CityWorldmapAdapter(
    private val activity: CityActivity,
    val items: List<ModuleType>,
    val modulesData: Map<ModuleType, Long>
): RecyclerView.Adapter<CityWorldmapAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemCityWorldmapMarkerBinding): RecyclerView.ViewHolder(binding.root) {
        val markerHolder = binding.llWorldmapMarkerHolder
        val mapMarker = binding.tvWorldmapMarker
        val markerDescription = binding.tvWorldmapMarkerDescription
        val markerCompleted = binding.tvWorldmapMarkerCompleted
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemCityWorldmapMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.markerHolder.setOnClickListener {
            activity.switchModule(items[position])
        }
        holder.mapMarker.text = items[position].name
        holder.markerDescription.text = ModuleType.description(items[position])

        val completed = GameStats.progress[items[position]]
        holder.markerCompleted.text = "${completed.toString()}/${modulesData[items[position]]}"
    }
}