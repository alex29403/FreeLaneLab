package de.alexin.freelanelab

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.runtime.collectAsState

class OverlayService :
    LifecycleService(),
    SavedStateRegistryOwner {

    companion object {
        val isRunning = MutableStateFlow(false)
        private const val CHANNEL_ID = "overlay_channel"
        private const val NOTIFICATION_ID = 1
    }

    private val savedStateRegistryController =
        SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private lateinit var windowManager: WindowManager

    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var composeView: ComposeView

    override fun onCreate() {
        super.onCreate()

        isRunning.value = true

        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)

            setContent {
                OverlayUi(
                    onMove = { dx, dy ->
                        moveOverlayBy(dx, dy)
                    },
                    onLine = {
                        showPoints(2)
                    },
                    onCurve = {
                        showPoints(3)
                    },
                    onCircle = {
                        showPoints(3)
                    },
                    onDrawLine = {
                        val p1 = pointView1
                        val p2 = pointView2

                        if (p1 != null && p2 != null) {
                            val (x1, y1) = getCenterOnScreen(p1)
                            val (x2, y2) = getCenterOnScreen(p2)

                            hidePointWindows()

                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this@OverlayService, DrawReceiver::class.java).apply {
                                    putExtra(EXTRA_DRAW_TYPE, DRAW_LINE)

                                    putExtra(EXTRA_X1, x1)
                                    putExtra(EXTRA_Y1, y1)
                                    putExtra(EXTRA_X2, x2)
                                    putExtra(EXTRA_Y2, y2)
                                }
                                sendBroadcast(intent)
                            }, 500)
                        }
                    },
                    onDrawCurve = {
                        val p1 = pointView1
                        val p2 = pointView2
                        val p3 = pointView3

                        if (p1 != null && p2 != null && p3 != null) {
                            val (x1, y1) = getCenterOnScreen(p1)
                            val (x2, y2) = getCenterOnScreen(p2)
                            val (x3, y3) = getCenterOnScreen(p3)

                            hidePointWindows()

                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this@OverlayService, DrawReceiver::class.java).apply {
                                    putExtra(EXTRA_DRAW_TYPE, DRAW_CURVE)

                                    putExtra(EXTRA_X1, x1)
                                    putExtra(EXTRA_Y1, y1)
                                    putExtra(EXTRA_X2, x2)
                                    putExtra(EXTRA_Y2, y2)
                                    putExtra(EXTRA_X3, x3)
                                    putExtra(EXTRA_Y3, y3)
                                }
                                sendBroadcast(intent)
                            }, 500)
                        }
                    },
                    onDrawCircle = {
                        val p1 = pointView1
                        val p2 = pointView2
                        val p3 = pointView3

                        if (p1 != null && p2 != null && p3 != null) {
                            val (x1, y1) = getCenterOnScreen(p1)
                            val (x2, y2) = getCenterOnScreen(p2)
                            val (x3, y3) = getCenterOnScreen(p3)

                            hidePointWindows()

                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this@OverlayService, DrawReceiver::class.java).apply {
                                    putExtra(EXTRA_DRAW_TYPE, DRAW_CIRCLE)

                                    putExtra(EXTRA_X1, x1)
                                    putExtra(EXTRA_Y1, y1)
                                    putExtra(EXTRA_X2, x2)
                                    putExtra(EXTRA_Y2, y2)
                                    putExtra(EXTRA_X3, x3)
                                    putExtra(EXTRA_Y3, y3)
                                }
                                sendBroadcast(intent)
                            }, 500)
                        }
                    },
                    onClose = { stopSelf() },
                    onCloseMode = { num ->
                        hidePointWindows()
                    }
                )
            }
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        windowManager.addView(composeView, layoutParams)
    }

    private var pointView1: ComposeView? = null
    private var pointLayout1: WindowManager.LayoutParams? = null

    private var pointView2: ComposeView? = null
    private var pointLayout2: WindowManager.LayoutParams? = null

    private var pointView3: ComposeView? = null
    private var pointLayout3: WindowManager.LayoutParams? = null

    private val point1Aligned = MutableStateFlow(false)
    private val point2Aligned = MutableStateFlow(false)
    private val point3Aligned = MutableStateFlow(false)

    private fun moveOverlayBy(dx: Int, dy: Int) {
        layoutParams.x += dx
        layoutParams.y += dy
        windowManager.updateViewLayout(composeView, layoutParams)
    }

    override fun onDestroy() {
        isRunning.value = false

        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
        super.onDestroy()

        if (pointView1 != null || pointView2 != null) {
            hidePointWindows()
        }
    }

    private fun createNotification(): Notification {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Overlay",
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)

        val stopIntent = createStopPendingIntent()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_brush)
            .setContentTitle(this.getString(R.string.notification_title))
            .setContentText(this.getString(R.string.notification_text))
            .setOngoing(true)
            .addAction(
                R.drawable.ic_close_windows,
                this.getString(R.string.notification_close),
                stopIntent
            )
            .build()
    }

    private fun showPoints(num: Int) {
        if (pointView1 != null || pointView2 != null || pointView3 != null) return

        if (num >= 1) {
            pointView1 = ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@OverlayService)
                setViewTreeSavedStateRegistryOwner(this@OverlayService)
                setContent {
                    OverlayPointUi(
                        "1",
                        point1Aligned.collectAsState().value,
                        onMove = { dx, dy -> movePoint1By(dx, dy) }
                    )
                }
            }

            pointLayout1 = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply { gravity = Gravity.CENTER }

            windowManager.addView(pointView1, pointLayout1)
        }

        if (num >= 2) {
            pointView2 = ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@OverlayService)
                setViewTreeSavedStateRegistryOwner(this@OverlayService)
                setContent {
                    OverlayPointUi(
                        "2",
                        point2Aligned.collectAsState().value,
                        onMove = { dx, dy -> movePoint2By(dx, dy) }
                    )
                }
            }

            pointLayout2 = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply { gravity = Gravity.CENTER }

            windowManager.addView(pointView2, pointLayout2)
        }

        if (num >= 3) {
            pointView3 = ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@OverlayService)
                setViewTreeSavedStateRegistryOwner(this@OverlayService)
                setContent {
                    OverlayPointUi(
                        "3",
                        point3Aligned.collectAsState().value,
                        onMove = { dx, dy -> movePoint3By(dx, dy) }
                    )
                }
            }

            pointLayout3 = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply { gravity = Gravity.CENTER }

            windowManager.addView(pointView3, pointLayout3)
        }
    }

    private fun movePoint1By(dx: Int, dy: Int) {
        pointLayout1?.x += dx
        pointLayout1?.y += dy
        windowManager.updateViewLayout(pointView1, pointLayout1)
        updateLineState(movedPoint = pointView1)
    }

    private fun movePoint2By(dx: Int, dy: Int) {
        pointLayout2?.x += dx
        pointLayout2?.y += dy
        windowManager.updateViewLayout(pointView2, pointLayout2)
        updateLineState(movedPoint = pointView2)
    }

    private fun movePoint3By(dx: Int, dy: Int) {
        pointLayout3?.x += dx
        pointLayout3?.y += dy
        windowManager.updateViewLayout(pointView3, pointLayout3)
        updateLineState(movedPoint = pointView3)
    }

    private val SNAP_THRESHOLD = 5

    private fun updateLineState(movedPoint: ComposeView?) {
        if (movedPoint == null) return

        val points = listOf(pointView1, pointView2, pointView3)
        val layouts = listOf(pointLayout1, pointLayout2, pointLayout3)

        val index = points.indexOf(movedPoint)
        val movedLayout = layouts.getOrNull(index) ?: return

        var newX = movedLayout.x
        var newY = movedLayout.y

        for (i in points.indices) {
            if (i == index) continue
            val otherLayout = layouts.getOrNull(i) ?: continue

            if (kotlin.math.abs(newX - otherLayout.x) <= SNAP_THRESHOLD) {
                newX = otherLayout.x
            }

            if (kotlin.math.abs(newY - otherLayout.y) <= SNAP_THRESHOLD) {
                newY = otherLayout.y
            }
        }

        movedLayout.x = newX
        movedLayout.y = newY
        windowManager.updateViewLayout(movedPoint, movedLayout)

        val l1 = pointLayout1
        val l2 = pointLayout2
        val l3 = pointLayout3

        point1Aligned.value = (l1 != null && l2 != null && (l1.x == l2.x || l1.y == l2.y)) ||
                (l1 != null && l3 != null && (l1.x == l3.x || l1.y == l3.y))

        point2Aligned.value = (l2 != null && l1 != null && (l2.x == l1.x || l2.y == l1.y)) ||
                (l2 != null && l3 != null && (l2.x == l3.x || l2.y == l3.y))

        point3Aligned.value = (l3 != null && l1 != null && (l3.x == l1.x || l3.y == l1.y)) ||
                (l3 != null && l2 != null && (l3.x == l2.x || l3.y == l2.y))
    }

    private fun hidePointWindows() {
        pointView1?.let {
            if (it.isAttachedToWindow) windowManager.removeView(it)
            pointView1 = null
            pointLayout1 = null
        }

        pointView2?.let {
            if (it.isAttachedToWindow) windowManager.removeView(it)
            pointView2 = null
            pointLayout2 = null
        }

        pointView3?.let {
            if (it.isAttachedToWindow) windowManager.removeView(it)
            pointView3 = null
            pointLayout3 = null
        }
    }

    private fun createStopPendingIntent(): PendingIntent {
        val intent = Intent(this, StopOverlayReceiver::class.java)
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

private fun getCenterOnScreen(view: ComposeView): Pair<Float, Float> {
    val location = IntArray(2)
    view.getLocationOnScreen(location)

    val centerX = location[0] + view.width / 2f
    val centerY = location[1] + view.height / 2f

    return centerX to centerY
}

class StopOverlayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (context is OverlayService) {
            context.stopSelf()
        } else {
            val serviceIntent = Intent(context, OverlayService::class.java)
            context.stopService(serviceIntent)
        }
    }
}

fun hasOverlayPermission(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun hasNotificationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}