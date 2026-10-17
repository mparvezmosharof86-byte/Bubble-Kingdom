package com.example.game.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = try {
        TextToSpeech(context.applicationContext, this)
    } catch (_: Throwable) {
        null
    }
    private var isInitialized = false

    var voiceVolume: Float = 1.0f
    private var lastSpokenTime = 0L

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                tts?.let { engine ->
                    val result = engine.setLanguage(Locale.US)
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        try {
                            val voices = engine.voices
                            val femaleVoice = voices?.firstOrNull {
                                it.name.contains("female", ignoreCase = true) ||
                                        it.name.contains("en-us-x-sfg", ignoreCase = true) ||
                                        it.name.contains("en-us-x-iol", ignoreCase = true)
                            }
                            if (femaleVoice != null) {
                                engine.voice = femaleVoice
                            }
                        } catch (_: Throwable) {}

                        engine.setPitch(1.35f)
                        engine.setSpeechRate(1.08f)
                        isInitialized = true
                    }
                }
            } catch (_: Throwable) {}
        }
    }

    fun speak(text: String, priority: Boolean = false) {
        if (!isInitialized || voiceVolume <= 0.05f) return

        val now = System.currentTimeMillis()
        if (!priority && now - lastSpokenTime < 1800) {
            return
        }
        lastSpokenTime = now

        try {
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, voiceVolume)
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "VOICE_${System.currentTimeMillis()}")
        } catch (_: Throwable) {}
    }

    fun onGameStart() = speak("Let's play!", priority = true)
    fun onMatch() = speak("Great shot!")
    fun onCombo(combo: Int) {
        if (combo >= 3) {
            speak("Unbelievable combo!", priority = true)
        } else {
            speak("Amazing combo!")
        }
    }
    fun onPetRescued() = speak("Pet rescued! Wonderful!", priority = true)
    fun onBigDrop() = speak("Fantastic!")
    fun onLevelComplete() = speak("Level complete! Superb!", priority = true)
    fun onLevelFailed() = speak("Don't give up! Try again!")
    fun onBoosterUsed(name: String) = speak("$name activated!")

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Throwable) {}
    }
}
