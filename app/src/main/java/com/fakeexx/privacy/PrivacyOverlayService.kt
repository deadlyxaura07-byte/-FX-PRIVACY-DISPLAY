package com.fakeexx.privacy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class PrivacyOverlayService : Service() {
 private var wm: WindowManager? = null
 private var layer: View? = null
 private val channelId = "fx_privacy_active"

 override fun onCreate() {
  super.onCreate()
  if (Build.VERSION.SDK_INT >= 26) {
   getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(channelId, "Privacy protection", NotificationManager.IMPORTANCE_LOW))
  }
  val notification = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, channelId) else @Suppress("DEPRECATION") Notification.Builder(this)
  notification.setContentTitle("FX Privacy is active").setContentText("Screen privacy overlay is running").setSmallIcon(android.R.drawable.ic_lock_lock).setOngoing(true)
  startForeground(41, notification.build())
 }

 override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
  showLayer((intent?.getFloatExtra("intensity", 0.42f) ?: 0.42f).coerceIn(0.05f, 0.85f))
  return START_STICKY
 }

 private fun showLayer(alpha: Float) {
  val manager = wm ?: (getSystemService(WINDOW_SERVICE) as WindowManager).also { wm = it }
  if (layer == null) {
   val view = View(this).apply { setBackgroundColor(android.graphics.Color.BLACK); this.alpha = alpha }
   val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
   val params = WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
   params.gravity = Gravity.TOP or Gravity.START
   manager.addView(view, params)
   layer = view
  } else layer?.alpha = alpha
 }

 override fun onDestroy() {
  layer?.let { wm?.removeView(it) }
  layer = null
  super.onDestroy()
 }
 override fun onBind(intent: Intent?): IBinder? = null
}