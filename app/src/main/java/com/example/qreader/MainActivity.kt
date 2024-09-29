package com.example.qreader

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.qreader.R

class MainActivity : AppCompatActivity() {
    private lateinit var btnQrScan: Button
    private lateinit var btnQrCreate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnQrScan = findViewById(R.id.btnQrScan)
        btnQrCreate = findViewById(R.id.btnQrCreate)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        btnQrScan.setOnClickListener {
            val intent = Intent(this, QrScanActivity::class.java)
            startActivity(intent)
        }

        btnQrCreate.setOnClickListener {
            val intent = Intent(this, QrCreateActivity::class.java)
            startActivity(intent)
        }
    }
}