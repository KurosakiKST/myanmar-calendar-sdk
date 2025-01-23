package com.ryan.myanmarcalendar.data.model

data class MyanmarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val monthType: MonthType,
    val isLeapYear: Boolean = false
)

enum class MonthType {
    REGULAR,
    INTERCALARY
}