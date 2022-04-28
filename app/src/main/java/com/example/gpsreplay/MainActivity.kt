package com.example.gpsreplay

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
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
        val red: Int = Color.rgb(200, 0, 0)
        val green: Int = Color.rgb(0, 200, 0)
        var gpxButton: Button? = null
        var seekBar: SeekBar? = null
        var startTime: TextView? = null
        var endTime: TextView? = null
        var duration: TextView? = null
        var dataPointTime: TextView? = null
        var dataPointIndex: TextView? = null
        var index: Int = 0
        var latitude: TextView? = null
        var longitude: TextView? = null
        var altitude: TextView? = null
        var speed: TextView? = null
        var playPauseButton: Button? = null
        var numOfPoints: Int = 0
        var trackpoints: List<Trackpoint>? = null
        var systemTimeAtPlaystart: Long = System.currentTimeMillis()
        var deltaTime: Long = 0
        var play: Boolean = false

        gpxButton = findViewById<Button>(R.id.gpx_button)
        seekBar = findViewById<SeekBar>(R.id.seekBar)
        startTime = findViewById<TextView>(R.id.startTime)
        startTime.text = 0.toString()
        endTime = findViewById<TextView>(R.id.endTime)
        endTime.text = 0.toString()
        duration = findViewById<TextView>(R.id.duration)
        dataPointTime = findViewById<TextView>(R.id.dataPointTime)
        dataPointIndex = findViewById<TextView>(R.id.dataPointIndex)
        latitude = findViewById<TextView>(R.id.latitude)
        longitude = findViewById<TextView>(R.id.longitude)
        altitude = findViewById<TextView>(R.id.altitude)
        speed = findViewById<TextView>(R.id.speed)
        playPauseButton = findViewById<Button>(R.id.playPause)
        playPauseButton.setBackgroundColor(red)

        fun updateDatafields() {
            dataPointTime.text = Date(trackpoints!![index].epoch).toString()
            dataPointIndex.text = index.toString()
            latitude.text = trackpoints!![index].lat.toString()
            longitude.text = trackpoints!![index].lon.toString()
            altitude.text = trackpoints!![index].altitude.toFt().toString()
            speed.text = trackpoints!![index].speed.toKts().toString()
        }

        fun pause() {
            playPauseButton.text = "Paused"
            playPauseButton.setBackgroundColor(red)
            play = false
        }

        fun play() {
            playPauseButton.text = "Playing"
            playPauseButton.setBackgroundColor(green)
            play = true
        }

        //This is for reading the external file
        val getContent = ActivityResultContracts.GetContent()
        var callBack = ActivityResultCallback<Uri> {
            pause()
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
                    0 -> {          //0 means the file was read successfully
                        val toast = Toast.makeText(
                            this@MainActivity,
                            "Read $numOfPoints points",
                            Toast.LENGTH_LONG
                        )
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()
                        val startDate: Date = Date(trackpoints!![0].epoch)
                        val endDate: Date = Date(trackpoints!![numOfPoints - 1].epoch)
                        startTime?.text = startDate.toString()
                        endTime?.text = endDate.toString()
                        dataPointTime?.text = startDate.toString()
                        dataPointIndex?.text = 0.toString()
                        val millis: Long = endDate!!.time - startDate!!.time
                        val hours: Int = (millis / (1000 * 60 * 60)).toInt()
                        val mins: Int = (millis / (1000 * 60) % 60).toInt()
                        val secs: Int =
                            ((millis - (hours * 3600 + mins * 60) * 1000) / 1000).toInt()
                        duration?.text =
                            hours.toString() + " Hrs " + mins.toString() + " Mins " + secs.toString() + " secs"
                        index = 0
                        updateDatafields()
                    }
                    1 -> {  //1 means there was some error in the file
                        val toast = Toast.makeText(this@MainActivity, "Invalid File", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()
                        numOfPoints = 0
                        startTime?.text = 0.toString()
                        endTime?.text = 0.toString()
                        dataPointTime?.text = 0.toString()
                        dataPointIndex?.text = 0.toString()
                        latitude.text = 0.toString()
                        longitude.text = 0.toString()
                        altitude.text = 0.toString()
                        speed.text = 0.toString()
                        duration?.text = 0.toString()
                    }
                }
                seekBar.setProgress(0)
                index = 0
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        //Listener for the GPX file opener
        val getContentActivity = registerForActivityResult(getContent, callBack)
        gpxButton?.setOnClickListener { getContentActivity.launch("*/*") }

        //Listener for seekbar change
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2){    //If the seekbar change was caused by screen input (instead of by code)
                    pause()     //put the system on pause
                    if (numOfPoints > 0) {
                        index = (p1 * (numOfPoints - 1) / 50).toInt()
                        updateDatafields()
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //TODO("Not yet implemented")
            }
        })

        playPauseButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (play) {
                    pause()
                } else {
                    if (numOfPoints > 0) {
                        deltaTime =
                            System.currentTimeMillis() - Date(trackpoints!![index].epoch).time
                        play()
                    }
                }
            }
        })


        Thread(Runnable {
            while (true) {
                if (play && (index == numOfPoints - 1)) {
                    runOnUiThread() {
                        pause()
                    }
                }
                if (play && (numOfPoints > 0) && (index < numOfPoints - 1)) {
                    while (play && (Date(trackpoints!![index + 1].epoch).time + deltaTime > System.currentTimeMillis())) {
                    }
                    if (play) {//We need to check play again because it might have changed during the above idle loop
                        index += 1
                        runOnUiThread() {
                            updateDatafields()
                            if ((index*50.0/numOfPoints).toInt() > ((index-1)*50.0/numOfPoints).toInt()) {
                                //Log.d("SEEK",index.toString())
                                seekBar.setProgress((index * 50.0 / numOfPoints).toInt())
                            }
                        }
                    }
                }
            }
        }).start()

        fun isMockLocationEnabled():Boolean
        {
            var isMockLocation: Boolean
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AppOpsManager opsManager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
                    isMockLocation = (Objects.requireNonNull(opsManager).checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
                } else {
                    isMockLocation = !android.provider.Settings.Secure.getString(mContext.getContentResolver(), "mock_location").equals("0");
                }
            } catch (Exception e) {
                return false;
            }
            return isMockLocation;
        }

        if (!isMockLocationEnabled) {
            startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
        }

    }

}