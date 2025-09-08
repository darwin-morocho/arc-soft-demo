package fit.codergym.arc_soft_demo.data

import android.app.Activity
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.arcsoft.face.FaceFeature
import com.arcsoft.face.FaceInfo
import com.arcsoft.face.FaceSimilar
import com.arcsoft.face.ImageQualitySimilar
import com.arcsoft.face.LivenessInfo
import com.arcsoft.face.MaskInfo
import com.arcsoft.face.enums.CompareModel
import com.arcsoft.face.enums.DetectFaceOrientPriority
import com.arcsoft.face.enums.DetectMode
import com.arcsoft.face.enums.ExtractType
import io.flutter.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


enum class LivenessType {
    RGB, IR
}


class FaceDetector(private val activity: Activity, private val livenessType: LivenessType) {
    companion object {
        private const val TAG = "👀 FaceDetector" // Tag for logging
        private const val MAX_DETECT_NUM = 1 // Maximum number of faces to detect
        var THRESHOLD = 0.8f // Threshold for face recognition similarity score
    }

    private val disposeLock = Mutex()
    private var isDisposed = false


    var isEngineInitialized: Boolean = false // Flag to check if the engine is initialized

    private var engine: FaceEngine? = null

    /**
     * Initializes the FaceEngine with the specified parameters.
     *
     * We use suspend function to allow this to be called from a coroutine context,
     * due to the potentially long-running nature of the initialization process.
     *
     * @return True if initialization is successful, false otherwise.
     * @throws Error if the SDK is not activated.
     */
    suspend fun init(): Boolean = withContext(Dispatchers.IO) {
        if (!SDK.isSdkActivated) {
            Log.e(TAG, "❌ SDK is not activated")
            return@withContext false
        }

        engine = engine ?: FaceEngine()

        val livenessCode = when (livenessType) {
            LivenessType.RGB -> FaceEngine.ASF_LIVENESS
            LivenessType.IR -> FaceEngine.ASF_IR_LIVENESS
        }

        val combinedMask = FaceEngine.ASF_FACE_DETECT or
                FaceEngine.ASF_FACE_RECOGNITION or
                FaceEngine.ASF_IMAGEQUALITY or livenessCode

        val frCode = engine!!.init(
            activity, DetectMode.ASF_DETECT_MODE_VIDEO,
            DetectFaceOrientPriority.ASF_OP_ALL_OUT,
            MAX_DETECT_NUM, combinedMask
        )

        isEngineInitialized = frCode == ErrorInfo.MOK

        return@withContext isEngineInitialized
    }


    /**
     * Detects a face in the provided NV21 byte array.
     *
     * @param nv21 The NV21 byte array containing the image data.
     * @param width The width of the image.
     * @param height The height of the image.
     * @return A FaceInfo object containing information about the detected face, or null if no face is detected.
     */
    fun detectFace(nv21: ByteArray, width: Int, height: Int): FaceInfo? {
        try {

            if (engine == null) {
                Log.e(TAG, "❌ Face tracking engine is not initialized")
                return null
            }

            val faceInfoList = mutableListOf<FaceInfo>()

            val code = engine!!.detectFaces(
                nv21,
                width,
                height,
                FaceEngine.CP_PAF_NV21,
                faceInfoList
            )

            if (code != ErrorInfo.MOK || faceInfoList.isEmpty()) {
                return null
            }
            val faceInfo = faceInfoList.first()
            return if (faceInfo.isWithinBoundary == 1) faceInfo else null
        } catch (e: Exception) {
            Log.e(TAG, "❌ Face detection error: ${e.message}")
            return null
        }
    }


    /**
     * Extracts face features from the provided NV21 byte array.
     */
    fun extractFaceFeature(
        nv21: ByteArray,
        width: Int,
        height: Int,
        faceInfo: FaceInfo,
        extractType: ExtractType
    ): FaceFeature? {
        if (engine == null) {
            Log.e(TAG, "❌ Face recognition engine is not initialized")
            return null
        }
        val faceFeature = FaceFeature()

        val code = engine!!.extractFaceFeature(
            nv21,
            width,
            height,
            FaceEngine.CP_PAF_NV21,
            faceInfo,
            extractType,
            MaskInfo.NOT_WORN,
            faceFeature,
        )

        if (code != ErrorInfo.MOK) {
//            Log.e(TAG, "❌ Face feature extraction failed with code: $code")
            return null
        }

        return faceFeature
    }


    /**
     * Detects if a face is alive using the provided NV21 byte array and face information.
     */
    fun isAliveFaceDetected(
        nv21: ByteArray,
        width: Int,
        height: Int,
        faceInfo: FaceInfo
    ): Boolean {
        if (engine == null) {
            Log.e(TAG, "❌ Face recognition engine is not initialized")
            return false
        }
        val code = when (livenessType) {
            LivenessType.RGB -> engine!!.process(
                nv21,
                width,
                height,
                FaceEngine.CP_PAF_NV21,
                listOf(faceInfo),
                FaceEngine.ASF_LIVENESS
            )

            LivenessType.IR -> engine!!.processIr(
                nv21,
                width,
                height,
                FaceEngine.CP_PAF_NV21,
                listOf(faceInfo),
                FaceEngine.ASF_IR_LIVENESS
            )
        }

        if (code != ErrorInfo.MOK) {
            Log.e(TAG, "❌ Liveness ${livenessType.name} detection failed with code: $code")
            return false
        }
        val list = mutableListOf<LivenessInfo>()

        val getCode = when (livenessType) {
            LivenessType.RGB -> engine!!.getLiveness(list)
            LivenessType.IR -> engine!!.getIrLiveness(list)
        }

        if (getCode != ErrorInfo.MOK || list.isEmpty()) {
            return false
        }

        return list.first().liveness == LivenessInfo.ALIVE
    }


    /**
     * Searches for a face in the database using the provided camera face feature.
     *
     * @param cameraFaceFeature The face feature extracted from the camera.
     * @return The user ID if a matching face is found, null otherwise.
     */
    fun searchFace(cameraFaceFeature: FaceFeature): String? {
        val faces = FacesDatabase.faces
        if (faces.isEmpty()) {
            return null
        }

        for ((userId, enrolledTemplate) in faces) {
            val enrolledFeature = FaceFeature().apply {
                featureData = enrolledTemplate
            }

            val score = compareFaces(cameraFaceFeature, enrolledFeature)
            if (score > THRESHOLD) { // Threshold for similarity
                return userId
            }
        }
        return null
    }


    /**
     * Compares two face features to determine their similarity score.
     *
     * @param cameraFeature The face feature extracted from the camera.
     * @param enrolledFeature The face feature of the enrolled user.
     * @return A float (0.0 - 1.0) representing the similarity score between the two face features.
     */
    private fun compareFaces(
        cameraFeature: FaceFeature,
        enrolledFeature: FaceFeature
    ): Float {
        if (engine == null) {
            return 0f
        }
        val faceSimilar = FaceSimilar()
        val compareCode = engine!!.compareFaceFeature(
            cameraFeature,
            enrolledFeature,
            CompareModel.LIFE_PHOTO,
            faceSimilar
        )
        return if (compareCode == ErrorInfo.MOK) {
            faceSimilar.score
        } else {
            Log.e(TAG, "Error comparing face features: $compareCode")
            0f
        }

    }

    /**
     * Gets the image quality score for a given face.
     *
     * @param nv21 The NV21 byte array containing the image data.
     * @param width The width of the image.
     * @param height The height of the image.
     * @param faceInfo The FaceInfo object containing information about the detected face.
     * @return A float representing the image quality score, or 0 if detection fails.
     */
    fun getImageQuality(nv21: ByteArray, width: Int, height: Int, faceInfo: FaceInfo): Float {

        if (engine == null) {
            return 0f
        }

        val quality = ImageQualitySimilar()
        val qualityCode = engine!!.imageQualityDetect(
            nv21,
            width,
            height,
            FaceEngine.CP_PAF_NV21,
            faceInfo,
            MaskInfo.NOT_WORN,
            quality,
        )
        if (qualityCode != ErrorInfo.MOK) {
            Log.e(TAG, "❌ Image quality detection failed with code: $qualityCode")
            return 0f
        }
        return quality.score
    }

    /**
     * Releases the resources used by the FaceEngine.
     */
    suspend fun dispose(): Unit = withContext(Dispatchers.IO) {
        disposeLock.withLock {
            if (isDisposed) return@withContext

            try {
                engine?.let {
                    it.unInit()
                    engine = null
                }
                isDisposed = true
                Log.i(TAG, "✅ Face engine disposed")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error disposing face engine: ${e.message}")
            }
        }
    }
}