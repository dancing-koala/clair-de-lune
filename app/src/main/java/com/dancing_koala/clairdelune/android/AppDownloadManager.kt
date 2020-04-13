package com.dancing_koala.clairdelune.android

import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.dancing_koala.clairdelune.R
import java.io.File

class AppDownloadManager(private val appContext: Context) {

    private var downloadReference = 0L
    private lateinit var downloadManager: DownloadManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action ?: return

            val action = intent.action

            if (action == ACTION_DOWNLOAD_COMPLETE) {
                val downloadId = intent.getLongExtra(EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != downloadReference) {
                    context?.unregisterReceiver(this)
                    return
                }
                val query = Query().apply {
                    setFilterById(downloadId)
                }

                downloadManager.query(query)?.let { cursor ->
                    if (cursor.moveToFirst()) {
                        val status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))
                        if (status == STATUS_SUCCESSFUL) {
                            var localFile = cursor.getString(cursor.getColumnIndex(COLUMN_LOCAL_URI))
                            if (localFile.startsWith("file:///")) {
                                localFile = localFile.removePrefix("file:///").substringBefore(File.separator)
                            }

                            Toast.makeText(context, localFile, Toast.LENGTH_LONG).show()

                        } else if (status == STATUS_FAILED) {
                            val reason = cursor.getString(cursor.getColumnIndex(COLUMN_REASON))
                            val message = "Erreur: $reason"
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                    cursor.close()
                }

                context?.unregisterReceiver(this)
            }
        }
    }

    fun downloadFile(url: String, fileName: String) {
        val request = Request(Uri.parse(url)).apply {
            setAllowedNetworkTypes(Request.NETWORK_MOBILE or Request.NETWORK_WIFI)
            setTitle(fileName)
            setDescription(appContext.getString(R.string.download_picture_description))
            setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        appContext.registerReceiver(receiver, IntentFilter(ACTION_DOWNLOAD_COMPLETE))
        downloadManager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}
