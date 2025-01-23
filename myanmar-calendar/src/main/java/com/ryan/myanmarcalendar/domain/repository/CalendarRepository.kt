package com.ryan.myanmarcalendar.domain.repository

import com.ryan.myanmarcalendar.data.model.MyanmarDate
import com.ryan.myanmarcalendar.domain.model.CalendarResult
import java.util.Date

interface CalendarRepository {
    fun convertToMyanmarDate(date: Date): MyanmarDate
    fun getMonthCalendar(timeMillis: Long): CalendarResult
}