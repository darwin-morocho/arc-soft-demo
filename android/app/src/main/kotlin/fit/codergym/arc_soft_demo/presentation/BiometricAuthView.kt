package fit.codergym.arc_soft_demo.presentation

import android.app.Activity
import android.view.TextureView
import android.view.View
import android.view.ViewTreeObserver
import fit.codergym.arc_soft_demo.R
import fit.codergym.arc_soft_demo.data.FaceDetector
import fit.codergym.arc_soft_demo.data.LivenessType
import fit.codergym.arc_soft_demo.presentation.extensions.initialize
import fit.codergym.camera2builder.Camera2Builder
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class BiometricAuthView(
    val activity: Activity,
    val appID: String,
    val sdkKey: String,
    val activeKey: String,
    val rgbCameraId: String,
    val irCameraId: String,
    val binaryMessenger: BinaryMessenger,
) : PlatformView,
    ViewTreeObserver.OnGlobalLayoutListener, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val rootView: View
    val faceRectView: FaceRectView

    var cameraPreview: TextureView
    var methodChannel: MethodChannel

    var initializeResult: MethodChannel.Result? = null
    var enrollResult: MethodChannel.Result? = null
    var eventSink: EventChannel.EventSink? = null

    var rgbCameraBuilder: Camera2Builder? = null
    var irCameraBuilder: Camera2Builder? = null

    val faceDetector = FaceDetector(activity, LivenessType.IR)

    var enrollUserId: String? = null

    var pauseRecognition = false

    init {
        val inflater = activity.layoutInflater
        rootView = inflater.inflate(R.layout.face_recognition_view, null)
        cameraPreview = rootView.findViewById(R.id.camera_preview)
        faceRectView = rootView.findViewById(R.id.face_rect_view)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        methodChannel = MethodChannel(
            binaryMessenger,
            "biometric_auth_channel"
        ).apply {
            setMethodCallHandler { call, result ->
                val args = call.arguments as? Map<*, *>
                when (call.method) {
                    "initialize" -> {
                        if (initializeResult != null) {
                            initializeResult?.error(
                                "ALREADY_INITIALIZING",
                                "Initialization is already in progress",
                                null
                            )
                            return@setMethodCallHandler
                        }
                        initializeResult = result
                        initialize()
                    }

                    "enroll" -> {
                        if(enrollResult!=null){
                            enrollResult?.error(
                                "ALREADY_ENROLLING",
                                "Enrollment is already in progress",
                                null
                            )
                            return@setMethodCallHandler
                        }

                        val userId = args?.get("userId") as? String
                        if (userId == null) {
                            result.error("INVALID_ARGUMENT", "userId is required", null)
                            return@setMethodCallHandler
                        }
                        enrollUserId = userId
                        enrollResult = result
                    }

                    "resumeRecognition" -> {
                        pauseRecognition = false
                        result.success(null)
                    }

                    else -> {
                        result.notImplemented()
                    }
                }
            }
        }

        EventChannel(binaryMessenger, "biometric_auth_events").apply {
            setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    eventSink = events
                }

                override fun onCancel(arguments: Any?) {

                }
            })
        }
    }

    // Override from PlatformView
    override fun getView(): View? {
        return rootView
    }

    // Override from PlatformView
    override fun dispose() {
        methodChannel.setMethodCallHandler(null)
        rgbCameraBuilder?.release { }
        irCameraBuilder?.release { }
    }


    /**
     * Override from ViewTreeObserver.OnGlobalLayoutListener
     * Called when the global layout state or the visibility of views within the view tree changes.
     * Here you can get the dimensions of the view after layout is complete.
     */
    override fun onGlobalLayout() {
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }
}