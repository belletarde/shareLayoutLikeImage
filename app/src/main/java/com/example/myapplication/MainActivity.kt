package com.example.myapplication


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bgShareLayout.setBackgroundColor(Color.parseColor("#FF7769"))
        bgShareLayout.isDrawingCacheEnabled = true

        bgShareLayout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        bgShareLayout.layout(0, 0, bgShareLayout.measuredWidth, bgShareLayout.measuredHeight)

        bgShareLayout.buildDrawingCache(true)
        val b = Bitmap.createBitmap(bgShareLayout.drawingCache)
        bgShareLayout.isDrawingCacheEnabled = false // clear drawing cache

        bgShareLayout.background = resources.getDrawable(R.drawable.background_rounded_fasting_done_chart)
        clickBTN.setOnClickListener { isExternalStorageWritable(b)}
    }

    private fun isExternalStorageWritable(b: Bitmap): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            try {
                val imagesFolder = File(cacheDir, "images")
                val uri: Uri?
                imagesFolder.mkdirs()
                val file = File(imagesFolder, "shared_image.png")

                val stream = FileOutputStream(file)
                b.compress(Bitmap.CompressFormat.PNG, 90, stream)
                stream.flush()
                stream.close()
                uri = FileProvider.getUriForFile(this, "com.mydomain.fileprovider", file)

                share(uri)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return true
        }
        return false
    }

    private fun share(uri: Uri?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }
}
