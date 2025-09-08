package fit.codergym.arc_soft_demo.presentation.extensions

import com.arcsoft.face.FaceInfo
import kotlin.math.abs

// Thresholds to consider a face as frontal (adjustable)
const val MAX_YAW = 10.0f // ±10° on Y axis (left/right)
const val MAX_PITCH = 10.0f // ±10° on X axis (up/down)
const val MAX_PITCH_RECOGNIZE = 15.0f // ±15° on X axis (up/down)
const val MAX_YAW_RECOGNIZE = 15.0f // ±15° on Y axis (left/right)

fun FaceInfo.isAcceptableAngles(isEnrolling: Boolean): Boolean {
    val angle = face3DAngle

    if (isEnrolling) {
        return abs(angle.yaw) <= MAX_YAW && abs(angle.pitch) <= MAX_PITCH
    }
    return abs(angle.yaw) <= MAX_YAW_RECOGNIZE && abs(angle.pitch) <= MAX_PITCH_RECOGNIZE
}