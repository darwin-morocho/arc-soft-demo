package fit.codergym.arc_soft_demo.data

import android.app.Activity
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import io.flutter.Log

class SDK {
    companion object {

        var isSdkActivated: Boolean = false // Flag to check if the SDK is activated

        private const val TAG = "👀 SDK"

        /**
         * Activates the ArcFace SDK online with the provided parameters.
         *
         * @param appId The application ID.
         * @param sdkKey The SDK key.
         * @param activeKey The activation key.
         * @return True if activation is successful, false otherwise.
         */
        fun activateSdk(
            activity: Activity,
            appId: String,
            sdkKey: String,
            activeKey: String
        ): Boolean {
            when (val code = FaceEngine.activeOnline(activity, activeKey, appId, sdkKey)) {
                ErrorInfo.MOK -> {
                    Log.i(TAG, "✅ SDK activated successfully")
                    isSdkActivated = true
                    return true
                }

                ErrorInfo.MERR_ASF_ALREADY_ACTIVATED -> {
                    Log.w(TAG, "⚠️ SDK already activated")
                    isSdkActivated = true
                    return true
                }

                else -> {
                    Log.e(TAG, "❌ SDK activation error: $code")
                    return false
                }
            }
        }

    }
}