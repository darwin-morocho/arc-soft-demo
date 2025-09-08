package fit.codergym.arc_soft_demo.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Sealed class representing different face detection states
 */
sealed class FaceDetectionState {
    object NoFaceDetected : FaceDetectionState()
    object BadAngle : FaceDetectionState()
    object GoodFace : FaceDetectionState()
    object NotAlive : FaceDetectionState()
    object BadQuality : FaceDetectionState()
}

/**
 * Custom View for displaying a horizontal scanner line that moves
 * The line changes color based on face detection state
 */
class FaceRectView(
    context: Context?,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // Paint object for drawing the scanner line
    private val scannerPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND // Rounded edges for the line
    }

    // Paint object for drawing the background rectangle
    private val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Current face detection state
    private var faceDetectionState: FaceDetectionState = FaceDetectionState.NoFaceDetected

    // Animation variables
    private var scannerPosition: Float = 0f
    private var scannerDirection: Int = 1 // 1 for down, -1 for up
    private val scannerSpeed: Float = 6f // pixels per frame

    // Horizontal margin
    private val horizontalMargin: Int = 15 // 15 pixels margin

    // Corner radius for the line
    private val lineCornerRadius: Float = 12f // Rounded corners radius

    // Explicitly type the Runnable to avoid recursive type inference
    private val scannerRunnable: Runnable = object : Runnable {
        override fun run() {
            updateScannerPosition()
            invalidate()
            postDelayed(this, 30)
        }
    }

    /**
     * Main drawing method called by the system
     * @param canvas The canvas to draw on
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background rectangle first
        drawBackgroundRectangle(canvas)

        // Then draw the scanner line on top
        drawScannerLine(canvas)
    }

    /**
     * Draws the background rectangle with opacity
     * @param canvas The canvas to draw on
     */
    private fun drawBackgroundRectangle(canvas: Canvas) {
        // Determine base color based on detection state
        val baseColor = when (faceDetectionState) {
            is FaceDetectionState.NoFaceDetected -> Color.WHITE
            is FaceDetectionState.BadAngle -> Color.YELLOW
            is FaceDetectionState.GoodFace -> Color.BLUE
            is FaceDetectionState.NotAlive -> Color.RED
            is FaceDetectionState.BadQuality -> Color.YELLOW
        }

        // Set opacity based on detection state
        val alpha = when (faceDetectionState) {
            is FaceDetectionState.NoFaceDetected -> 0.0f // Transparent when no face detected
            else -> 0.1f // 10% opacity for other states
        }

        // Apply alpha to the base color
        val colorWithAlpha = Color.argb(
            (255 * alpha).toInt(),
            Color.red(baseColor),
            Color.green(baseColor),
            Color.blue(baseColor)
        )

        backgroundPaint.color = colorWithAlpha

        // Draw the background rectangle covering the whole view
        canvas.drawRect(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            backgroundPaint
        )
    }

    /**
     * Draws the moving scanner line with horizontal margin and rounded corners
     * @param canvas The canvas to draw on
     */
    private fun drawScannerLine(canvas: Canvas) {
        // Determine line color based on detection state
        val lineColor = when (faceDetectionState) {
            is FaceDetectionState.NoFaceDetected -> Color.WHITE // White when no face detected
            is FaceDetectionState.BadAngle -> Color.YELLOW
            is FaceDetectionState.GoodFace -> Color.BLUE
            is FaceDetectionState.NotAlive -> Color.RED
            is FaceDetectionState.BadQuality -> Color.YELLOW
        }

        // Draw the scanner line with rounded corners
        drawRoundedLine(canvas, lineColor)
    }

    /**
     * Draws a rounded line using a path for better rounded corners
     * @param canvas The canvas to draw on
     * @param color The color of the line
     */
    private fun drawRoundedLine(canvas: Canvas, color: Int) {
        scannerPaint.color = color

        // Calculate line boundaries with horizontal margin
        val left = horizontalMargin.toFloat()
        val right = (width - horizontalMargin).toFloat()
        val top = scannerPosition - (scannerPaint.strokeWidth / 2)
        val bottom = scannerPosition + (scannerPaint.strokeWidth / 2)

        // Create a rounded rectangle path for the line
        val path = Path()
        val rect = android.graphics.RectF(left, top, right, bottom)

        // Add rounded rectangle to the path
        path.addRoundRect(rect, lineCornerRadius, lineCornerRadius, Path.Direction.CW)

        // Draw the rounded line
        canvas.drawPath(path, scannerPaint)
    }

    /**
     * Updates the scanner position for animation
     */
    private fun updateScannerPosition() {
        // Move scanner position based on direction and speed
        scannerPosition += scannerSpeed * scannerDirection

        // Reverse direction at boundaries
        if (scannerPosition >= height) {
            scannerPosition = height.toFloat()
            scannerDirection = -1 // Change direction to up
        } else if (scannerPosition <= 0) {
            scannerPosition = 0f
            scannerDirection = 1 // Change direction to down
        }
    }

    /**
     * Starts the scanner animation when view is attached to window
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startScannerAnimation()
    }

    /**
     * Stops the scanner animation when view is detached from window
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopScannerAnimation()
    }

    /**
     * Starts the continuous scanner animation
     */
    private fun startScannerAnimation() {
        // Remove any existing callbacks and start new animation
        removeCallbacks(scannerRunnable)
        post(scannerRunnable)
    }

    /**
     * Stops the scanner animation
     */
    private fun stopScannerAnimation() {
        // Remove animation callbacks
        removeCallbacks(scannerRunnable)
    }

    /**
     * Sets the face detection state and updates line color
     * @param state The new detection state
     */
    fun setFaceDetectionState(state: FaceDetectionState) {
        faceDetectionState = state
        // No need to invalidate here since animation is continuous
    }
}