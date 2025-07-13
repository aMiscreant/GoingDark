package com.yourname.goingdark

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    
    private var lockdownActive = false
    private val CHANNEL_ID = "going_dark_channel"
    private val SERVICE_ID = 1
    private val REQUEST_CODE_POST_NOTIFICATIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Request POST_NOTIFICATIONS permission on Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        val logo = findViewById<ImageView>(R.id.logoView)
        logo.setImageResource(R.drawable.ic_lock)

        showPersistentNotification("Lockdown: disabled")
    
        if (!hasRootAccess()) {
            Toast.makeText(this, "Root access is required. Please enable it in Magisk.", Toast.LENGTH_LONG).show()
        }

        logo.setOnClickListener {
            if (!lockdownActive) {
                confirmLockdown {
                    lockdownActive = true
                    logo.setImageResource(R.drawable.ic_logo)
                    runLockdownActions()
                    showPersistentNotification("Lockdown: online")
                }
            } else {
                lockdownActive = false
                logo.setImageResource(R.drawable.ic_lock)
                stopLockdownActions()
                removeNotification()
                showPersistentNotification("Lockdown: disabled")
            }
        }

        createNotificationChannel()
    }

    private fun hasRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()
            output.contains("uid=0")
        } catch (e: Exception) {
            false
        }
    }

    private fun confirmLockdown(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Activate Lockdown?")
            .setMessage("This will disable Wi-Fi, Bluetooth, ADB, clear logs, and more. Are you sure?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPersistentNotification(contentText: String) {
        Log.d("GoingDark", "Notification triggered: $contentText")

        val serviceIntent = Intent(this, ForegroundService::class.java).apply {
            putExtra("contentText", contentText)
        }

        startForegroundService(serviceIntent)
    }

    private fun removeNotification() {
        stopService(Intent(this, ForegroundService::class.java))
    }

    private fun executeCommand(cmd: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
            val output = process.inputStream.bufferedReader().use { it.readText() }
            val error = process.errorStream.bufferedReader().use { it.readText() }
            process.waitFor()
            if (process.exitValue() == 0) {
                println("✅ $cmd\n$output")
            } else {
                println("❌ $cmd\n$error")
            }
        } catch (e: Exception) {
            println("❌ Exception running command: $cmd\n${e.message}")
            e.printStackTrace()
        }
    }

    private fun runLockdownActions() {
        val commands = listOf(
            // Standard Lockdown
            "settings put global airplane_mode_on 1",
            "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true",
            "svc wifi disable",
            "svc bluetooth disable",
            "svc data disable",

            // Flush DNS + ARP cache
            "ndc resolver flushdefaultif",                    // Flush DNS
            "ip neigh flush all",                             // Flush ARP cache

            // Disable Location
            "settings put secure location_providers_allowed -gps",
            "settings put secure location_providers_allowed -network",
            "settings put secure location_mode 0",

            // Clear clipboard
            "cmd clipboard set \"\"",

            // Wipe logs and net traces
            "logcat -c",
            "rm -rf /data/tombstones/*",
            "dumpsys batterystats --reset",
            "rm -rf /data/system/netstats/*",

            // ADB off + clear keys
            "settings put global adb_enabled 0",
            "rm /data/misc/adb/adb_keys",

            // Advertising ID reset (depends on Android version support)
            "settings put secure advertising_id_reset true",

            // Disable notifications on lockscreen
            "settings put secure lock_screen_show_notifications 0",

            // Disable background scanning
            "settings put global wifi_scan_always_enabled 0",
            "settings put global ble_scan_always_enabled 0",

            // Remove from recent tasks list (your app)
            "am set-inactive com.yourname.goingdark true",

            // Optional UX tweak
            "settings put system vibrate_when_ringing 0"
        )

        for (cmd in commands) {
            executeCommand(cmd)
        }
    }

    private fun stopLockdownActions() {
        val commands = listOf(
            "settings put global airplane_mode_on 0",
            "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false",
            "svc wifi enable",
            "svc bluetooth enable",
            "svc data enable",
            "settings put global adb_enabled 1",

            // Re-enable background scanning
            "settings put global wifi_scan_always_enabled 1",
            "settings put global ble_scan_always_enabled 1",

            // Re-enable lockscreen notifications
            "settings put secure lock_screen_show_notifications 1",

            // Restore app to active
            "am set-inactive com.yourname.goingdark false",
            
            // Restore ADB
            "settings put global adb_enabled 1",

            "settings put system vibrate_when_ringing 1"
        )

        for (cmd in commands) {
            executeCommand(cmd)
        }
    }

    private fun createNotificationChannel() {
        val name = "Going Dark Channel"
        val descriptionText = "Notifications for Going Dark"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        removeNotification()
    }
}

