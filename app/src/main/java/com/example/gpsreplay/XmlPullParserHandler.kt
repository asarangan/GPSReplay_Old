package com.example.gpsreplay

import android.content.Context
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class XmlPullParserHandler (context: Context){
    private val trackpoints = ArrayList<Trackpoint>()
    private var trackpoint : Trackpoint? = null
    private var text : String? = null
    private var tagname : String? = null
    private var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX",
        Locale.US)
    private var inside_trk:Boolean = false


   fun parse (inputStream: InputStream?) : ArrayList<Trackpoint>{
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
                       inside_trk = true
                       trackpoint = Trackpoint()
                       trackpoint!!.lat = parser.getAttributeValue(null,"lat").toDouble()
                       trackpoint!!.lon = parser.getAttributeValue(null,"lon").toDouble()
                   }
                   XmlPullParser.TEXT -> text = parser.text
                   XmlPullParser.END_TAG -> when {
                           tagname.equals("trkpt", ignoreCase = true) -> {trackpoints.add(trackpoint!!); inside_trk = false}
                           tagname.equals("ele", ignoreCase = true) -> trackpoint!!.altitude = text!!.toDouble()
                           tagname.equals("speed", ignoreCase = true) -> trackpoint!!.speed = text!!.toDouble()
                           tagname.equals("time",ignoreCase = true) -> if (inside_trk){ trackpoint!!.epoch = simpleDateFormat.parse(text).time }
                       }
               }
               eventType = parser.next()
           }
       }

       catch (e: XmlPullParserException){
           e.printStackTrace()
          //Toast.makeText(context, "Message copied", Toast.LENGTH_LONG).show()

       }
       catch (e:IOException){
           e.printStackTrace()
       }

   return trackpoints}
}