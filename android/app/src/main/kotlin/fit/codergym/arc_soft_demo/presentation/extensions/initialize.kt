package fit.codergym.arc_soft_demo.presentation.extensions

import android.util.Log
import android.util.Size
import com.arcsoft.face.enums.ExtractType
import fit.codergym.arc_soft_demo.data.FacesDatabase
import fit.codergym.arc_soft_demo.data.SDK
import fit.codergym.arc_soft_demo.presentation.BiometricAuthView
import fit.codergym.arc_soft_demo.presentation.FaceDetectionState
import fit.codergym.camera2builder.Camera2Builder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun BiometricAuthView.initialize() {
    if (!SDK.isSdkActivated) {
        SDK.activateSdk(
            activity = activity,
            appId = appID,
            sdkKey = sdkKey,
            activeKey = activeKey,
        )
    }

    if (!SDK.isSdkActivated) {
        initializeResult?.success(false)
        initializeResult = null
        return
    }

    rgbCameraBuilder = Camera2Builder(
        context = activity,
        cameraId = rgbCameraId,
        previewSize = Size(480, 640),
        textureView = cameraPreview
    ).apply {
        start()
    }

    irCameraBuilder = Camera2Builder(
        context = activity,
        cameraId = irCameraId,
        previewSize = Size(480, 640),
        textureView = null // No preview for IR camera
    ).apply {
        setOnFrameListener { nv21, size ->
            // Handle IR frame data here
            val faceInfo = faceDetector.detectFace(
                nv21 = nv21,
                width = size.width,
                height = size.height
            )

            if (faceInfo == null) {
                faceRectView.setFaceDetectionState(FaceDetectionState.NoFaceDetected)
                return@setOnFrameListener
            }

            if (pauseRecognition) {
                return@setOnFrameListener
            }

            val isEnrolling = enrollUserId != null

            if (!faceInfo.isAcceptableAngles(isEnrolling)) {
                faceRectView.setFaceDetectionState(FaceDetectionState.BadAngle)
                return@setOnFrameListener
            }

            val quality = faceDetector.getImageQuality(
                nv21 = nv21,
                width = size.width,
                height = size.height,
                faceInfo = faceInfo
            )

            if(quality<0.6){
                faceRectView.setFaceDetectionState(FaceDetectionState.BadQuality)
                return@setOnFrameListener
            }

            faceRectView.setFaceDetectionState(FaceDetectionState.GoodFace)
            
            val feature = faceDetector.extractFaceFeature(
                nv21 = nv21,
                width = size.width,
                height = size.height,
                faceInfo = faceInfo,
                extractType = if (isEnrolling) ExtractType.REGISTER else ExtractType.RECOGNIZE
            )

            if (feature == null) {
                return@setOnFrameListener
            }

            if (isEnrolling) {
                FacesDatabase.addFace(
                    userId = enrollUserId!!,
                    template = feature.featureData
                )
                pauseRecognition = true
                enrollResult?.success(feature.featureData)
                enrollResult = null
                enrollUserId = null
            } else {
                val detectedUserId = faceDetector.searchFace(feature)
                if (detectedUserId == null) {
                    return@setOnFrameListener
                }
                Log.i("👀 BiometricAuthView", "Face recognized: $detectedUserId")
                pauseRecognition = true
                activity.runOnUiThread {
                    eventSink?.success(
                        mapOf(
                            "event" to "onFaceRecognized",
                            "userId" to detectedUserId
                        )
                    )
                }
            }
        }

        start()
    }
    launch(Dispatchers.IO) {
        faceDetector.init()
    }
    initializeResult?.success(true)
    initializeResult = null
}