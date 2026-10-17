package com.example.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.game.model.BubbleColor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {
    // Safe coroutine handler to prevent audio failures from ever crashing the app
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
    private val scope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)

    var sfxVolume: Float = 1.0f
    var musicVolume: Float = 0.6f
    var hapticsEnabled: Boolean = true

    private var musicJob: Job? = null
    private var musicTrack: AudioTrack? = null

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Throwable) {
            null
        }
    }

    fun playHaptic(durationMs: Long = 25, intensity: Int = 120) {
        if (!hapticsEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, intensity.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Throwable) {}
    }

    /**
     * Synthesizes and plays a bubble pop sound with pitch tailored to the bubble color
     */
    fun playBubblePop(color: BubbleColor) {
        if (sfxVolume <= 0.01f) return
        playHaptic(20, 100)

        scope.launch {
            try {
                val baseFreq = when (color) {
                    BubbleColor.RED -> 523.25f
                    BubbleColor.ORANGE -> 587.33f
                    BubbleColor.YELLOW -> 659.25f
                    BubbleColor.GREEN -> 698.46f
                    BubbleColor.BLUE -> 783.99f
                    BubbleColor.PURPLE -> 880.00f
                }
                playToneSweep(baseFreq * 0.8f, baseFreq * 1.4f, durationMs = 70, volume = sfxVolume)
            } catch (_: Throwable) {}
        }
    }

    fun playShoot() {
        if (sfxVolume <= 0.01f) return
        playHaptic(15, 80)
        scope.launch {
            try {
                playToneSweep(300f, 650f, durationMs = 90, volume = sfxVolume * 0.8f)
            } catch (_: Throwable) {}
        }
    }

    fun playWallBounce() {
        if (sfxVolume <= 0.01f) return
        playHaptic(10, 60)
        scope.launch {
            try {
                playTone(880f, durationMs = 45, volume = sfxVolume * 0.5f)
            } catch (_: Throwable) {}
        }
    }

    fun playComboChime(comboLevel: Int) {
        if (sfxVolume <= 0.01f) return
        playHaptic(40, 180)
        scope.launch {
            try {
                val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
                val note = notes.getOrElse((comboLevel - 1).coerceAtMost(notes.size - 1)) { 1046.50f }
                playToneSweep(note * 0.9f, note * 1.15f, durationMs = 120, volume = sfxVolume)
            } catch (_: Throwable) {}
        }
    }

    fun playPetRescueChime() {
        if (sfxVolume <= 0.01f) return
        playHaptic(60, 220)
        scope.launch {
            try {
                val chords = listOf(587.33f, 739.99f, 880f, 1174.66f)
                for (freq in chords) {
                    playTone(freq, durationMs = 75, volume = sfxVolume)
                    kotlinx.coroutines.delay(40)
                }
            } catch (_: Throwable) {}
        }
    }

    fun playBombExplosion() {
        if (sfxVolume <= 0.01f) return
        playHaptic(80, 255)
        scope.launch {
            try {
                playToneSweep(250f, 60f, durationMs = 250, volume = sfxVolume)
            } catch (_: Throwable) {}
        }
    }

    fun playVictoryFanfare() {
        if (sfxVolume <= 0.01f) return
        scope.launch {
            try {
                val melody = listOf(523f, 659f, 784f, 1046f, 1318f)
                for (note in melody) {
                    playTone(note, durationMs = 110, volume = sfxVolume)
                    kotlinx.coroutines.delay(80)
                }
            } catch (_: Throwable) {}
        }
    }

    fun playDefeatChime() {
        if (sfxVolume <= 0.01f) return
        scope.launch {
            try {
                val melody = listOf(440f, 415f, 392f, 349f)
                for (note in melody) {
                    playTone(note, durationMs = 130, volume = sfxVolume * 0.7f)
                    kotlinx.coroutines.delay(100)
                }
            } catch (_: Throwable) {}
        }
    }

    /**
     * Starts continuous soothing fantasy ambient melody loop with total crash immunity
     */
    fun startBackgroundMusic() {
        if (musicJob?.isActive == true) return

        musicJob = scope.launch {
            try {
                val sampleRate = 44100
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                if (minBuf <= 0) return@launch

                val bufferSize = maxOf(minBuf * 2, sampleRate)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                if (track.state != AudioTrack.STATE_INITIALIZED) {
                    try { track.release() } catch (_: Throwable) {}
                    return@launch
                }

                musicTrack = track
                track.play()

                val melodyFrequencies = doubleArrayOf(
                    261.63, 329.63, 392.00, 523.25,
                    293.66, 349.23, 440.00, 587.33,
                    329.63, 392.00, 493.88, 659.25,
                    392.00, 523.25, 659.25, 783.99
                )

                var melodyIdx = 0
                val noteDurationSamples = (sampleRate * 0.35).toInt()
                val shortBuf = ShortArray(noteDurationSamples)

                while (isActive) {
                    val vol = musicVolume
                    if (vol <= 0.01f) {
                        kotlinx.coroutines.delay(200)
                        continue
                    }

                    val freq = melodyFrequencies[melodyIdx % melodyFrequencies.size]
                    melodyIdx++

                    for (i in 0 until noteDurationSamples) {
                        val t = i.toDouble() / sampleRate
                        val progress = i.toDouble() / noteDurationSamples
                        val envelope = (1.0 - progress) * (1.0 - progress)
                        val sample = (sin(2.0 * PI * freq * t) * 0.7 + sin(4.0 * PI * freq * t) * 0.3) * envelope
                        shortBuf[i] = (sample * 14000 * vol).toInt().coerceIn(-32768, 32767).toShort()
                    }

                    track.write(shortBuf, 0, noteDurationSamples)
                }

                try {
                    track.stop()
                    track.release()
                } catch (_: Throwable) {}
            } catch (_: Throwable) {
                // Audio unavailable or buffer error on this environment, safely ignore
            }
        }
    }

    fun stopBackgroundMusic() {
        musicJob?.cancel()
        musicJob = null
        try {
            musicTrack?.stop()
            musicTrack?.release()
            musicTrack = null
        } catch (_: Throwable) {}
    }

    private fun playTone(frequency: Float, durationMs: Long, volume: Float) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val decay = 1.0 - (i.toDouble() / numSamples)
                val sample = sin(2.0 * PI * frequency * t) * decay
                buffer[i] = (sample * 28000 * volume).toInt().coerceIn(-32768, 32767).toShort()
            }

            playRawPcm(buffer, sampleRate)
        } catch (_: Throwable) {}
    }

    private fun playToneSweep(startFreq: Float, endFreq: Float, durationMs: Long, volume: Float) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return

            val buffer = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val decay = 1.0 - progress
                phase += 2.0 * PI * currentFreq / sampleRate
                val sample = sin(phase) * decay
                buffer[i] = (sample * 28000 * volume).toInt().coerceIn(-32768, 32767).toShort()
            }

            playRawPcm(buffer, sampleRate)
        } catch (_: Throwable) {}
    }

    private fun playRawPcm(buffer: ShortArray, sampleRate: Int) {
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            if (minBuf <= 0) return

            val bufferSizeBytes = maxOf(minBuf, buffer.size * 2)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSizeBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            if (track.state != AudioTrack.STATE_INITIALIZED) {
                try { track.release() } catch (_: Throwable) {}
                return
            }

            track.write(buffer, 0, buffer.size)
            track.play()

            scope.launch {
                val delayMs = (buffer.size * 1000L / sampleRate) + 50
                kotlinx.coroutines.delay(delayMs)
                try {
                    track.stop()
                    track.release()
                } catch (_: Throwable) {}
            }
        } catch (_: Throwable) {}
    }
}
