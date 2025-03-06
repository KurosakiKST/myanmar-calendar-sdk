package com.ryan.myanmarcalendar.api

import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.core.kernel.MyanmarCalendarKernel
import com.ryan.myanmarcalendar.core.kernel.MyanmarDateKernel
import com.ryan.myanmarcalendar.core.kernel.WesternDateKernel
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
import com.ryan.myanmarcalendar.data.model.*
import com.ryan.myanmarcalendar.domain.model.CalendarResult
import com.ryan.myanmarcalendar.domain.model.DayInfo
import com.ryan.myanmarcalendar.domain.model.GregorianMonth
import com.ryan.myanmarcalendar.domain.model.MyanmarMonth
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class MyanmarCalendar private constructor(private val config: CalendarConfig) {

    /**
     * Get the current month calendar based on system time
     */
    fun getCurrentMonthCalendar(): CalendarResult {
        return getMonthCalendar(System.currentTimeMillis())
    }

    /**
     * Get the month calendar for a specific timestamp
     */
    fun getMonthCalendar(timeMillis: Long): CalendarResult {
        val date = Date(timeMillis)
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date

        return getMonthCalendar(
            year = calendar.get(java.util.Calendar.YEAR),
            month = calendar.get(java.util.Calendar.MONTH) + 1,
            day = calendar.get(java.util.Calendar.DATE)
        )
    }

    /**
     * Get month calendar for a specific year, month and day
     */
    fun getMonthCalendar(year: Int, month: Int, day: Int): CalendarResult {
        val julianDay = WesternDateKernel.westernToJulian(year, month, day, config.calendarType.number)
        val myanmarDate = MyanmarDateKernel.julianToMyanmarDate(julianDay)

        return CalendarResult(
            gregorianMonth = getGregorianMonth(WesternDate(year, month, 1)),
            myanmarMonth = getMyanmarMonth(myanmarDate),
            days = getDaysInMonth(year, month)
        )
    }

    /**
     * Convert a Date object to MyanmarDate
     */
    fun convertToMyanmarDate(date: Date): MyanmarDate {
        val localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return MyanmarDate.of(localDateTime)
    }

    /**
     * Convert a year/month/day to MyanmarDate
     */
    fun convertToMyanmarDate(year: Int, month: Int, day: Int): MyanmarDate {
        val julianDay = WesternDateKernel.westernToJulian(year, month, day, config.calendarType.number)
        return MyanmarDateKernel.julianToMyanmarDate(julianDay)
    }

    /**
     * Get astrological information for a specific date
     */
    fun getAstrological(date: Date): Astro {
        val myanmarDate = convertToMyanmarDate(date)
        return Astro.of(myanmarDate)
    }

    /**
     * Get astrological information for a specific Myanmar date
     */
    fun getAstrological(myanmarDate: MyanmarDate): Astro {
        return Astro.of(myanmarDate)
    }

    /**
     * Get astrological information for a specific year, month, day
     */
    fun getAstrological(year: Int, month: Int, day: Int): Astro {
        val myanmarDate = convertToMyanmarDate(year, month, day)
        return Astro.of(myanmarDate)
    }

    /**
     * Get calendar header for a Myanmar year and month
     */
    fun getCalendarHeader(myear: Int, mmonth: Int): String {
        return MyanmarCalendarKernel.getCalendarHeader(myear, mmonth, config.language)
    }

    /**
     * Get calendar header for a Western year and month
     */
    fun getWesternCalendarHeader(year: Int, month: Int): String {
        return MyanmarCalendarKernel.getCalendarHeaderForWesternStyle(year, month, config.language)
    }

    companion object {
        @Volatile private var instance: MyanmarCalendar? = null

        @JvmStatic
        fun getInstance(config: CalendarConfig = CalendarConfig.getInstance()): MyanmarCalendar {
            return instance ?: synchronized(this) {
                instance ?: MyanmarCalendar(config).also { instance = it }
            }
        }
    }

    /**
     * Create a GregorianMonth object from a WesternDate
     */
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
        ).toJulian(config.calendarType).toLong()

        val endDate = WesternDate(
            westernDate.year,
            westernDate.month,
            monthLength
        ).toJulian(config.calendarType).toLong()

        return GregorianMonth(
            day = westernDate.day,
            year = westernDate.year,
            month = westernDate.month,
            daysInMonth = monthLength,
            startDate = startDate,
            endDate = endDate
        )
    }

    /**
     * Create a MyanmarMonth object from a MyanmarDate
     */
    private fun getMyanmarMonth(myanmarDate: MyanmarDate): MyanmarMonth {
        val months = MyanmarMonths.of(myanmarDate.year, myanmarDate.month)

        return MyanmarMonth(
            year = myanmarDate.year,
            month = myanmarDate.month,
            monthName = months.getCalculationMonthName(),
            daysInMonth = myanmarDate.lengthOfMonth(),
            isLeapMonth = myanmarDate.monthType == MonthType.INTERCALARY
        )
    }

    /**
     * Get all days in a month with their Myanmar date information
     */
    private fun getDaysInMonth(year: Int, month: Int): List<DayInfo> {
        val firstDayJulian = WesternDateKernel.westernToJulian(year, month, 1, config.calendarType.number)
        val firstDayOfWeek = ((firstDayJulian.toLong() + 2) % 7).toInt()  // +2 to adjust 0=sat, 1=sun...
        val daysInMonth = WesternDateKernel.getLengthOfMonth(year, month, config.calendarType.number)

        // Calculate padding days for the calendar grid (to align first day of month with correct weekday)
        val paddingDays = (0 until firstDayOfWeek).map {
            DayInfo(
                gregorianDay = 0,
                myanmarDay = 0,
                myanmarMonth = "",
                timestamp = 0,
                moonPhase = 0
            )
        }

        // Get all month days with their Myanmar date information
        val monthDays = (1..daysInMonth).map { day ->
            val julianDay = WesternDateKernel.westernToJulian(year, month, day, config.calendarType.number)
            val myanmarDate = MyanmarDateKernel.julianToMyanmarDate(julianDay)
            val astro = Astro.of(myanmarDate)

            DayInfo(
                gregorianDay = day,
                myanmarDay = myanmarDate.day,
                myanmarMonth = myanmarDate.getMonthName(config.language),
                timestamp = julianDay.toLong(),
                moonPhase = myanmarDate.moonPhase
            )
        }

        return paddingDays + monthDays
    }

    /**
     * Translate text to Myanmar
     */
    fun getMyanmarText(text: String): String {
        return LanguageTranslator.translate(text, Language.MYANMAR)
    }

    /**
     * Translate text to English
     */
    fun getEnglishText(text: String): String {
        return LanguageTranslator.translate(text, Language.ENGLISH)
    }
}