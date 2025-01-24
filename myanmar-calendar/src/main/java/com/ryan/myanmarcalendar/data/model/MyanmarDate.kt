package com.ryan.myanmarcalendar.data.model

import com.ryan.myanmarcalendar.core.kernel.MyanmarDateKernel
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

data class MyanmarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val monthType: MonthType,
    val yearType: Int = 0, // 0=common, 1=little watat, 2=big watat
    val yearLength: Int = 0,
    val monthLength: Int = 0,
    val moonPhase: Int = 0, // 0=waxing, 1=full moon, 2=waning, 3=new moon
    val fortnightDay: Int = 0,
    val weekDay: Int = 0,
    private val julianDayNumber: Double = 0.0
) : Serializable {

    fun toJulian(): Double {
        return if (julianDayNumber != 0.0) {
            julianDayNumber
        } else {
            MyanmarDateKernel.myanmarDateToJulian(year, month, day).toDouble()
        }
    }

    fun toWesternDate(calendarType: CalendarType = CalendarType.ENGLISH): WesternDate {
        return WesternDate.fromJulian(toJulian(), calendarType)
    }

    companion object {
        fun fromJulian(julianDate: Double): MyanmarDate {
            return MyanmarDateKernel.julianToMyanmarDate(julianDate)
        }

        fun fromDate(date: Date): MyanmarDate {
            val localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            return fromLocalDateTime(localDateTime)
        }

        fun fromLocalDateTime(localDateTime: LocalDateTime): MyanmarDate {
            val westernDate = WesternDate(
                year = localDateTime.year,
                month = localDateTime.monthValue,
                day = localDateTime.dayOfMonth,
                hour = localDateTime.hour,
                minute = localDateTime.minute,
                second = localDateTime.second
            )
            return fromJulian(westernDate.toJulian())
        }
    }
}