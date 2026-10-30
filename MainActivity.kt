package de.merios.chihuahua

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.*
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var messages: LinearLayout
    private lateinit var input: EditText
    private lateinit var tts: TextToSpeech
    private var speechReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        window.statusBarColor = Color.rgb(16, 21, 16)
        window.navigationBarColor = Color.rgb(16, 21, 16)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(14))
            setBackgroundColor(Color.rgb(16, 21, 16))
        }
        val title = TextView(this).apply {
            text = "Chihuahua 🐶"
            textSize = 27f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        root.addView(title)
        val subtitle = TextView(this).apply {
            text = "Dein kleiner persönlicher Assistent"
            textSize = 14f
            setTextColor(Color.rgb(177, 194, 177))
            setPadding(0, dp(4), 0, dp(12))
        }
        root.addView(subtitle)

        val hero = TextView(this).apply {
            text = "🐶\n\nWuff! Ich bin bereit."
            textSize = 28f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(31, 43, 31))
            setPadding(dp(10), dp(16), dp(10), dp(16))
        }
        root.addView(hero, LinearLayout.LayoutParams(-1, dp(150)))

        messages = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val scroll = ScrollView(this).apply { addView(messages) }
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        addMessage("Chihuahua", "Hey! Frag mich etwas oder tippe auf das Mikrofon. Für einfache Befehle bin ich schon bereit.", false)

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        input = EditText(this).apply {
            hint = "Schreib mir etwas …"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            setSingleLine(true)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setBackgroundColor(Color.rgb(37, 48, 37))
        }
        row.addView(input, LinearLayout.LayoutParams(0, dp(52), 1f))
        row.addView(button("🎤") { listen() }, LinearLayout.LayoutParams(dp(52), dp(52)))
        row.addView(button("➤") { sendMessage() }, LinearLayout.LayoutParams(dp(52), dp(52)))
        root.addView(row)

        val floatButton = button("🐾 Chihuahua schweben lassen") { enableFloating() }
        val floatParams = LinearLayout.LayoutParams(-1, dp(48)); floatParams.topMargin = dp(10)
        root.addView(floatButton, floatParams)
        setContentView(root)
    }

    private fun button(label: String, action: () -> Unit) = Button(this).apply {
        text = label
        isAllCaps = false
        setTextColor(Color.rgb(16, 24, 16))
        setBackgroundColor(Color.rgb(126, 224, 129))
        setOnClickListener { action() }
    }

    private fun addMessage(who: String, body: String, user: Boolean) {
        val tv = TextView(this).apply {
            text = "$who: $body"
            textSize = 15f
            setTextColor(if (user) Color.rgb(210, 255, 211) else Color.WHITE)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            setBackgroundColor(if (user) Color.rgb(33, 62, 35) else Color.rgb(31, 43, 31))
        }
        val p = LinearLayout.LayoutParams(-1, -2); p.topMargin = dp(8)
        messages.addView(tv, p)
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return
        addMessage("Du", text, true)
        input.setText("")
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(input.windowToken, 0)
        val answer = respondTo(text)
        addMessage("Chihuahua", answer, false)
        if (speechReady) tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "dog-reply")
    }

    private fun respondTo(text: String): String {
        val s = text.lowercase(Locale.GERMANY)
        return when {
            s.contains("uhr") -> "Die Uhrzeit findest du oben in deiner Statusleiste. Später kann ich dir auch Datum und Uhrzeit direkt anzeigen."
            s.contains("hallo") || s.contains("hi") || s.contains("hey") -> "Wuff-wuff! Schön, dass du da bist! 🐾"
            s.contains("wie heißt") -> "Ich bin dein Chihuahua-Assistent. Einen Namen kannst du mir später in den Einstellungen geben."
            s.contains("öffne youtube") -> { openApp("com.google.android.youtube"); "Ich versuche, YouTube zu öffnen." }
            s.contains("öffne kamera") || s.contains("öffne die kamera") -> { openApp("com.sec.android.app.camera"); "Ich versuche, die Kamera zu öffnen." }
            s.contains("öffne einstellungen") -> { startActivity(Intent(Settings.ACTION_SETTINGS)); "Hier sind die Einstellungen." }
            s.contains("hilfe") || s.contains("was kannst du") -> "Ich kann schon chatten, Antworten vorlesen und ein paar einfache Befehle ausführen. Die richtige KI-Anbindung bauen wir als nächsten Schritt ein."
            else -> "Wuff! Ich habe dich verstanden: „$text“. Meine KI-Anbindung ist noch nicht eingerichtet. Im nächsten Schritt ergänzen wir sie, damit ich frei auf Fragen antworten kann."
        }
    }

    private fun openApp(pkg: String) {
        try { val i = packageManager.getLaunchIntentForPackage(pkg); if (i != null) startActivity(i) } catch (_: Exception) { }
    }

    private fun listen() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            addMessage("Chihuahua", "Spracherkennung ist auf diesem Gerät gerade nicht verfügbar.", false); return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sprich mit deinem Chihuahua")
        }
        try { startActivityForResult(intent, 42) } catch (_: Exception) { addMessage("Chihuahua", "Ich konnte die Spracherkennung nicht starten.", false) }
    }

    @Deprecated("Android activity result API kept simple for starter project")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) { input.setText(spoken); sendMessage() }
        }
    }

    private fun enableFloating() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            Toast.makeText(this, "Bitte erlaube „Über anderen Apps anzeigen“ und tippe danach erneut.", Toast.LENGTH_LONG).show()
            return
        }
        startService(Intent(this, FloatingDogService::class.java))
        Toast.makeText(this, "Dein Chihuahua schwebt jetzt. Tippe auf ihn, um ihn zu schließen.", Toast.LENGTH_LONG).show()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) { speechReady = true; tts.language = Locale.GERMANY }
    }
    override fun onDestroy() { if (::tts.isInitialized) { tts.stop(); tts.shutdown() }; super.onDestroy() }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
