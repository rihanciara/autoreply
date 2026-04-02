package com.gasivr.replyauto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 101

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkPermissionsButton: Button = findViewById(R.id.btn_check_permissions)
        val statusText: TextView = findViewById(R.id.tv_status)

        checkPermissionsButton.setOnClickListener {
            if (hasAllPermissions()) {
                Toast.makeText(this, "All permissions granted! App is ready to filter calls.", Toast.LENGTH_LONG).show()
                statusText.text = "Status: READY (Filter Active)"
            } else {
                requestPermissions()
            }
        }
    }

    private fun hasAllPermissions(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (hasAllPermissions()) {
                Toast.makeText(this, "Permissions granted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions required for app to function", Toast.LENGTH_LONG).show()
            }
        }
    }
}