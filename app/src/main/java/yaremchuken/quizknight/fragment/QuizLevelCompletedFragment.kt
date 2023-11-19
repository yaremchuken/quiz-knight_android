package yaremchuken.quizknight.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yaremchuken.quizknight.R
import yaremchuken.quizknight.adapter.QuizCompletedRatingsAdapter

class QuizLevelCompletedFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.quiz_layout_level_completed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().visibility = View.GONE
    }

    fun setTribute(tribute: Long) {
        val textField = requireView().findViewById<TextView>(R.id.tvLevelTribute)
        lifecycleScope.launch {
            var counter = 0L
            while (counter <= tribute) {
                textField.text = "$counter"
                counter += 10
                if (counter > tribute) counter = tribute
                withContext(Dispatchers.IO) {
                    Thread.sleep(60)
                }
            }
        }
    }

    fun runAnimations() {
        val animSlide: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide)
        requireView().findViewById<ImageView>(R.id.ivOvalBG).startAnimation(animSlide)

        val animDrop: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_drop)
        requireView().findViewById<ImageView>(R.id.ivThumbUp).startAnimation(animDrop)

        val ratings = requireView().findViewById<RecyclerView>(R.id.rvRatings)

        ratings.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        ratings.adapter = QuizCompletedRatingsAdapter(this, listOf(true, true, false))

        val moveLeft: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_move_left)
        val moveRight: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_move_right)
        val dustL = requireView().findViewById<ImageView>(R.id.ivDustLeft)
        val dustR = requireView().findViewById<ImageView>(R.id.ivDustRight)

        dustL.scaleY = 0F
        dustL.scaleX = 0F
        dustR.scaleY = 0F
        dustR.scaleX = 0F

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                Thread.sleep(300)
                dustL.scaleY = 1F
                dustL.scaleX = 1F
                dustR.scaleY = 1F
                dustR.scaleX = 1F
                dustL.startAnimation(moveLeft)
                dustR.startAnimation(moveRight)
            }
        }
    }
}