package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        txt_title.text = intent.getStringExtra(EXTRA_DOWNLOAD_TITLE)
        txt_description.text = intent.getStringExtra(EXTRA_DOWNLOAD_DESCRIPTION)

        txt_status.text = if(intent.getBooleanExtra(EXTRA_DOWNLOAD_STATUS, false)) {
            txt_status.setTextColor(Color.GREEN)
            getString(R.string.success)
        } else {
            txt_status.setTextColor(Color.RED)
            getString(R.string.failed)
        }

        txt_reason.text = intent.getIntExtra(EXTRA_DOWNLOAD_REASON, 0).toString()
        txt_uri.text = intent.getStringExtra(EXTRA_DOWNLOAD_URI)
        txt_size.text = intent.getIntExtra(EXTRA_DOWNLOAD_SIZE, 0).toString()

        btn_ok.setOnClickListener { finish() }

        this.getSystemService(NotificationManager::class.java)
            .cancel(intent.getLongExtra(EXTRA_DOWNLOAD_ID, 0).toInt())
    }

}
