package com.example.diplom.presentation.common

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.TemporalUnit
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

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

fun Long.toTimePassed(): String{
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy") // mention the format you need
    val loc = Locale.forLanguageTag("ru")


    val msgTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    val nowTime = LocalDateTime.now()
    println(msgTime)
    println(nowTime)
    return when{
        ChronoUnit.YEARS.between(msgTime, nowTime) > 0L  -> msgTime.format(dateFormat)
        ChronoUnit.DAYS.between(msgTime, nowTime) > 1L && ChronoUnit.WEEKS.between(msgTime, nowTime) != 0L ->
            "${msgTime.dayOfMonth} ${msgTime.month.getDisplayName(TextStyle.FULL, loc)}"
        msgTime.isBefore(nowTime.toLocalDate().atTime(LocalTime.MIDNIGHT)) -> "вчера"
        ChronoUnit.WEEKS.between(msgTime, nowTime) == 0L && ChronoUnit.DAYS.between(msgTime, nowTime) != 0L ->
            msgTime.dayOfWeek.getDisplayName(TextStyle.FULL, loc)
        else -> msgTime.format(timeFormat)
    }
//    val msgTomeInstant = Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault())
//    val msgTome = msgTomeInstant.second
//    val now = System.currentTimeMillis() / 1000
//    val timePassed = msgTome - now
//    return when{
//        timePassed < 60 -> ""
//        timePassed < 60 * 60 -> "${timePassed/60}м"
//        msgTomeInstant.hour < 23 -> "${msgTomeInstant.hour/60}ч"
//        msgTomeInstant.dayOfMonth < 31
//    }
}