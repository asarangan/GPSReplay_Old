package com.example.gpsreplay

import android.content.Context
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sin


class XmlPullParserHandler() {
    private val trackpoints = ArrayList<Trackpoint>()
    private var trackpoint: Trackpoint? = null
    private var text: String? = null
    private var tagname: String? = null
    private var simpleDateFormat: SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ssX",
        Locale.US
    )
    private var inside_trk: Boolean = false
    private var code: Int = 0


    fun parse(inputStream: InputStream?): Pair<ArrayList<Trackpoint>, Int> {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals("trkpt", ignoreCase = true)) {
                        inside_trk = true
                        trackpoint = Trackpoint()
                        trackpoint!!.lat = parser.getAttributeValue(null, "lat").toDouble()
                        trackpoint!!.lon = parser.getAttributeValue(null, "lon").toDouble()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> when {
                        tagname.equals("trkpt", ignoreCase = true) -> {
                            //sin(trackpoint!!.lon)
                            //   tc1=mod(atan2(sin(lon1-lon2)*cos(lat2),
                            //           cos(lat1)*sin(lat2)-sin(lat1)*cos(lat2)*cos(lon1-lon2)), 2*pi)
                            trackpoints.add(trackpoint!!)
                            inside_trk = false
                        }
                        tagname.equals("ele", ignoreCase = true) -> trackpoint!!.altitude =
                            text!!.toDouble()
                        tagname.equals("speed", ignoreCase = true) -> trackpoint!!.speed =
                            text!!.toFloat()
                        tagname.equals("time", ignoreCase = true) -> if (inside_trk) {
                            trackpoint!!.epoch = simpleDateFormat.parse(text).time
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            code = 1

        } catch (e: IOException) {
            e.printStackTrace()
            code = 2
        }

        return Pair(trackpoints, code)
    }
}