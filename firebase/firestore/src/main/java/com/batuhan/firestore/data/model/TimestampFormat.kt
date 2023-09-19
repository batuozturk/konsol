package com.batuhan.firestore.data.model

import androidx.annotation.Keep
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Keep
data class TimestampFormat(
    val nanoSecond: String = "0",
    val second: String = "0",
    val minute: String = "0",
    val hour: String = "0",
    val day: String = "1",
    val month: String = "1",
    val year: String = "1970"
)

fun TimestampFormat.toFormattedTimestamp(): String {
    val instant = Instant.from(
        LocalDate.of(year.toInt(), month.toInt(), day.toInt())
            .atTime(hour.toInt(), minute.toInt(), second.toInt(), nanoSecond.toInt())
            .atZone(ZoneId.systemDefault())
    )
    return instant.toString()
}

fun String.toTimestampFormat(): TimestampFormat {
    val instant = Instant.parse(this)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    return TimestampFormat(
        nanoSecond = localDateTime.nano.toString(),
        second = localDateTime.second.toString(),
        minute = localDateTime.minute.toString(),
        hour = localDateTime.hour.toString(),
        day = localDateTime.dayOfMonth.toString(),
        month = localDateTime.monthValue.toString(),
        year = localDateTime.year.toString()
    )
}
