package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    }

    private var downloadID: Long = 0

    private lateinit var appNotificationsManager: AppNotificationsManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        appNotificationsManager = AppNotificationsManager(this)

        //register receiver to get notified when DownloadManager finishes download
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        binding.content.customButton.setOnClickListener {
            if (binding.content.radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please select the file to download", Toast.LENGTH_LONG).show()
            } else {
                download()
                binding.content.customButton.buttonState = LoadingButton.ButtonState.LOADING
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            // Checking if downloaded file is the file we requested
            if (id == downloadID) {

                val selectedCategoryName =
                    findViewById<RadioButton>(binding.content.radioGroup.checkedRadioButtonId).text.toString()
                val downloadSuccessful = getDownloadStatus(downloadID)

                appNotificationsManager.showNotification(
                    selectedCategoryName,
                    downloadSuccessful
                )

                binding.content.customButton.buttonState = LoadingButton.ButtonState.NORMAL
            }
        }
    }

    // Returns if requested file was successfully downloaded or failed
    private fun getDownloadStatus(downloadRequestId: Long): Boolean {
        val query = DownloadManager.Query()
        query.setFilterById(downloadRequestId)

        val cursor: Cursor =
            (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).query(query)

        var status = 0

        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        }
        cursor.close()

        return status == DownloadManager.STATUS_SUCCESSFUL
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // enqueue puts the download request in the queue.
        downloadID = downloadManager.enqueue(request)
    }
}
