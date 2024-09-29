package com.example.qreader

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class QrCreateActivity : AppCompatActivity() {
    private lateinit var editTextLink: EditText
    private lateinit var btnGenerateQR: Button
    private lateinit var imageViewQR: ImageView
    private lateinit var btnShare: Button
    private lateinit var btnSave: Button
    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_create)

        editTextLink = findViewById(R.id.editTextLink)
        btnGenerateQR = findViewById(R.id.btnGenerateQR)
        imageViewQR = findViewById(R.id.imageViewQR)
        btnShare = findViewById(R.id.btnShare)
        btnSave = findViewById(R.id.btnSave)

        btnGenerateQR.setOnClickListener {
            val link = editTextLink.text.toString()
            if (link.isNotEmpty()) {
                qrBitmap = generateQRCode(link)
                imageViewQR.setImageBitmap(qrBitmap)
            }
        }

        btnShare.setOnClickListener {
            shareQRCode()
        }

        btnSave.setOnClickListener {
            saveQRCode()
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500)
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun shareQRCode() {
        qrBitmap?.let { bitmap ->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "QR Code", null)
            val uri = Uri.parse(path)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "QR Kodu Paylaş"))
        } ?: Toast.makeText(this, "Önce QR kod oluşturun", Toast.LENGTH_SHORT).show()
    }

    private fun saveQRCode() {
        qrBitmap?.let { bitmap ->
            val filename = "QRCode_${System.currentTimeMillis()}.jpg"
            var fos: FileOutputStream? = null

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = uri?.let { contentResolver.openOutputStream(it) } as FileOutputStream?
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(this, "QR kod kaydedildi", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(this, "Önce QR kod oluşturun", Toast.LENGTH_SHORT).show()
    }
}