package com.paw.key.presentation.ui.mypage.route.courseinfo.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatDateTime(input: String): String {
    val dateTime = try {
        LocalDateTime.parse(input, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) {
        LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
    }

    val date = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

    val hour = dateTime.hour
    val minute = dateTime.minute

    val amPm = if (hour < 12) "오전" else "오후"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return "%s | %s %02d:%02d".format(date, amPm, hour12, minute)
}