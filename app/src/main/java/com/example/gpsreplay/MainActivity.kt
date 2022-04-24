package com.example.gpsreplay

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TEST","start")

        var button: Button? = null
        var txtView: TextView? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var trackpoints: List<Trackpoint>? = null

        val getContent = ActivityResultContracts.GetContent()
        var callBack = ActivityResultCallback <Uri> {
            val inputStream: InputStream? = this.contentResolver.openInputStream(it)
            //val inputReader: inputStream.bufferedReader().use
            //val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
            //Log.d("MainActivity",inputAsString.toString())
            try {
                val parser = XmlPullParserHandler()
                trackpoints = parser.parse(inputStream)
                Log.d("Parse",trackpoints!!.size.toString())
                Log.d("Parse",trackpoints!!.first().epoch.toString())
            }
            catch (e: IOException){
                e.printStackTrace()
            }

            }

        val getContentActivity = registerForActivityResult(getContent,callBack)


//            { uri: Uri ->
//            val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
//            val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
//           Log.d("TEST",inputAsString.toString())
//            }

        button = findViewById<Button>(R.id.btn)
        txtView = findViewById<TextView>(R.id.textView)
        button?.setOnClickListener {getContentActivity.launch("*/*")}


    }

}