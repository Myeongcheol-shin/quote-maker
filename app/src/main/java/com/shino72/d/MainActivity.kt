package com.shino72.d

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.shino72.d.databinding.ActivityMainBinding
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_CAPTURE = 1001
    }

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val quote = intent.getSerializableExtra("quote") as Quote
        val uri = intent.getParcelableExtra<Uri>("uri")
        binding.apply {
            val quotes = "\"${quote.quote}\""
            val dates = "[${quote.date}]"
            nameTv.text = quote.name
            dateTv.text = dates
            quoteTv.text = quotes
            if(uri != null){
                val bitmap = uriToBitmap(uri)
                BackgroundRemover.bitmapForProcessing(
                    bitmap!!,
                    true,
                    object: OnBackgroundChangeListener {
                        override fun onSuccess(bitmap: Bitmap) {
                            //do what ever you want to do with this bitmap
                            binding.iv.apply {
                                Glide.with(this@MainActivity).load(bitmap).into(this)
                                val matrix = ColorMatrix()
                                matrix.setSaturation(0f)
                                colorFilter = ColorMatrixColorFilter(matrix)
                            }
                        }
                        override fun onFailed(exception: Exception) {
                            //exception
                        }
                    }
                )
            }

            floatingBtn.setOnClickListener {
                binding.fl.visibility = View.GONE
                val bitmap = getScreenShotFromView(binding.root)
                if (bitmap != null) {
                    saveMediaToStorage(bitmap)
                }
            }
        }
    }
    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "갤러리에 사진이 저장되었습니다." , Toast.LENGTH_SHORT).show()
            binding.fl.visibility = View.VISIBLE
        }
    }
    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return screenshot
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