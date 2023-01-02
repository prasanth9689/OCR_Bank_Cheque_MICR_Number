package com.skyblue.ocr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import com.skyblue.ocr.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val TESS_DATA = "/tessdata"
    private val DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess"
    private val textView: TextView? = null
    private var tessBaseAPI: TessBaseAPI? = null
    private val outputFileDir: Uri? = null
    private val mCurrentPhotoPath: String? = null
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.cheque_ocr_1)

        prepareTessData()

        val result = this.getText(bitmap = BitmapFactory.decodeResource(resources, R.drawable.cheque_ocr_1))

        val strNew = result!!.replace("([a-z])".toRegex(), "")
        val scanner = Scanner(strNew)
        val firstStrChequeNo = scanner.next()
        val secondStrRoutingNo = scanner.next()
        val secondStrRoutingNoRemLastTwoNo =
            secondStrRoutingNo.substring(0, secondStrRoutingNo.length - 2)

        val thirdStrShortAcNo = scanner.next()
        val fourthStrTCNo = scanner.next()

        binding.edTextChequeNo.setText(firstStrChequeNo)
        binding.edTextRouting.setText(secondStrRoutingNoRemLastTwoNo)
        binding.edTextShortAcNo.setText(thirdStrShortAcNo)
        binding.edTexttC.setText(fourthStrTCNo)

        binding.textView.setText(result)
    }

    private fun prepareTessData() {
        try {
            val dir = getExternalFilesDir(TESS_DATA)
            if (!dir!!.exists()) {
                if (!dir!!.mkdir()) {
                    Toast.makeText(
                        applicationContext,
                        "The folder " + dir!!.path + "was not created",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            val fileList = assets.list("")
            for (fileName in fileList!!) {
                val pathToDataFile = "$dir/$fileName"
                if (!File(pathToDataFile).exists()) {
                    val `in` = assets.open(fileName)
                    val out: OutputStream = FileOutputStream(pathToDataFile)
                    val buff = ByteArray(1024)
                    var len: Int
                    while (`in`.read(buff).also { len = it } > 0) {
                        out.write(buff, 0, len)
                    }
                    `in`.close()
                    out.close()
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", e.message!!)
        }
    }

    private fun getText(bitmap: Bitmap): String? {
        try {
            tessBaseAPI = TessBaseAPI()
        } catch (e: java.lang.Exception) {
            Log.e("TAG", e.message!!)
        }
        var dataPath: String? = null
        dataPath = getExternalFilesDir("/")!!.path + "/"
        tessBaseAPI!!.init(dataPath, "eng")
        tessBaseAPI!!.setImage(bitmap)
        var retStr: String? = "No result"
        try {
            retStr = tessBaseAPI!!.utF8Text
        } catch (e: java.lang.Exception) {
            Log.e("TAG", e.message!!)
        }
        tessBaseAPI!!.end()
        return retStr
    }

}