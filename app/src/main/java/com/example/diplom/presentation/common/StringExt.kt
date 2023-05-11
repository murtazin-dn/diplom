package com.example.diplom.presentation.common

import android.text.Editable
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val MAIL_REGEX = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")

fun String.isEmailValid(): Boolean = this.isNotBlank() && Regex(MAIL_REGEX).matches(this)

fun String.isNameValid() = matches("^([А-Я]{1}[а-яё]{1,50}|[A-Z]{1}[a-z]{1,50})\$+".toRegex())

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun String.toEpochSeconds(): Long {
    val localDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
}