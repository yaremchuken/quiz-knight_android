package yaremchuken.quizknight.draw

import android.content.Context
import android.graphics.Point

class Hero(context: Context, sceneDims: Point): Animated() {

    init {
        super.init(context, sceneDims, "hero")
        xPos = 40F
    }

    fun update() {

    }
}