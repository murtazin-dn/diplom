package com.example.diplom.presentation.common

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun Long.toDateString(): String{
    val date = Date(this)
    val myFormat = "dd.MM.yyyy" // mention the format you need
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    sdf.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
    return sdf.format(date)
//    val instant = Instant.ofEpochSecond(this)
//    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
//        .withZone(ZoneId.systemDefault())
//    return formatter.format(instant)
}
fun Long.toTimeString(): String{
    val date = Date(this)
    val myFormat = "HH:mm" // mention the format you need
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    sdf.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
    return sdf.format(date)
//    val instant = Instant.ofEpochSecond(this)
//    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
//        .withZone(ZoneId.systemDefault())
//    return formatter.format(instant)
}