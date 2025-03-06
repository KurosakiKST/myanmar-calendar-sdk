package com.ryan.myanmarcalendar.data.model

import com.ryan.myanmarcalendar.api.config.CalendarConfig
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

    fun toJulian(calendarType: CalendarType = CalendarConfig.getInstance().calendarType, sg: Double = 2361222.0): Double {
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

    fun toMyanmarDate(): MyanmarDate {
        return MyanmarDate.of(toJulian())
    }

    override fun toString(): String {
        return "WesternDate [year=$year, month=$month, day=$day, hour=$hour, minute=$minute, second=$second]"
    }

    companion object {
        fun of(myanmarDate: MyanmarDate): WesternDate {
            return of(myanmarDate.toJulian())
        }

        fun of(julianDate: Double, calendarType: CalendarType = CalendarConfig.getInstance().calendarType): WesternDate {
            return of(julianDate, calendarType, 2361222.0)
        }

        fun of(julianDate: Double, calendarType: CalendarType, sg: Double): WesternDate {
            return WesternDateKernel.julianToWestern(julianDate, calendarType.number, sg)
        }

        // For compatibility with existing code
        fun fromJulian(
            julianDate: Double,
            calendarType: CalendarType = CalendarConfig.getInstance().calendarType,
            sg: Double = 2361222.0
        ): WesternDate = of(julianDate, calendarType, sg)

        fun fromMyanmarDate(myanmarDate: MyanmarDate): WesternDate = of(myanmarDate)
    }
}