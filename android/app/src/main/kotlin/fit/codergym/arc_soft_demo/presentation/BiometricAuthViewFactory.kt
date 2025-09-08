package fit.codergym.arc_soft_demo.presentation

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class BiometricAuthViewFactory(private val activity: Activity) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(
        context: Context?,
        viewId: Int,
        args: Any?
    ): PlatformView {
        val creationParams = args as Map<*, *>
        TODO("Not yet implemented")
    }
}