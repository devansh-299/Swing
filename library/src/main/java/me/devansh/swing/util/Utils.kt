package me.devansh.swing.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object Utils {

    private const val VIBRATION_SOFT = 50L
    private const val VIBRATION_MEDIUM = 100L
    private const val VIBRATION_HARD = 200L

    @JvmStatic
    fun vibrateDevice(context: Context?) {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(
                VIBRATION_SOFT,
                VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(VIBRATION_SOFT)
        }
    }
}