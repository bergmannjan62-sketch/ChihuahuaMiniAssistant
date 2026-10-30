# Chihuahua Mini-Assistent – Android-Starterprojekt

Ein erstes natives Android-Studio-Projekt für einen deutschsprachigen Mini-Assistenten:
- Chat-Oberfläche
- Spracheingabe über die Android-Spracherkennung
- Antworten vorlesen per Android Text-to-Speech
- einige lokale Demo-Befehle (YouTube, Kamera, Einstellungen)
- optionales schwebendes Chihuahua-Symbol über anderen Apps

## Öffnen und bauen
1. Installiere Android Studio auf einem Computer.
2. Entpacke dieses ZIP.
3. Öffne den Ordner `ChihuahuaMiniAssistant` in Android Studio.
4. Lass Gradle synchronisieren und wähle **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Übertrage die APK auf das Samsung Galaxy S25 und installiere sie. Android kann beim ersten Sideload nach Erlaubnis für diese Quelle fragen.

## Aktueller Stand / wichtig
Dies ist ein Starter-Prototyp, keine fertige Store-App. Das schwebende Symbol ist aktuell ein Chihuahua-Emoji und noch kein echtes animiertes 3D-Modell. Die KI-Anbindung ist noch nicht konfiguriert; bis dahin gibt es lokale Demo-Antworten. Für die nächste Ausbaustufe können wir ein GLB-3D-Modell und eine KI-API mit Einstellungsdialog ergänzen.

Das schwebende Symbol benötigt die Android-Berechtigung **Über anderen Apps anzeigen**. Mikrofon und Sprachausgabe verwenden Android-Funktionen. Einige Spracherkennungsanbieter senden Audio zur Verarbeitung an ihren Dienst.
