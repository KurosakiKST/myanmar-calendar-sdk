package com.ryan.myanmarcalendar.data.repository

import com.ryan.myanmarcalendar.data.model.MonthType
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import com.ryan.myanmarcalendar.domain.model.CalendarResult
import com.ryan.myanmarcalendar.domain.model.DayInfo
import com.ryan.myanmarcalendar.domain.model.GregorianMonth
import com.ryan.myanmarcalendar.domain.model.MyanmarMonth
import com.ryan.myanmarcalendar.domain.repository.CalendarRepository
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class CalendarRepositoryImpl : CalendarRepository {
    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yangon"))

    // Myanmar Era constant (ME) - difference between Georgian and Myanmar calendar
    private val MYANMAR_YEAR_OFFSET = 638

    private val MYANMAR_MONTHS = listOf(
        "Tagu", "Kason", "Nayon", "Waso", "Wagaung", "Tawthalin",
        "Thadingyut", "Tazaungmon", "Nadaw", "Pyatho", "Tabodwe", "Tabaung"
    )

    override fun convertToMyanmarDate(date: Date): MyanmarDate {
        calendar.time = date

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Calculate Myanmar year
        val myanmarYear = year - MYANMAR_YEAR_OFFSET

        // Simplified month calculation (approximate)
        var myanmarMonth = (month + 9) % 12
        if (myanmarMonth == 0) myanmarMonth = 12

        // Simplified day calculation
        val myanmarDay = day

        return MyanmarDate(myanmarYear, myanmarMonth, myanmarDay, MonthType.REGULAR)
    }

    override fun getMonthCalendar(timeMillis: Long): CalendarResult {
        calendar.timeInMillis = timeMillis

        val gregorianMonth = getGregorianMonth(calendar)
        val myanmarMonth = getMyanmarMonth(calendar)
        val days = getDaysInMonth(timeMillis)

        return CalendarResult(gregorianMonth, myanmarMonth, days)
    }

    private fun getGregorianMonth(calendar: Calendar): GregorianMonth {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth)
        val endDate = calendar.timeInMillis

        return GregorianMonth(year, month, daysInMonth, startDate, endDate)
    }

    private fun getMyanmarMonth(calendar: Calendar): MyanmarMonth {
        val date = calendar.time
        val myanmarDate = convertToMyanmarDate(date)

        // Calculate days in Myanmar month (simplified)
        val daysInMonth = when (myanmarDate.month) {
            2, 3, 4, 5, 6, 9 -> 30
            else -> 29
        }

        return MyanmarMonth(
            year = myanmarDate.year,
            month = myanmarDate.month,
            monthName = MYANMAR_MONTHS[myanmarDate.month - 1],
            daysInMonth = daysInMonth,
            isLeapMonth = false  // TODO Needs proper calculation for leap months
        )
    }

    private fun getDaysInMonth(timeMillis: Long): List<DayInfo> {
        calendar.timeInMillis = timeMillis
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val days = mutableListOf<DayInfo>()

        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val currentDate = calendar.time
            val myanmarDate = convertToMyanmarDate(currentDate)

            days.add(DayInfo(
                gregorianDay = day,
                myanmarDay = myanmarDate.day,
                myanmarMonth = MYANMAR_MONTHS[myanmarDate.month - 1],
                timestamp = calendar.timeInMillis
            ))
        }

        return days
    }
}