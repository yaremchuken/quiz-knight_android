package yaremchuken.quizknight.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.View.OnSystemUiVisibilityChangeListener
import android.view.Window
import android.view.WindowManager

object Responsive {

    /**
     * Usage
     *
     *   override fun onResume() {
     *      super.onResume()
     *      Responsive.hideSystemBar(window)
     *   }
     *
     *   override fun onWindowFocusChanged(hasFocus: Boolean) {
     *      super.onWindowFocusChanged(hasFocus)
     *      if (hasFocus) {
     *          Responsive.hideSystemBarOnFocus(window)
     *      }
     *   }
     */
    fun hideSystemBar(window: Window) {
        window.decorView.setOnSystemUiVisibilityChangeListener(
            OnSystemUiVisibilityChangeListener {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        )
    }

    fun hideSystemBarOnFocus(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    fun vibratePhone(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(100)
        }
    }
}