package com.ryan.myanmarcalendar.api

import com.ryan.myanmarcalendar.api.config.CalendarConfig

class MyanmarCalendar private constructor(
    private val config: CalendarConfig
) {
    companion object {
        @Volatile
        private var instance: MyanmarCalendar? = null

        @JvmStatic
        fun getInstance(config: CalendarConfig = CalendarConfig.default()): MyanmarCalendar {
            return instance ?: synchronized(this) {
                instance ?: MyanmarCalendar(config).also { instance = it }
            }
        }
    }

    // Public API methods will go here
}