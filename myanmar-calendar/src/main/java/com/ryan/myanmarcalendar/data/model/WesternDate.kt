package com.ryan.myanmarcalendar.data.model

import com.ryan.myanmarcalendar.core.kernel.WesternDateKernel
import java.io.Serializable

data class WesternDate(
    val year: Int,
    val month: Int,  // 1-based [Jan=1, ..., Dec=12]
    val day: Int,
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0
) : Serializable {

    fun toJulian(calendarType: CalendarType = CalendarType.ENGLISH, sg: Double = 2361222.0): Double {
        return WesternDateKernel.westernToJulian(
            year = year,
            month = month,
            day = day,
            hour = hour,
            minute = minute,
            second = second,
            calendarType = calendarType,
            sg = sg
        )
    }

    companion object {
        fun fromJulian(
            julianDate: Double,
            calendarType: CalendarType = CalendarType.ENGLISH,
            sg: Double = 2361222.0
        ): WesternDate = WesternDateKernel.julianToWestern(julianDate, calendarType.number, sg)

        fun fromMyanmarDate(myanmarDate: MyanmarDate): WesternDate =
            fromJulian(myanmarDate.toJulian())
    }
}