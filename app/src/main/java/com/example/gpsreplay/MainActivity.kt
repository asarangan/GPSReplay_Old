package com.example.gpsreplay

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import java.io.IOException
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var button: Button? = null
        var seekBar: SeekBar? = null
        var min_txt: TextView? = null
        var max_txt: TextView? = null
        var start_time: TextView? = null
        var end_time: TextView? = null
        var duration: TextView? = null
        var current_time: TextView? = null
        var latitude: TextView? = null
        var longitude: TextView? = null
        var altitude: TextView? = null
        var speed: TextView? = null
        var startDate : Date = Date()
        var endDate : Date = Date()
        var numOfPoints: Int = 0
        var trackpoints: List<Trackpoint>? = null

        button = findViewById<Button>(R.id.btn)
        seekBar = findViewById<SeekBar>(R.id.seekBar)
        min_txt = findViewById<TextView>(R.id.min_txt)
        max_txt = findViewById<TextView>(R.id.max_txt)
        start_time = findViewById<TextView>(R.id.start_time)
        start_time.text = startDate.toString()
        end_time = findViewById<TextView>(R.id.end_time)
        end_time.text = endDate.toString()
        duration = findViewById<TextView>(R.id.duration)
        current_time = findViewById<TextView>(R.id.current_time)
        latitude = findViewById<TextView>(R.id.latitude)
        longitude = findViewById<TextView>(R.id.longitude)
        altitude = findViewById<TextView>(R.id.altitude)
        speed = findViewById<TextView>(R.id.speed)


        val getContent = ActivityResultContracts.GetContent()
        var callBack = ActivityResultCallback <Uri> {
            val inputStream: InputStream? = this.contentResolver.openInputStream(it)
            //val inputReader: inputStream.bufferedReader().use
            //val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
            //Log.d("MainActivity",inputAsString.toString())
            try {
                val parser = XmlPullParserHandler()
                trackpoints = parser.parse(inputStream)
                //bar?.max = trackpoints!!.size
                //min_txt?.text = 0.toString()
                numOfPoints = trackpoints!!.size
                max_txt?.text = numOfPoints.toString()
                startDate = Date(trackpoints!![0].epoch)
                endDate = Date(trackpoints!![numOfPoints-1].epoch)
                start_time?.text = startDate.toString()
                end_time?.text = endDate.toString()
                val millis: Long = endDate.time - startDate.time
                val hours: Int = (millis / (1000 * 60 * 60)).toInt()
                val mins = (millis / (1000 * 60) % 60).toInt()
                val secs = (millis - (hours*3600+mins*60)*1000)/1000.toInt()
                duration?.text = hours.toString()+" Hrs "+mins.toString()+" Mins "+secs.toString()+" secs"
                current_time?.text = Date(trackpoints!![0].epoch).toString()

                Log.d("Parse",trackpoints!!.size.toString())
                Log.d("Parse",trackpoints!![0].epoch.toString())
                Log.d("Parse",trackpoints!![2].epoch?.toString())
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




        button?.setOnClickListener {getContentActivity.launch("*/*")}
        seekBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val index:Int  = (p1*(numOfPoints-1)/50).toInt()
                current_time.text = Date(trackpoints!![index].epoch).toString()
                latitude.text = trackpoints!![index].lat.toString()
                longitude.text = trackpoints!![index].lon.toString()
                altitude.text = trackpoints!![index].toft().toString()
                speed.text = trackpoints!![index].toKts().toString()

            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                //TODO("Not yet implemented")
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                //TODO("Not yet implemented")
            }
        })


    }

}