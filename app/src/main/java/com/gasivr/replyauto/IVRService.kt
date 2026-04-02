package com.gasivr.replyauto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class IVRService : Service() {

    private lateinit var audioManager: AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val number = intent?.getStringExtra("INCOMING_NUMBER") ?: "Unknown"
        startForeground(1, createNotification("Screening call from $number..."))

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Step 1: Answer Call
        answerCall()

        // Step 2: Delay for connection setup, then turn on speaker and play audio
        handler.postDelayed({
            enableSpeakerAndPlayAudio()
        }, 2000)

        return START_NOT_STICKY
    }

    private fun answerCall() {
        try {
            val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ANSWER_PHONE_CALLS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                telecomManager.acceptRingingCall()
                Log.d("IVRService", "Call answered successfully.")
            }
        } catch (e: Exception) {
            Log.e("IVRService", "Failed to answer call", e)
        }
    }

    private fun enableSpeakerAndPlayAudio() {
        // FORCE SPEAKERPHONE ON
        // In Android 12+, we need to request audio focus differently and ensure the mode is IN_COMMUNICATION
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = true

        // Double check speakerphone actually turned on
        Log.d("IVRService", "Speakerphone enabled: ${audioManager.isSpeakerphoneOn}")

        try {
            // Set volume to absolute maximum so caller hears it via the microphone
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0)
            
            // Also max out media volume just in case it routes there
            val maxMediaVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMediaVolume, 0)

            mediaPlayer = MediaPlayer.create(this, R.raw.fliki_sample)
            
            // Set audio attributes to prioritize voice communication
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaPlayer?.setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .build()
                )
            }

            mediaPlayer?.setOnCompletionListener {
                Log.d("IVRService", "Audio message finished playing.")
                listenForResponse()
            }
            mediaPlayer?.start()
            Log.d("IVRService", "Playing Malayalam Audio Message on Stream: ${AudioManager.STREAM_VOICE_CALL}")
        } catch (e: Exception) {
            Log.e("IVRService", "Error playing media", e)
        }
    }

    private fun listenForResponse() {
        // NOTE: NATIVE ANDROID BLOCKS DETECTING DTMF (KEYPAD PRESSES LIKE '1') DURING A CALL.
        // It is an OS-level security restriction to prevent apps from stealing passwords/PINs.
        // Because we CANNOT detect if they press '1', we MUST use voice recognition (Speech-to-Text)
        // or a time-based workaround.
        // I have implemented the time-based workaround: if they don't hang up after 15 seconds,
        // the app assumes they want to talk to you and rings your phone loudly.
        
        Log.d("IVRService", "Waiting for caller response (15 seconds).")
        handler.postDelayed({
            alertOwnerToPickup()
        }, 15000)
    }

    private fun dropCall() {
        try {
            val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ANSWER_PHONE_CALLS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    telecomManager.endCall()
                    Log.d("IVRService", "Call ended automatically.")
                }
            }
        } catch (e: Exception) {
            Log.e("IVRService", "Failed to end call", e)
        }
    }

    private fun alertOwnerToPickup() {
        Log.d("IVRService", "Alerting owner to pick up the phone.")
        // Turn off speaker temporarily to ring an alarm tone locally
        audioManager.mode = AudioManager.MODE_RINGTONE
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
            // Note: Make sure to add an icon for this to work perfectly in production
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