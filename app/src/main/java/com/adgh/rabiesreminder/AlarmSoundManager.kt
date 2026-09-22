package com.adgh.rabiesreminder

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object AlarmSoundManager {
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var stopRunnable: Runnable? = null

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
