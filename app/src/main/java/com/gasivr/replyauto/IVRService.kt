package com.gasivr.replyauto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class IVRService : Service() {

    private lateinit var audioManager: AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val number = intent?.getStringExtra("INCOMING_NUMBER") ?: "Unknown"
        startForeground(1, createNotification("Playing audio to $number..."))

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Wait just 1 second after user answers before injecting audio
        handler.postDelayed({
            injectAudioToCall()
        }, 1000)

        return START_NOT_STICKY
    }

    private fun injectAudioToCall() {
        // EXACT AUDIO ROUTING TO BYPASS AEC AND INJECT INTO CALL TX
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        
        // Ensure speakerphone is OFF to prevent acoustic echo cancellation blocking the mic
        audioManager.isSpeakerphoneOn = false 

        Log.d("IVRService", "Attempting direct audio injection. Mode: IN_COMMUNICATION")

        try {
            // Set max volume for the voice call stream
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0)

            // Define exact AudioAttributes to tell Android "This IS the call"
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            mediaPlayer = MediaPlayer.create(this, R.raw.fliki_sample)
            mediaPlayer?.setAudioAttributes(attributes)

            mediaPlayer?.setOnCompletionListener {
                Log.d("IVRService", "Audio message finished playing.")
                listenForResponse()
            }
            
            mediaPlayer?.start()
            Log.d("IVRService", "Started playing Malayalam Audio Message directly into Voice Communication.")
            
        } catch (e: Exception) {
            Log.e("IVRService", "Error injecting audio", e)
        }
    }

    private fun listenForResponse() {
        Log.d("IVRService", "Waiting for caller response (15 seconds).")
        handler.postDelayed({
            alertOwnerToPickup()
        }, 15000)
    }

    private fun alertOwnerToPickup() {
        Log.d("IVRService", "Alerting owner to pick up the phone.")
        // Turn off call routing to ring an alarm tone locally
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = true
        
        val ringtone = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_RINGTONE_URI)
        ringtone.isLooping = true
        ringtone.start()
        
        // Stop the ringtone after 30 seconds
        handler.postDelayed({
            ringtone.stop()
            ringtone.release()
            stopSelf()
        }, 30000)
    }

    private fun createNotification(contentText: String): android.app.Notification {
        val channelId = "ivr_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "IVR Service Channel", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Gas Auto-Reply Active")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        audioManager.isSpeakerphoneOn = false
        audioManager.mode = AudioManager.MODE_NORMAL
    }

    override fun onBind(intent: Intent?): IBinder? = null
}