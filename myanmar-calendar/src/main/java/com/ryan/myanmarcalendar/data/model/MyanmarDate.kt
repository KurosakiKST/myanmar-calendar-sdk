package com.ryan.myanmarcalendar.data.model

import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.core.constants.CalendarConstants
import com.ryan.myanmarcalendar.core.kernel.MyanmarCalendarKernel
import com.ryan.myanmarcalendar.core.kernel.MyanmarDateKernel
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
import java.io.Serializable
import java.time.*
import java.util.Date

data class MyanmarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val monthType: MonthType = MonthType.REGULAR,
    val yearType: Int = 0, // 0=common, 1=little watat, 2=big watat
    val yearLength: Int = 0,
    val monthLength: Int = 0,
    val moonPhase: Int = 0, // 0=waxing, 1=full moon, 2=waning, 3=new moon
    val fortnightDay: Int = 0,
    val weekDay: Int = 0,
    private val julianDayNumber: Double = 0.0
) : Serializable {

    // Calculate Buddhist Era based on Myanmar year
    fun getBuddhistEraValue(): Int {
        val buddhistEraOffset = if (month == 1 || (month == 2 && day < 16)) 1181 else 1182
        return year + buddhistEraOffset
    }

    fun getBuddhistEra(language: Language = CalendarConfig.getInstance().language): String {
        return LanguageTranslator.translate(getBuddhistEraValue().toDouble(), language)
    }

    fun getYearValue(): Int = year

    fun getYear(language: Language = CalendarConfig.getInstance().language): String {
        return LanguageTranslator.translate(year.toDouble(), language)
    }

    fun getMonthName(language: Language = CalendarConfig.getInstance().language): String {
        val sb = StringBuilder()

        if (month == 4 && yearType > 0) {
            sb.append(LanguageTranslator.translate("Second", language))
            sb.append(" ")
        }

        val monthName = when (month) {
            0 -> "First Waso"
            else -> CalendarConstants.EMA[month]
        }

        sb.append(LanguageTranslator.translate(monthName, language))
        return sb.toString()
    }

    fun getMoonPhase(language: Language = CalendarConfig.getInstance().language): String {
        return LanguageTranslator.translate(CalendarConstants.MSA[moonPhase], language)
    }

    fun getFortnightDay(language: Language = CalendarConfig.getInstance().language): String {
        return if ((moonPhase % 2) == 0) {
            LanguageTranslator.translate(fortnightDay.toString(), language)
        } else {
            ""
        }
    }

    fun getWeekDay(language: Language = CalendarConfig.getInstance().language): String {
        return LanguageTranslator.translate(CalendarConstants.WDA[weekDay], language)
    }

    fun isWeekend(): Boolean {
        return weekDay == 0 || weekDay == 1 // Saturday or Sunday
    }

    fun lengthOfMonth(): Int = monthLength

    fun lengthOfYear(): Int = yearLength

    fun toJulian(): Double {
        return julianDayNumber
    }

    fun toWesternDate(calendarType: CalendarType = CalendarConfig.getInstance().calendarType): WesternDate {
        return WesternDate.fromJulian(julianDayNumber, calendarType)
    }

    fun toMyanmarZonedDateTime(): ZonedDateTime {
        return toZonedDateTime(CalendarConstants.MYANMAR_ZONE_ID)
    }

    fun toZonedDateTime(zoneId: ZoneId): ZonedDateTime {
        val wd = toWesternDate()
        return LocalDateTime.of(
            wd.year, wd.month, wd.day, wd.hour, wd.minute, wd.second
        ).atZone(CalendarConstants.MYANMAR_ZONE_ID)
            .withZoneSameInstant(zoneId)
    }

    fun toMyanmarLocalDateTime(): LocalDateTime {
        return toLocalDateTime(CalendarConstants.MYANMAR_ZONE_ID)
    }

    fun toLocalDateTime(zoneId: ZoneId): LocalDateTime {
        return toZonedDateTime(zoneId).toLocalDateTime()
    }

    fun toMyanmarLocalDate(): LocalDate {
        return toLocalDate(CalendarConstants.MYANMAR_ZONE_ID)
    }

    fun toLocalDate(zoneId: ZoneId): LocalDate {
        return toZonedDateTime(zoneId).toLocalDate()
    }

    fun toMyanmarLocalTime(): LocalTime {
        return toLocalTime(CalendarConstants.MYANMAR_ZONE_ID)
    }

    fun toLocalTime(zoneId: ZoneId): LocalTime {
        return toZonedDateTime(zoneId).toLocalTime()
    }

    fun format(pattern: String, language: Language = CalendarConfig.getInstance().language): String {
        if (pattern.isBlank()) return toString(language)

        val sb = StringBuilder()
        for (c in pattern) {
            when (c) {
                'S' -> sb.append(LanguageTranslator.translate("Sasana Year", language))
                's' -> sb.append(getBuddhistEra(language))
                'B' -> sb.append(LanguageTranslator.translate("Myanmar Year", language))
                'y' -> sb.append(getYear(language))
                'k' -> sb.append(LanguageTranslator.translate("Ku", language))
                'M' -> sb.append(getMonthName(language))
                'p' -> sb.append(getMoonPhase(language))
                'f' -> sb.append(getFortnightDay(language))
                'E' -> sb.append(getWeekDay(language))
                'n' -> sb.append(LanguageTranslator.translate("Nay", language))
                'r' -> sb.append(LanguageTranslator.translate("Yat", language))
                else -> sb.append(c)
            }
        }

        return sb.toString()
    }

    fun hasSameDay(other: MyanmarDate): Boolean {
        return year == other.year && month == other.month && day == other.day
    }

    override fun toString(): String {
        return toString(CalendarConfig.getInstance().language)
    }

    fun toString(language: Language): String {
        return format("S s k, B y k, M p f r E n", language)
    }

    companion object {
        fun of(julianDate: Double): MyanmarDate {
            return MyanmarDateKernel.julianToMyanmarDate(julianDate)
        }

        fun of(year: Int, month: Int, day: Int): MyanmarDate {
            val julianDay = MyanmarDateKernel.myanmarDateToJulian(year, month, day)
            return of(julianDay)
        }

        fun of(year: Int, month: Int, moonPhase: Int, fortnightDay: Int): MyanmarDate {
            val day = MyanmarCalendarKernel.calculateDayOfMonth(year, month, moonPhase, fortnightDay)
            return of(year, month, day)
        }

        fun of(date: Date): MyanmarDate {
            val localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            return of(localDateTime)
        }

        fun of(localDateTime: LocalDateTime): MyanmarDate {
            val westernDate = WesternDate(
                year = localDateTime.year,
                month = localDateTime.monthValue,
                day = localDateTime.dayOfMonth,
                hour = localDateTime.hour,
                minute = localDateTime.minute,
                second = localDateTime.second
            )
            return of(westernDate.toJulian())
        }

        fun of(zonedDateTime: ZonedDateTime): MyanmarDate {
            val myanmarLocalDateTime = zonedDateTime
                .withZoneSameInstant(CalendarConstants.MYANMAR_ZONE_ID)
                .toLocalDateTime()
            return of(myanmarLocalDateTime)
        }

        fun now(): MyanmarDate {
            val myanmarDateTime = LocalDateTime.now(CalendarConstants.MYANMAR_ZONE_ID)
            return of(myanmarDateTime)
        }

        // For compatibility with existing code
        fun fromJulian(julianDate: Double): MyanmarDate = of(julianDate)
        fun fromDate(date: Date): MyanmarDate = of(date)
        fun fromLocalDateTime(localDateTime: LocalDateTime): MyanmarDate = of(localDateTime)
    }
}