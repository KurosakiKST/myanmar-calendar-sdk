package com.ryan.myanmarcalendar.api.config


data class CalendarConfig(
    val locale: String = "my-MM",
    val timeZone: String = "Asia/Yangon"
) {
    companion object {
        fun default() = CalendarConfig()
    }
}