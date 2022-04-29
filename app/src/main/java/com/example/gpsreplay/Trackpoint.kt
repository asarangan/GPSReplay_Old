package com.example.gpsreplay


class Trackpoint {
    var id : Int = 0
    var epoch : Long = 0
    var lat : Double = 0.0
    var lon : Double = 0.0
    var speed : Float = 0.0F
    var altitude : Double = 0.0
    var bearing : Float = 0.0F
}



fun Double.toKts():Double
{
    return (this*19.4384).toInt()/10.0
}

fun Double.toFt():Double{
    return (this*32.8084).toInt()/10.0
}