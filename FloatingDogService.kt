package de.merios.chihuahua

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.graphics.Color

class FloatingDogService : Service() {
    private var wm: WindowManager? = null
    private var dog: TextView? = null
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (dog != null) return START_NOT_STICKY
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        dog = TextView(this).apply {
            text = "🐶"
            textSize = 42f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.TRANSPARENT)
            setOnClickListener { stopSelf() }
            contentDescription = "Schwebender Chihuahua – antippen zum Schließen"
        }
        val type = if (android.os.Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(150, 150, type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 20; params.y = 220
        wm?.addView(dog, params)
        return START_NOT_STICKY
    }
    override fun onDestroy() { try { if (dog != null) wm?.removeView(dog) } catch (_: Exception) {}; dog = null; super.onDestroy() }
}
