package com.noom.interview.fullstack.sleep.utils

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateUtils {
    companion object {
        fun formatTimeTo12HourPeriod(time: LocalTime?): String? {
            val formatter = getFormatter("h:mm a")
            val timeFormated = time?.format(formatter)

            return timeFormated
        }

        fun formatTimePassed(totalTimeInMinutes: Long) : String {
            val hours = totalTimeInMinutes / 60
            val minutes = totalTimeInMinutes % 60
            val hoursText = if(hours > 0) "$hours h" else ""
            val minutesText = if(minutes > 0) "$minutes min" else ""

            if (hours > 0 && minutes > 0){
                return "$hoursText $minutesText"
            }

            return hoursText + minutesText
        }

        fun formatDateWithOrdinal(date: LocalDate?): String? {
            if (date == null) return null

            val day = date.dayOfMonth
            val formatter = getFormatter("MMMM")
            val month = date.format(formatter)

            val ordinalSuffix = getOrdinalSuffix(day)

            return "$month, $day$ordinalSuffix"
        }

        fun getDurationBetweenTimes(timeInBedStart: LocalDateTime?, timeInBedEnd: LocalDateTime?): Duration {
            return Duration.between(timeInBedStart, timeInBedEnd)
        }

        private fun getFormatter(pattern : String) : DateTimeFormatter {
            return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        }

        private fun getOrdinalSuffix(day: Int): String {
            return when {
                day in 11..13 -> "th"
                day % 10 == 1 -> "st"
                day % 10 == 2 -> "nd"
                day % 10 == 3 -> "rd"
                else -> "th"
            }
        }
    }
}