package com.dicoding.tamantic.view.activity.plant

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.MyPlant
import com.dicoding.tamantic.data.reminder.WaterReminderWorker
import com.dicoding.tamantic.databinding.ActivityDetailPlantBinding

class DetailPlantActivity : AppCompatActivity() {

    private lateinit var binding:  ActivityDetailPlantBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<MyPlant>("MY_PLANT")
        setMyPlant(data)
        setupAction()
        setupView()

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val title = "Tamantic"
        val message = "Reminder Siram Tanamanmu sekarang!"

        binding.btnActivateReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sendNotification(title, message, 10)
            }else{
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(10)
            }
        }
    }
    private fun sendNotification(title: String, message: String, delayIn: Int) {
        val intent = Intent(this, DetailPlantActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.logo_tamantic)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSubText("Reminder")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = builder.build()

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, WaterReminderWorker::class.java).apply {
            putExtra(WaterReminderWorker.NOTIFICATION_ID, NOTIFICATION_ID)
            putExtra(WaterReminderWorker.NOTIFICATION, notification)
        }

        val delayInMillis = delayIn * 24 * 60 * 60 * 1000

        val pendingAlarmIntent = PendingIntent.getBroadcast(this, 0, alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val triggerTime = SystemClock.elapsedRealtime() + delayIn * 1000
//        val triggerTime = System.currentTimeMillis() + delayInMillis
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingAlarmIntent)
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "Tamantic"
    }


    private fun setMyPlant(data: MyPlant?) {
        binding.descPlant.text = data?.description
        Glide.with(binding.imageDetailMyplant).load(data?.image).into(binding.imageDetailMyplant)
        binding.matahari.text = "${data?.matahari}51%"
        binding.pupuk.text = "${data?.pupuk}70%"
        binding.air.text = "${data?.air}90%"
        binding.gunting.text = "${data?.potong}30%"
    }

    private fun setupAction() {
        binding.actionBack.setOnClickListener { onBackPressed() }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val statusBarColor = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(this, android.R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> ContextCompat.getColor(this, R.color.white)
            else -> ContextCompat.getColor(this, R.color.white)
        }

        window.statusBarColor = statusBarColor
    }
}