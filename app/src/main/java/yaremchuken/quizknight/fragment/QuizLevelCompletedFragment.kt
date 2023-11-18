package yaremchuken.quizknight.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import yaremchuken.quizknight.R

class QuizLevelCompletedFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.quiz_layout_level_completed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun setTribute(tribute: Long) {
        view?.findViewById<TextView>(R.id.tvLevelTribute)?.text = "+$tribute"
    }
}