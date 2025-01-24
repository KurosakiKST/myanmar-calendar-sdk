package com.ryan.myanmarcalendar.data.model

enum class CalendarType(val number: Int, val label: String) {
    ENGLISH(0, "English"),
    GREGORIAN(1, "Gregorian"),
    JULIAN(2, "Julian")
}