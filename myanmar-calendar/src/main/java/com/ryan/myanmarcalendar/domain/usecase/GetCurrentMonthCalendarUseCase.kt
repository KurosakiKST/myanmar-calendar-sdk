package com.ryan.myanmarcalendar.domain.usecase

import com.ryan.myanmarcalendar.domain.model.CalendarResult
import com.ryan.myanmarcalendar.domain.repository.CalendarRepository

class GetCurrentMonthCalendarUseCase(
    private val calendarRepository: CalendarRepository
) {
    operator fun invoke(currentTimeMillis: Long): CalendarResult {
        return calendarRepository.getMonthCalendar(currentTimeMillis)
    }
}