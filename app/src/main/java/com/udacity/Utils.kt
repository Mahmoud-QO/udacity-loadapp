package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

//// CONSTANTS /////////////////////////////////////////////////////////////////////////////////////

const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"

const val EXTRA_DOWNLOAD_ID = "EXTRA_DOWNLOAD_ID"
const val EXTRA_DOWNLOAD_TITLE = "EXTRA_DOWNLOAD_TITLE"
const val EXTRA_DOWNLOAD_DESCRIPTION = "EXTRA_DOWNLOAD_DESCRIPTION"
const val EXTRA_DOWNLOAD_STATUS = "EXTRA_DOWNLOAD_STATUS"
const val EXTRA_DOWNLOAD_REASON = "EXTRA_DOWNLOAD_REASON"
const val EXTRA_DOWNLOAD_URI = "EXTRA_DOWNLOAD_URI"
const val EXTRA_DOWNLOAD_SIZE = "EXTRA_DOWNLOAD_SIZE"

//// NOTIFICATION //////////////////////////////////////////////////////////////////////////////////

fun NotificationManager.notifyDownloadCompleted(context: Context, id: Long, intent: Intent) {

	val title = intent.getStringExtra(EXTRA_DOWNLOAD_TITLE)
	val msg = if(intent.getBooleanExtra(EXTRA_DOWNLOAD_STATUS, false)) {
		context.getString(R.string.notification_description_succeed, title)
	}else {
		context.getString(R.string.notification_description_fail, title)
	}

	val detailActivityIntent = PendingIntent.getActivity(
		context, id.toInt(), intent, PendingIntent.FLAG_IMMUTABLE or 0
	)

	val builder = NotificationCompat.Builder(
		context, context.getString(R.string.notification_channel_id)
	)
		.setSmallIcon(R.drawable.ic_assistant_black_24dp)
		.setContentTitle(context.getString(R.string.notification_title))
		.setContentText(msg)
		.addAction(
			R.drawable.ic_assistant_black_24dp,
			context.getString(R.string.notification_button),
			detailActivityIntent
		)
		.setPriority(NotificationCompat.PRIORITY_HIGH)
		.setAutoCancel(true)

	notify(id.toInt(), builder.build())
}

fun Intent.asDetailActivityIntent(context: Context, downloadID: Long): Intent {
	val detailActivityIntent = Intent(context, DetailActivity::class.java)

	val id = getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
	if (id == downloadID) {
		val cursor = context.getSystemService(DownloadManager::class.java)
			.query(DownloadManager.Query().setFilterById(id))
		if (cursor.moveToFirst() && cursor.count > 0) {

			detailActivityIntent.putExtra(EXTRA_DOWNLOAD_ID, id)

			detailActivityIntent.putExtra(EXTRA_DOWNLOAD_TITLE,
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE))
			)
			detailActivityIntent.putExtra(EXTRA_DOWNLOAD_DESCRIPTION,
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION))
			)
			detailActivityIntent.putExtra(EXTRA_DOWNLOAD_STATUS,
				DownloadManager.STATUS_SUCCESSFUL ==
						cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
			)
			detailActivityIntent.putExtra(EXTRA_DOWNLOAD_REASON,
				cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
			)
			detailActivityIntent.putExtra(EXTRA_DOWNLOAD_URI,
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
			)
			detailActivityIntent.putExtra(EXTRA_DOWNLOAD_SIZE,
				cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
			)
		}
		cursor.close()
	}
	return detailActivityIntent
}
