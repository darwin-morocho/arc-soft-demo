package fit.codergym.arc_soft_demo.presentation

import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver
import io.flutter.plugin.platform.PlatformView

class BiometricAuthView(activity: Activity): PlatformView, ViewTreeObserver.OnGlobalLayoutListener {

    init {
        val inflater = activity.layoutInflater
    }

    // Override from PlatformView
    override fun getView(): View? {
        TODO("Not yet implemented")
    }

    // Override from PlatformView
    override fun dispose() {
        TODO("Not yet implemented")
    }


    /**
     * Override from ViewTreeObserver.OnGlobalLayoutListener
     * Called when the global layout state or the visibility of views within the view tree changes.
     * Here you can get the dimensions of the view after layout is complete.
     */
    override fun onGlobalLayout() {
        TODO("Not yet implemented")
    }
}