package fit.codergym.arc_soft_demo.presentation

import android.app.Activity
import android.content.Context
import android.util.Log
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class BiometricAuthViewFactory(
    private val activity: Activity,
    private val binaryMessenger: BinaryMessenger
) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(
        context: Context?,
        viewId: Int,
        args: Any?
    ): PlatformView {
        val creationParams = args as Map<*, *>
        Log.i(
            "👀 BiometricAuthViewFactory",
            "Creating BiometricAuthView with params: $creationParams"
        )
        return BiometricAuthView(
            activity = activity,
            sdkKey = creationParams["sdkKey"] as String,
            appID = creationParams["appID"] as String,
            activeKey = creationParams["activeKey"] as String,
            rgbCameraId = creationParams["rgbCameraId"] as String,
            irCameraId = creationParams["irCameraId"] as String,
            binaryMessenger = binaryMessenger
        )
    }
}