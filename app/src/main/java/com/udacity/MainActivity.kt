package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity()
{
    //// Object Members //////////////////////////////////////////////////////////////////////////

    private var downloadID = 0L
    private var downloadOption = View.NO_ID

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { context?.let { intent?.let {
            custom_button.state = LoadingButton.Companion.ButtonState.Completed
            ContextCompat.getSystemService(context, NotificationManager::class.java)
                ?.notifyDownloadCompleted(context, downloadID, intent.asDetailActivityIntent(context, downloadID))
        }}}
    }

    //// Override ////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        custom_button.setOnClickListener {
            when (downloadOption) {
                R.id.rd_btn_glide -> download(
                    GLIDE_URL, "glide.zip", getString(R.string.glide_description)
                )
                R.id.rd_btn_loadapp -> download(
                    LOAD_APP_URL, "loadapp.zip", getString(R.string.loadapp_description)
                )
                R.id.rd_btn_retrofit -> download(
                    RETROFIT_URL, "retrofit.zip", getString(R.string.retrofit_description)
                )
                R.id.rd_btn_custom_url -> download(
                    edt_custom_url.text.toString(), "file", getString(R.string.app_description)
                )
                else -> Toast.makeText(this, R.string.no_option, Toast.LENGTH_SHORT).show()
            }
        }

    }

    //// Methods /////////////////////////////////////////////////////////////////////////////////

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) { downloadOption = view.id }
    }

    //// Functions ///////////////////////////////////////////////////////////////////////////////

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Downloads notifications"
                setShowBadge(false)
            }

            this.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(notificationChannel)
        }
    }

    private fun download(url: String, title: String, description: String) {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
        } else {
            custom_button.state = LoadingButton.Companion.ButtonState.Loading

            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(description)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
        }
    }

}
