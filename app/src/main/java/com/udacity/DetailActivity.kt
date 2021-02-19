package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    // Parameters received by DetailsActivity from Intent
    companion object {
        const val INTENT_EXTRA_CATEGORY_NAME = "INTENT_EXTRA_CATEGORY_NAME"
        const val INTENT_EXTRA_DOWNLOAD_STATUS = "INTENT_EXTRA_DOWNLOAD_STATUS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val fileName = intent.getStringExtra(INTENT_EXTRA_CATEGORY_NAME)
        val downloadStatus = intent.getBooleanExtra(INTENT_EXTRA_DOWNLOAD_STATUS, false)

        binding.content.categoryName.text = fileName

        when {
            downloadStatus -> {
                binding.content.status.text = getString(R.string.result_success)
                binding.content.status.setTextColor(getColor(R.color.colorPrimaryDark))
            }
            else -> {
                binding.content.status.text = getString(R.string.result_failure)
                binding.content.status.setTextColor(getColor(R.color.colorRed))
            }
        }

        binding.content.button.setOnClickListener {
            val intent = Intent(this@DetailActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
