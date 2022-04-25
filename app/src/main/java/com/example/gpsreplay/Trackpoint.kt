package com.example.gpsreplay

import java.util.*


class Trackpoint {
    var id : Int = 0
    var epoch : Long = 0
    var lat : Double = 0.0
    var lon : Double = 0.0
    var speed : Double = 0.0
    var altitude : Double = 0.0

    fun toKts():Double{
        return (speed*19.4384).toInt()/10.0
    }

    fun toft():Double{
        return (altitude*32.8084).toInt()/10.0
    }

}