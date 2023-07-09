package yaremchuken.quizknight.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import yaremchuken.quizknight.activities.CityActivity
import yaremchuken.quizknight.databinding.ItemAnswerFieldWordBorderedBinding
import yaremchuken.quizknight.databinding.ItemWorldmapMarkerBinding
import yaremchuken.quizknight.entity.ModuleType

class WorldmapAdapter(
    private val activity: CityActivity,
    val items: List<ModuleType>
): RecyclerView.Adapter<WorldmapAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemWorldmapMarkerBinding): RecyclerView.ViewHolder(binding.root) {
        val markerHolder = binding.llWorldmapMarkerHolder
        val mapMarker = binding.tvWorldmapMarker
        val markerDescription = binding.tvWorldmapMarkerDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemWorldmapMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.markerHolder.setOnClickListener {
            activity.switchModule(items[position])
        }
        holder.mapMarker.text = items[position].name
        holder.markerDescription.text = ModuleType.description(items[position])
    }
}