package com.example.castlerace.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundController {

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val sampleRate = 44100

    fun playSelect(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            playTones(
                durationMs = 40,
                generator = { t, total ->
                    val decay = exp(-t * 28.0)
                    (sin(2.0 * PI * 850.0 * t) * decay * 0.4).toFloat()
                }
            )
        }
    }

    fun playMove(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            playTones(
                durationMs = 70,
                generator = { t, total ->
                    val decay = exp(-t * 18.0)
                    (sin(2.0 * PI * 340.0 * t) * decay * 0.55).toFloat()
                }
            )
        }
    }

    fun playCapture(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            playTones(
                durationMs = 130,
                generator = { t, total ->
                    val decay = exp(-t * 12.0)
                    val tone1 = sin(2.0 * PI * 420.0 * t)
                    val tone2 = sin(2.0 * PI * 680.0 * t) * 0.7
                    val noise = ((Math.random() - 0.5) * 0.25).toFloat()
                    ((tone1 + tone2 + noise) * decay * 0.6).toFloat()
                }
            )
        }
    }

    fun playCastleEntry(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            playTones(
                durationMs = 260,
                generator = { t, total ->
                    val decay = exp(-t * 6.0)
                    val c5 = sin(2.0 * PI * 523.25 * t)
                    val e5 = sin(2.0 * PI * 659.25 * t)
                    val g5 = sin(2.0 * PI * 783.99 * t)
                    ((c5 + e5 + g5) / 3.0 * decay * 0.7).toFloat()
                }
            )
        }
    }

    fun playWin(enabled: Boolean) {
        if (!enabled) return
        audioScope.launch {
            // Ascending fanfare: C5 -> E5 -> G5 -> C6
            val noteDurationMs = 90
            val totalDurationMs = noteDurationMs * 4
            playTones(
                durationMs = totalDurationMs,
                generator = { t, total ->
                    val noteIndex = ((t / (totalDurationMs / 1000.0)) * 4.0).toInt().coerceIn(0, 3)
                    val freq = when (noteIndex) {
                        0 -> 523.25 // C5
                        1 -> 659.25 // E5
                        2 -> 783.99 // G5
                        else -> 1046.50 // C6
                    }
                    val localT = t % (noteDurationMs / 1000.0)
                    val localDecay = exp(-localT * 8.0)
                    (sin(2.0 * PI * freq * t) * localDecay * 0.75).toFloat()
                }
            )
        }
    }

    private fun playTones(durationMs: Int, generator: (timeSeconds: Double, totalSeconds: Double) -> Float) {
        try {
            val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val totalSeconds = durationMs / 1000.0
            val buffer = ShortArray(totalSamples)

            for (i in 0 until totalSamples) {
                val t = i.toDouble() / sampleRate
                val sampleValue = generator(t, totalSeconds).coerceIn(-1.0f, 1.0f)
                buffer[i] = (sampleValue * Short.MAX_VALUE).toInt().toShort()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()

            // Release track after playing
            Thread.sleep(durationMs.toLong() + 50)
            track.stop()
            track.release()
        } catch (_: Exception) {
            // Audio failure safe guard
        }
    }
}
