package com.adgh.rabiesreminder

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * Singleton utility for playing and stopping looping alarm notification audio.
 *
 * Uses [MediaPlayer] with resource `R.raw.nine_am` and automatically stops audio playback
 * after 90 seconds via a main-thread [Handler] timeout callback.
 */
object AlarmSoundManager {
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var stopRunnable: Runnable? = null

    /**
     * Starts looping alarm playback if notification permissions are active.
     * Automatically schedules auto-stop after 90 seconds (90,000 ms).
     *
     * @param context Context used to create the [MediaPlayer] and post toast messages.
     */
    fun startAlarm(context: Context) {
        if (areNotificationsEnabled(context)) {
            stopAlarm()
            try {
                mediaPlayer = MediaPlayer.create(context, R.raw.nine_am).apply {
                    isLooping = true
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error playing alarm sound", Toast.LENGTH_SHORT).show()
            }

            handler = Handler(Looper.getMainLooper())
            stopRunnable = Runnable {
                stopAlarm()
            }
            handler?.postDelayed(stopRunnable!!, (90L * 1000L))
        } else {
            Toast.makeText(context, context.getString(R.string.please_enable_notifications_to_use_the_reminder), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Stops active alarm audio playback, cancels pending timeout callbacks, and releases [MediaPlayer] memory.
     */
    fun stopAlarm() {
        stopRunnable?.let { handler?.removeCallbacks(it) }
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
    }
}

