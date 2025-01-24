package com.ryan.myanmarcalendar.api

import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.core.kernel.WesternDateKernel
import com.ryan.myanmarcalendar.data.model.*
import com.ryan.myanmarcalendar.domain.model.CalendarResult
import com.ryan.myanmarcalendar.domain.model.DayInfo
import com.ryan.myanmarcalendar.domain.model.GregorianMonth
import com.ryan.myanmarcalendar.domain.model.MyanmarMonth
import java.time.ZoneId
import java.util.Date

class MyanmarCalendar private constructor(private val config: CalendarConfig) {

    fun getCurrentMonthCalendar(): CalendarResult {
        return getMonthCalendar(System.currentTimeMillis())
    }

    fun getMonthCalendar(timeMillis: Long): CalendarResult {
        val date = Date(timeMillis)
        val myanmarDate = convertToMyanmarDate(date)
        val westernDate = WesternDate.fromMyanmarDate(myanmarDate)
        val astro = Astro.of(myanmarDate)

        // Use existing repository implementation for CalendarResult
        // but with accurate date conversions
        return CalendarResult(
            gregorianMonth = getGregorianMonth(westernDate),
            myanmarMonth = getMyanmarMonth(myanmarDate),
            days = getDaysInMonth(timeMillis)
        )
    }

    fun convertToMyanmarDate(date: Date): MyanmarDate {
        val localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return MyanmarDate.fromLocalDateTime(localDateTime)
    }

    companion object {
        @Volatile private var instance: MyanmarCalendar? = null

        @JvmStatic
        fun getInstance(config: CalendarConfig = CalendarConfig.default()): MyanmarCalendar {
            return instance ?: synchronized(this) {
                instance ?: MyanmarCalendar(config).also { instance = it }
            }
        }
    }

    private fun getGregorianMonth(westernDate: WesternDate): GregorianMonth {
        val monthLength = WesternDateKernel.getLengthOfMonth(
            westernDate.year,
            westernDate.month,
            config.calendarType.number
        )

        val startDate = WesternDate(
            westernDate.year,
            westernDate.month,
            1
        ).toJulian().toLong()

        val endDate = WesternDate(
            westernDate.year,
            westernDate.month,
            monthLength
        ).toJulian().toLong()

        return GregorianMonth(
            year = westernDate.year,
            month = westernDate.month,
            daysInMonth = monthLength,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun getMyanmarMonth(myanmarDate: MyanmarDate): MyanmarMonth {
        val months = MyanmarMonths.of(myanmarDate.year, myanmarDate.month)

        return MyanmarMonth(
            year = myanmarDate.year,
            month = myanmarDate.month,
            monthName = months.getCalculationMonthName(),
            daysInMonth = myanmarDate.monthLength,
            isLeapMonth = myanmarDate.monthType == MonthType.INTERCALARY
        )
    }

    private fun getDaysInMonth(timeMillis: Long): List<DayInfo> {
        val startDate = Date(timeMillis)
        val westernDate = WesternDate.fromMyanmarDate(convertToMyanmarDate(startDate))
        val daysInMonth = WesternDateKernel.getLengthOfMonth(
            westernDate.year,
            westernDate.month,
            config.calendarType.number
        )

        return (1..daysInMonth).map { day ->
            val currentDate = WesternDate(westernDate.year, westernDate.month, day)
            val myanmarDate = MyanmarDate.fromJulian(currentDate.toJulian())

            DayInfo(
                gregorianDay = day,
                myanmarDay = myanmarDate.day,
                myanmarMonth = MyanmarMonths.of(myanmarDate.year, myanmarDate.month)
                    .getCalculationMonthName(),
                timestamp = currentDate.toJulian().toLong()
            )
        }
    }
}