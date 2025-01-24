package com.ryan.myanmarcalendar.api.config

import com.ryan.myanmarcalendar.data.model.CalendarType
import com.ryan.myanmarcalendar.data.model.Language

class CalendarConfig private constructor(
    val calendarType: CalendarType = CalendarType.ENGLISH,
    val language: Language = Language.MYANMAR
) {
    companion object {
        private var instance: CalendarConfig? = null

        fun getInstance() = instance ?: CalendarConfig().also { instance = it }

        fun init(config: CalendarConfig) {
            instance = config
        }

        fun default() = CalendarConfig()
    }

    class Builder {
        private var calendarType: CalendarType = CalendarType.ENGLISH
        private var language: Language = Language.MYANMAR

        fun setCalendarType(type: CalendarType) = apply { calendarType = type }
        fun setLanguage(lang: Language) = apply { language = lang }
        fun build() = CalendarConfig(calendarType, language)
    }
}