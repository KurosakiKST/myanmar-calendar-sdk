package com.ryan.myanmarcalendar.domain.model

data class CalendarResult(
    val gregorianMonth: GregorianMonth,
    val myanmarMonth: MyanmarMonth,
    val days: List<DayInfo>
)

data class DayInfo(
    val gregorianDay: Int,
    val myanmarDay: Int,
    val myanmarMonth: String,
    val timestamp: Long
)

data class GregorianMonth(
    val year: Int,
    val month: Int,
    val daysInMonth: Int,
    val startDate: Long,
    val endDate: Long
)

data class MyanmarMonth(
    val year: Int,
    val month: Int,
    val monthName: String,
    val daysInMonth: Int,
    val isLeapMonth: Boolean
)