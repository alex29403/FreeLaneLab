package de.alexin.freelanelab

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.graphics.RectF
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

const val EXTRA_DRAW_TYPE = "draw_type"

const val DRAW_LINE = "line"
const val DRAW_CURVE = "curve"
const val DRAW_CIRCLE = "circle"

const val EXTRA_X1 = "x1"
const val EXTRA_Y1 = "y1"
const val EXTRA_X2 = "x2"
const val EXTRA_Y2 = "y2"
const val EXTRA_X3 = "x3"
const val EXTRA_Y3 = "y3"

class MyAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: MyAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        instance = this
        Log.d("DEBUG", "Accessibility service connected")
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float) {
        val path = Path().apply {
            moveTo(x1, y1)
            lineTo(x2, y2)
        }

        val distance = hypot(x2 - x1, y2 - y1)
        val duration = (distance / 1.0f)
            .coerceIn(100f, 1000f)
            .toLong()

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(path, 0, duration)
            )
            .build()

        dispatchGesture(gesture, null, null)
    }

    fun drawCurve(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        val D = 2f * (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2))

        if (D == 0f) {
            val path = Path().apply {
                moveTo(x1, y1)
                lineTo(x3, y3)
            }

            val duration = hypot(x3 - x1, y3 - y1)
                .coerceIn(100f, 1000f)
                .toLong()

            dispatchGesture(
                GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
                    .build(),
                null,
                null
            )
            return
        }

        val cx = (
                (x1 * x1 + y1 * y1) * (y2 - y3) +
                        (x2 * x2 + y2 * y2) * (y3 - y1) +
                        (x3 * x3 + y3 * y3) * (y1 - y2)
                ) / D

        val cy = (
                (x1 * x1 + y1 * y1) * (x3 - x2) +
                        (x2 * x2 + y2 * y2) * (x1 - x3) +
                        (x3 * x3 + y3 * y3) * (x2 - x1)
                ) / D

        val r = hypot(x1 - cx, y1 - cy)

        val rect = RectF(cx - r, cy - r, cx + r, cy + r)

        var theta1 = Math.toDegrees(atan2((y1 - cy).toDouble(), (x1 - cx).toDouble())).toFloat()
        val theta2 = Math.toDegrees(atan2((y2 - cy).toDouble(), (x2 - cx).toDouble())).toFloat()
        var theta3 = Math.toDegrees(atan2((y3 - cy).toDouble(), (x3 - cx).toDouble())).toFloat()

        var sweep = theta3 - theta1

        val midAngle = theta2
        if (!((sweep > 0 && midAngle in theta1..theta3) ||
                    (sweep < 0 && midAngle in theta3..theta1))
        ) {
            sweep = if (sweep > 0) sweep - 360f else sweep + 360f
        }

        val path = Path().apply {
            addArc(rect, theta1, sweep)
        }

        val distance = hypot(x3 - x1, y3 - y1)

        // NOCH NICHT RICHTIG !!!
        val duration = (distance / 1.0f).coerceIn(100f, 1000f).toLong()

        dispatchGesture(
            GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
                .build(),
            null,
            null
        )
    }

    fun drawCircle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        val D = 2f * (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2))

        if (D == 0f) {
            val path = Path().apply {
                moveTo(x1, y1)
                lineTo(x3, y3)
            }

            val duration = hypot(x3 - x1, y3 - y1)
                .coerceIn(100f, 1000f)
                .toLong()

            dispatchGesture(
                GestureDescription.Builder()
                    .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
                    .build(),
                null,
                null
            )
            return
        }

        val cx = (
                (x1 * x1 + y1 * y1) * (y2 - y3) +
                        (x2 * x2 + y2 * y2) * (y3 - y1) +
                        (x3 * x3 + y3 * y3) * (y1 - y2)
                ) / D

        val cy = (
                (x1 * x1 + y1 * y1) * (x3 - x2) +
                        (x2 * x2 + y2 * y2) * (x1 - x3) +
                        (x3 * x3 + y3 * y3) * (x2 - x1)
                ) / D

        val r = hypot(x1 - cx, y1 - cy)

        val rect = RectF(cx - r, cy - r, cx + r, cy + r)

        val direction = if ((x2 - x1)*(y3 - y1) - (x3 - x1)*(y2 - y1) > 0) {
            Path.Direction.CW
        } else {
            Path.Direction.CCW
        }

        val path = Path().apply {
            addOval(rect, direction)
        }

        val duration = (2 * Math.PI * r).toFloat()
            .coerceIn(100f, 1000f)
            .toLong()

        dispatchGesture(
            GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
                .build(),
            null,
            null
        )
    }
}

fun Context.isAccessibilityServiceEnabled(
    service: Class<out AccessibilityService>
): Boolean {
    val expectedComponent = ComponentName(this, service)

    val enabledServices = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    return enabledServices
        .split(':')
        .map { ComponentName.unflattenFromString(it) }
        .any { it == expectedComponent }
}

class DrawReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val svc = MyAccessibilityService.instance ?: return

        when (intent.getStringExtra(EXTRA_DRAW_TYPE)) {
            DRAW_LINE -> {
                svc.drawLine(
                    intent.getFloatExtra(EXTRA_X1, 0f),
                    intent.getFloatExtra(EXTRA_Y1, 0f),
                    intent.getFloatExtra(EXTRA_X2, 0f),
                    intent.getFloatExtra(EXTRA_Y2, 0f)
                )
            }

            DRAW_CURVE -> {
                svc.drawCurve(
                    intent.getFloatExtra(EXTRA_X1, 0f),
                    intent.getFloatExtra(EXTRA_Y1, 0f),
                    intent.getFloatExtra(EXTRA_X2, 0f),
                    intent.getFloatExtra(EXTRA_Y2, 0f),
                    intent.getFloatExtra(EXTRA_X3, 0f),
                    intent.getFloatExtra(EXTRA_Y3, 0f)
                )
            }

            DRAW_CIRCLE -> {
                svc.drawCircle(
                    intent.getFloatExtra(EXTRA_X1, 0f),
                    intent.getFloatExtra(EXTRA_Y1, 0f),
                    intent.getFloatExtra(EXTRA_X2, 0f),
                    intent.getFloatExtra(EXTRA_Y2, 0f),
                    intent.getFloatExtra(EXTRA_X3, 0f),
                    intent.getFloatExtra(EXTRA_Y3, 0f)
                )
            }
        }
    }
}