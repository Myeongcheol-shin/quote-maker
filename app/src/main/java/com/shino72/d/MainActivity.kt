package com.shino72.d

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.shino72.d.databinding.ActivityMainBinding
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    companion object{
        private const val PICK_IMAGE_REQUEST = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            ivBtn.setOnClickListener {
                openGallery()
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

                val returnUri = data?.data
                if(returnUri != null) {
                    val bitmap = uriToBitmap(returnUri)
                    BackgroundRemover.bitmapForProcessing(
                        bitmap!!,
                        true,
                        object: OnBackgroundChangeListener {
                            override fun onSuccess(bitmap: Bitmap) {
                                //do what ever you want to do with this bitmap
                                binding.iv.apply {
                                    Glide.with(this@MainActivity).load(bitmap).into(this)
                                }
                            }

                            override fun onFailed(exception: Exception) {
                                //exception
                            }
                        }
                    )
                }
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