package com.example.castlerace.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

class HapticController(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun vibrateSelect(view: View?, enabled: Boolean) {
        if (!enabled) return
        if (view != null && view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)) {
            return
        }
        vibrateMillis(12, 40)
    }

    fun vibrateMove(view: View?, enabled: Boolean) {
        if (!enabled) return
        if (view != null && view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)) {
            return
        }
        vibrateMillis(20, 60)
    }

    fun vibrateCapture(view: View?, enabled: Boolean) {
        if (!enabled) return
        if (view != null && view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)) {
            return
        }
        vibrateMillis(35, 120)
    }

    fun vibrateCastleEntry(view: View?, enabled: Boolean) {
        if (!enabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && view != null) {
            if (view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)) {
                return
            }
        }
        vibratePattern(longArrayOf(0, 30, 40, 50), intArrayOf(0, 100, 0, 180))
    }

    fun vibrateWin(view: View?, enabled: Boolean) {
        if (!enabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && view != null) {
            if (view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)) {
                return
            }
        }
        vibratePattern(
            longArrayOf(0, 50, 40, 60, 40, 90),
            intArrayOf(0, 120, 0, 180, 0, 255)
        )
    }

    private fun vibrateMillis(ms: Long, amplitude: Int) {
        vibrator?.let { v ->
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val clamped = amplitude.coerceIn(1, 255)
                v.vibrate(VibrationEffect.createOneShot(ms, clamped))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(ms)
            }
        }
    }

    private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
        vibrator?.let { v ->
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(timings, -1)
            }
        }
    }
}
