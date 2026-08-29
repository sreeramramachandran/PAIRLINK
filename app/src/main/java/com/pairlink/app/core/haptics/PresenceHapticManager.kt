package com.pairlink.app.core.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages continuous heartbeat haptic vibrations when partner sends presence.
 * Supports VibratorManager (Android 12+) and legacy Vibrator (Android 8-11).
 */
@Singleton
class PresenceHapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isVibrating = false

    private val vibratorManager: VibratorManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        } else null
    }

    private val defaultVibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Heartbeat pattern: [delay, pulse1, rest, pulse2, long_rest]
    private val heartbeatTimings = longArrayOf(0, 130, 90, 220, 550)
    private val heartbeatAmplitudes = intArrayOf(0, 220, 0, 255, 0)

    /**
     * Starts continuous rhythmic heartbeat vibration when partner is holding the presence heart.
     */
    fun startHeartbeatVibration() {
        if (isVibrating) return
        isVibrating = true

        try {
            val vibrator = defaultVibrator ?: return
            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    heartbeatTimings,
                    heartbeatAmplitudes,
                    0 // Repeat at index 0 indefinitely
                )
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(heartbeatTimings, 0)
            }
        } catch (_: Exception) {
            isVibrating = false
        }
    }

    /**
     * Triggers a distinct double-heartbeat vibration pulse when partner taps the love symbol.
     */
    fun triggerPulseVibration() {
        try {
            val vibrator = defaultVibrator ?: return
            if (!vibrator.hasVibrator()) return

            // Deep romantic multi-pulse heartbeat pattern (~2.4s total duration)
            val pulseTimings = longArrayOf(0, 350, 150, 450, 150, 600, 200, 500)
            val pulseAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pulseTimings, pulseAmplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pulseTimings, -1)
            }
        } catch (_: Exception) {
            // Ignore
        }
    }

    /**
     * Immediately stops any active presence vibration.
     */
    fun stopHeartbeatVibration() {
        if (!isVibrating) return
        isVibrating = false

        try {
            defaultVibrator?.cancel()
        } catch (_: Exception) {
            // Ignore
        }
    }

    /**
     * Triggers a subtle single tap feedback when user presses the heart.
     */
    fun triggerHeartPressFeedback() {
        try {
            val vibrator = defaultVibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (_: Exception) {
            // Ignore
        }
    }
}
