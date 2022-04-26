package com.example.gpsreplay

import android.app.PendingIntent.getActivity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var gpxButton: Button? = null
        var seekBar: SeekBar? = null
        var startTime: TextView? = null
        var endTime: TextView? = null
        var duration: TextView? = null
        var currentTime: TextView? = null
        var latitude: TextView? = null
        var longitude: TextView? = null
        var altitude: TextView? = null
        var speed: TextView? = null
        var startDate: Date? = null
        var endDate: Date? = null
        var playPauseButton: Button? = null
        var numOfPoints: Int = 0
        var trackpoints: List<Trackpoint>? = null
        var sysTimeAtStart: Long = System.currentTimeMillis()
        var play: Boolean = false

        gpxButton = findViewById<Button>(R.id.gpx_button)
        seekBar = findViewById<SeekBar>(R.id.seekBar)
        startTime = findViewById<TextView>(R.id.start_time)
        startTime.text = 0.toString()
        endTime = findViewById<TextView>(R.id.end_time)
        endTime.text = 0.toString()
        duration = findViewById<TextView>(R.id.duration)
        currentTime = findViewById<TextView>(R.id.current_time)
        latitude = findViewById<TextView>(R.id.latitude)
        longitude = findViewById<TextView>(R.id.longitude)
        altitude = findViewById<TextView>(R.id.altitude)
        speed = findViewById<TextView>(R.id.speed)
        playPauseButton = findViewById<Button>(R.id.playPause)

        val getContent = ActivityResultContracts.GetContent()
        var callBack = ActivityResultCallback<Uri> {
            val inputStream: InputStream? = this.contentResolver.openInputStream(it)
            //val inputReader: inputStream.bufferedReader().use
            //val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
            //Log.d("MainActivity",inputAsString.toString())
            try {
                val parser = XmlPullParserHandler()
                val pairReturn: Pair<List<Trackpoint>, Int>
                pairReturn = parser.parse(inputStream)
                trackpoints = pairReturn.first
                val code: Int = pairReturn.second
                numOfPoints = trackpoints!!.size
                //bar?.max = trackpoints!!.size
                //min_txt?.text = 0.toString()
                when (code) {
                    0 -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Read $numOfPoints points",
                            Toast.LENGTH_LONG
                        ).show()
                        startDate = Date(trackpoints!![0].epoch)
                        endDate = Date(trackpoints!![numOfPoints - 1].epoch)
                        startTime?.text = startDate.toString()
                        endTime?.text = endDate.toString()
                        currentTime?.text = Date(trackpoints!![0].epoch).toString()
                        val millis:Long  = endDate!!.time - startDate!!.time
                        val hours:Int  = (millis / (1000 * 60 * 60)).toInt()
                        val mins:Int = (millis / (1000 * 60) % 60).toInt()
                        val secs:Int = ((millis - (hours * 3600 + mins * 60) * 1000) / 1000).toInt()
                        duration?.text =
                            hours.toString() + " Hrs " + mins.toString() + " Mins " + secs.toString() + " secs"
                    }
                    1 -> {
                        Toast.makeText(this@MainActivity, "Invalid File", Toast.LENGTH_LONG).show()
                        startTime?.text = "0"
                        endTime?.text = "0"
                        currentTime?.text = 0.toString()
                        latitude.text = 0.toString()
                        longitude.text = 0.toString()
                        altitude.text = 0.toString()
                        speed.text = 0.toString()
                        duration?.text = 0.toString()
                    }
                }




                seekBar.setProgress(0)

//                Log.d("Parse",trackpoints!!.size.toString())
//                Log.d("Parse",trackpoints!![0].epoch.toString())
//                Log.d("Parse",trackpoints!![2].epoch?.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val getContentActivity = registerForActivityResult(getContent, callBack)


//            { uri: Uri ->
//            val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
//            val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
//           Log.d("TEST",inputAsString.toString())
//            }

        gpxButton?.setOnClickListener { getContentActivity.launch("*/*") }
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (numOfPoints > 0) {
                    val index: Int = (p1 * (numOfPoints - 1) / 50).toInt()
                    currentTime.text = Date(trackpoints!![index].epoch).toString()
                    latitude.text = trackpoints!![index].lat.toString()
                    longitude.text = trackpoints!![index].lon.toString()
                    altitude.text = trackpoints!![index].toft().toString()
                    speed.text = trackpoints!![index].toKts().toString()
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                play = false
                playPauseButton.text = "Paused"
                //sysTimeAtStart = System.currentTimeMillis()
                //Toast.makeText(this@MainActivity, "test", Toast.LENGTH_LONG).show()
            }
        })

        playPauseButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (play) {
                    playPauseButton.text = "Paused"
                    //playPauseButton.backgroundTintList()
                    play = false
                } else {
                    playPauseButton.text = "Playing"
                    play = true
                }
            }
        })

        //Toast.makeText(this@MainActivity, "This works", Toast.LENGTH_LONG).show()
        //Toast.makeText(MainActivity().applicationContext, "Message copied", Toast.LENGTH_LONG).show()

    }

}