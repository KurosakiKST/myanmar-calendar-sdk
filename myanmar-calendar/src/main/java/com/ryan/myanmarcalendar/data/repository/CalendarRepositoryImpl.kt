package com.ryan.myanmarcalendar.data.repository

import com.ryan.myanmarcalendar.data.model.MonthType
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import com.ryan.myanmarcalendar.domain.model.CalendarResult
import com.ryan.myanmarcalendar.domain.model.GregorianMonth
import com.ryan.myanmarcalendar.domain.model.MyanmarMonth
import com.ryan.myanmarcalendar.domain.repository.CalendarRepository
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class CalendarRepositoryImpl : CalendarRepository {
    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yangon"))

    override fun convertToMyanmarDate(date: Date): MyanmarDate {
        // TODO: Implement conversion logic
        return MyanmarDate(0, 0, 0, MonthType.REGULAR)
    }

    override fun convertToGregorianDate(myanmarDate: MyanmarDate): Date {
        // TODO: Implement conversion logic
        return Date()
    }

    override fun getMonthCalendar(timeMillis: Long): CalendarResult {
        calendar.timeInMillis = timeMillis

        val gregorianMonth = getGregorianMonth(calendar)
        val myanmarMonth = getMyanmarMonth(calendar)

        return CalendarResult(gregorianMonth, myanmarMonth)
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
        // TODO: Implement Myanmar calendar conversion
        return MyanmarMonth(
            year = 1385,
            month = 1,
            monthName = "Tagu",
            daysInMonth = 30,
            isLeapMonth = false
        )
    }
}