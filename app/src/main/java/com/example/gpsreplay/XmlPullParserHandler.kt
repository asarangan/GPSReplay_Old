package com.example.gpsreplay

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat


class XmlPullParserHandler {
    private val trackpoints = ArrayList<Trackpoint>()
    private var trackpoint : Trackpoint? = null
    private var text : String? = null
    private var tagname : String? = null
    private var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")


   fun parse (inputStream: InputStream?) : List<Trackpoint>{

       //val inputAsString = inputStream?.bufferedReader().use { it?.readText() }
       //Log.d("MainActivity",inputAsString.toString())

       try{
           val factory = XmlPullParserFactory.newInstance()
           factory.isNamespaceAware = true
           val parser = factory.newPullParser()
           parser.setInput(inputStream,null)
           var eventType = parser.eventType
           while (eventType != XmlPullParser.END_DOCUMENT){
               tagname = parser.name
               when (eventType){
                   XmlPullParser.START_TAG -> if (tagname.equals("trkpt",ignoreCase = true)){
                       trackpoint = Trackpoint()
                       trackpoint!!.lat = parser.getAttributeValue(null,"lat").toDouble()
                       trackpoint!!.lon = parser.getAttributeValue(null,"lon").toDouble()
                       //Log.d("Parse",trackpoint!!.lon.toString())
                   }
                   XmlPullParser.TEXT -> text = parser.text
                   XmlPullParser.END_TAG -> when {
                           tagname.equals("trkpt", ignoreCase = true) -> trackpoints.add(trackpoint!!)
                           tagname.equals("ele", ignoreCase = true) -> trackpoint!!.altitude = text!!.toDouble()
                           tagname.equals("speed", ignoreCase = true) -> trackpoint!!.speed = text!!.toDouble()
                           tagname.equals("time",ignoreCase = true) -> if (trackpoint != null){
                                                                    trackpoint!!.epoch = simpleDateFormat.parse(text)
                           }
                       }
               }
               eventType = parser.next()
           }
       }

       catch (e: XmlPullParserException){
           e.printStackTrace()
       }
       catch (e:IOException){
           e.printStackTrace()
       }

   return trackpoints}
}