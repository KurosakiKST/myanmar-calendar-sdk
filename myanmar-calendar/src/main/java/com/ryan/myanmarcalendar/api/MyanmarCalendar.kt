package com.ryan.myanmarcalendar.api

import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.core.kernel.MyanmarDateKernel
import com.ryan.myanmarcalendar.core.kernel.WesternDateKernel
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
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
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date

        return getMonthCalendar(
            year = calendar.get(java.util.Calendar.YEAR),
            month = calendar.get(java.util.Calendar.MONTH) + 1,
            day = calendar.get(java.util.Calendar.DATE)
        )
    }

    private fun getMonthCalendar(year: Int, month: Int, day: Int): CalendarResult {
        val julianDay = WesternDateKernel.westernToJulian(year, month, day)
        val myanmarDate = MyanmarDateKernel.julianToMyanmarDate(julianDay)

        return CalendarResult(
            gregorianMonth = getGregorianMonth(WesternDate(year, month, 1)),
            myanmarMonth = getMyanmarMonth(myanmarDate),
            days = getDaysInMonth(year, month)
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
            day = westernDate.day,
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

    private fun getDaysInMonth(year: Int, month: Int): List<DayInfo> {
        val firstDayJulian = WesternDateKernel.westernToJulian(year, month, 1)
        val firstDayOfWeek = (firstDayJulian.toLong() + 1) % 7

        val paddingDays = (0 until firstDayOfWeek.toInt()).map {
            DayInfo(
                gregorianDay = 0,
                myanmarDay = 0,
                myanmarMonth = "",
                timestamp = 0,
                moonPhase = 0
            )
        }

        val monthDays = (1..WesternDateKernel.getLengthOfMonth(year, month, config.calendarType.number)).map { day ->
            val julianDay = WesternDateKernel.westernToJulian(year, month, day)
            val myanmarDate = MyanmarDateKernel.julianToMyanmarDate(julianDay)

            DayInfo(
                gregorianDay = day,
                myanmarDay = myanmarDate.day,
                myanmarMonth = MyanmarMonths.of(myanmarDate.year, myanmarDate.month).getCalculationMonthName(),
                timestamp = julianDay.toLong(),
                moonPhase = myanmarDate.moonPhase
            )
        }

        return paddingDays + monthDays
    }

    fun getMyanmarText(text: String): String {
        return LanguageTranslator.translate(text, Language.MYANMAR)
    }

    fun getEnglishText(text: String): String {
        return LanguageTranslator.translate(text, Language.ENGLISH)
    }
}