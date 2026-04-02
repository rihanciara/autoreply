package com.gasivr.replyauto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val stateStr = intent.extras?.getString(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)

            // TRIGGER ONLY WHEN THE CALL IS ANSWERED (OFFHOOK), NOT WHEN RINGING
            if (stateStr == TelephonyManager.EXTRA_STATE_OFFHOOK && incomingNumber != null) {
                if (!isContactSaved(context, incomingNumber)) {
                    Log.d("CallReceiver", "Unknown number answered by user: $incomingNumber. Starting IVR Audio.")
                    // Start service to play audio
                    val serviceIntent = Intent(context, IVRService::class.java).apply {
                        putExtra("INCOMING_NUMBER", incomingNumber)
                    }
                    context.startForegroundService(serviceIntent)
                } else {
                    Log.d("CallReceiver", "Saved contact answered. Ignoring.")
                }
            }
        }
    }

    private fun isContactSaved(context: Context, number: String): Boolean {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        context.contentResolver.query(uri, projection, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                return true // Contact found
            }
        }
        return false // Contact not found
    }
}