package com.example.gpsreplay


class Trackpoint {
    var id : Int = 0
    var epoch : Double = 0.0
    var lat : Double = 0.0
    var lon : Double = 0.0
    var speed : Double = 0.0
    var altitude : Double = 0.0

    override fun toString():String{
        return("$lat $lon $speed")
    }
}