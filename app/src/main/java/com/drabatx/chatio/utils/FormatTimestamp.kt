package com.drabatx.chatio.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

fun FormatTimestamp(timestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - timestamp

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp

    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} min ago"
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            if (isSameDay(calendar, today)) "Today at ${formatter.format(calendar.time)}"
            else "Yesterday at ${formatter.format(calendar.time)}"
        }

        isSameYear(calendar, today) -> {
            val formatter = SimpleDateFormat("d MMMM 'at' HH:mm", Locale.getDefault())
            formatter.format(calendar.time)
        }

        else -> {
            val formatter = SimpleDateFormat("d MMMM yyyy 'at' HH:mm", Locale.getDefault())
            formatter.format(calendar.time)
        }
    }
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
}