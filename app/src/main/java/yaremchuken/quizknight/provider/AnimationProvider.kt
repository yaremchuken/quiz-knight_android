package yaremchuken.quizknight.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import yaremchuken.quizknight.PersonageType
import yaremchuken.quizknight.draw.ActionType
import yaremchuken.quizknight.draw.Animation
import java.util.EnumMap

object AnimationProvider {

    private val animations: MutableMap<PersonageType, Map<ActionType, Animation>> = EnumMap(PersonageType::class.java)

    fun preparePersonages(context: Context, personages: List<PersonageType>) {
        personages
            .filter { !animations.keys.contains(it) }
            .forEach { animations[it] = loadAnimation(context, it) }
    }

    fun getAnimation(personage: PersonageType) = animations[personage] ?: mapOf()

    private fun loadAnimation(context: Context, personage: PersonageType): EnumMap<ActionType, Animation> {
        val anims = EnumMap<ActionType, Animation>(ActionType::class.java)

        val persName = personage.name.lowercase()
        context.assets
            .list("animations/$persName")
            ?.forEach {
                ActionType.values()
                    .forEach { type ->
                        val bitmaps = ArrayList<Bitmap>()
                        val action = type.name.lowercase()
                        context.assets
                            .list("animations/$persName/$action")
                            ?.forEach { pic ->
                                bitmaps.add(getBitmap(context, "animations/$persName/$action/$pic"))
                            }
                        anims[type] = Animation(bitmaps)
                    }
        }

        return anims
    }

    private fun getBitmap(context: Context, filePath: String): Bitmap =
        BitmapFactory.decodeStream(context.assets.open(filePath))
}