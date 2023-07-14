package com.shino72.d

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.shino72.d.databinding.ActivitySettingBinding
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import java.io.IOException

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    companion object{
        private const val PICK_IMAGE_REQUEST = 1
        private var uri : Uri? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            addBtn.setOnClickListener {
                openGallery()
            }
            makeBtn.setOnClickListener {
                val quote = Quote(nameEt.text.toString(), dateEt.text.toString(), quoteEt.text.toString())
                val intent = Intent(this@SettingActivity, MainActivity::class.java)
                intent.putExtra("quote", quote)
                intent.putExtra("uri",uri)
                startActivity(intent)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            PICK_IMAGE_REQUEST -> {
                uri = data?.data
                Glide.with(this@SettingActivity).load(uri).into(binding.photoIv)
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}