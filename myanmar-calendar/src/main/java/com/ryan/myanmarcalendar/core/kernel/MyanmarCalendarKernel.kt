package com.ryan.myanmarcalendar.core.kernel

import com.ryan.myanmarcalendar.core.constants.CalendarConstants
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
import com.ryan.myanmarcalendar.data.model.Language
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import com.ryan.myanmarcalendar.data.model.MyanmarMonths
import kotlin.math.floor
import kotlin.math.roundToLong

internal object MyanmarCalendarKernel {
    /**
     * Calculate fortnight day from month day (1-30)
     * Returns a value from 1 to 15
     */
    fun calculateFortnightDay(md: Int): Int {
        return md - 15 * (md / 16)
    }

    /**
     * Calculate length of month based on month number and year type
     */
    fun calculateLengthOfMonth(mm: Int, myt: Int): Int {
        var mml = 30 - mm % 2
        if (mm == 3) {
            mml += myt / 2
        }
        return mml
    }

    /**
     * Calculate moon phase from day of the month, month, and year type
     * Returns:
     * 0 = waxing, 1 = full moon, 2 = waning, 3 = new moon
     */
    fun calculateMoonPhase(myt: Int, mm: Int, md: Int): Int {
        val mml = calculateLengthOfMonth(mm, myt)

        if (md == 15) return 1  // Full moon
        if (md == mml) return 3  // New moon

        if (md < 15) return 0    // Waxing
        return 2                 // Waning
    }

    /**
     * Calculate the length of a Myanmar year based on year type
     * Returns:
     * 354 days for common year
     * 384 days for little watat
     * 385 days for big watat
     */
    fun calculateMyanmarYearLength(myt: Int): Int {
        return 354 + (1 - floor(1.0 / (myt + 1)).toInt()) * 30 + floor(myt / 2.0).toInt()
    }

    /**
     * Calculate related Myanmar months for a specific year and month
     */
    fun calculateRelatedMyanmarMonths(myear: Int, mmonth: Int): MyanmarMonths {
        val j1 = (CalendarConstants.SY * myear + CalendarConstants.MO).roundToLong() + 1L
        val j2 = (CalendarConstants.SY * (myear + 1) + CalendarConstants.MO).roundToLong()

        val m1 = MyanmarDateKernel.julianToMyanmarDate(j1.toDouble())
        val m2 = MyanmarDateKernel.julianToMyanmarDate(j2.toDouble())

        var si = m1.month
        val ei = m2.month

        if (si == 0) si = 4

        var targetMonth = mmonth
        if (mmonth == 0 && m1.yearType == 0) {
            targetMonth = 4
        }
        if (mmonth != 0 && mmonth < si) {
            targetMonth = si
        }
        if (mmonth > ei) {
            targetMonth = ei
        }

        return populateMonthLists(m1.yearType, si, ei, targetMonth)
    }

    /**
     * Populate month lists for display
     */
    private fun populateMonthLists(yearType: Int, si: Int, ei: Int, mmonth: Int): MyanmarMonths {
        val monthList = mutableListOf<Int>()
        val monthNameList = mutableListOf<String>()
        var currentIndex = 0

        for (i in si..ei) {
            if (i == 4 && yearType != 0) {
                monthList.add(0)
                monthNameList.add(CalendarConstants.EMA[0])
                if (mmonth == 0) {
                    currentIndex = monthList.size - 1
                }
            }

            monthList.add(i)
            monthNameList.add(
                if (i == 4 && yearType != 0) "Second ${CalendarConstants.EMA[i]}"
                else CalendarConstants.EMA[i]
            )

            if (i == mmonth) {
                currentIndex = monthList.size - 1
            }
        }

        return MyanmarMonths(monthList, monthNameList, monthList[currentIndex])
    }

    /**
     * Calculate year type for a Myanmar year
     */
    fun calculateYearType(myear: Int): Int {
        return MyanmarDateKernel.checkMyanmarYear(myear)["myt"] ?: 0
    }

    /**
     * Calculate day of month from fortnight day, moon phase, and month
     */
    fun calculateDayOfMonth(myear: Int, mmonth: Int, moonPhase: Int, fortnightDay: Int): Int {
        val yo = MyanmarDateKernel.checkMyanmarYear(myear)
        val yearType = yo["myt"] ?: 0

        // Adjust month length
        val monthLength = calculateLengthOfMonth(mmonth, yearType)

        // Handle different moon phases
        return when (moonPhase) {
            0 -> fortnightDay                  // Waxing
            1 -> 15                           // Full moon
            2 -> 15 + fortnightDay            // Waning
            3 -> monthLength                  // New moon
            else -> throw IllegalArgumentException("Invalid moon phase: $moonPhase")
        }
    }

    /**
     * Generate a formatted calendar header for the given date range
     */
    fun getCalendarHeader(
        startDate: MyanmarDate,
        endDate: MyanmarDate,
        language: Language
    ): String {
        val str = StringBuilder()

        str.append(getHeaderForBuddhistEra(startDate, endDate, language))

        if (endDate.year >= 2) {
            str.append(language.punctuationMark)
                .append(getHeaderForMyanmarYear(startDate, endDate, language))
                .append(language.punctuationMark)

            str.append(getHeaderForMyanmarMonth(startDate, endDate, language))
        }

        return str.toString()
    }

    /**
     * Format Buddhist Era header
     */
    fun getHeaderForBuddhistEra(startDate: MyanmarDate, endDate: MyanmarDate, language: Language): String {
        val sb = StringBuilder()

        sb.append(LanguageTranslator.translate("Sasana Year", language))
            .append(" ")
            .append(startDate.getBuddhistEra(language))

        if (startDate.getBuddhistEraValue() != endDate.getBuddhistEraValue()) {
            sb.append(" - ")
                .append(endDate.getBuddhistEra(language))
        }

        sb.append(" ")
            .append(LanguageTranslator.translate("Ku", language))

        return sb.toString()
    }

    /**
     * Format Myanmar Year header
     */
    fun getHeaderForMyanmarYear(startDate: MyanmarDate, endDate: MyanmarDate, language: Language): String {
        val sb = StringBuilder()

        sb.append(LanguageTranslator.translate("Myanmar Year", language))
            .append(" ")

        if (startDate.year >= 2) {
            sb.append(startDate.getYear(language))
            if (startDate.year != endDate.year) {
                sb.append(" - ")
            }
        }

        if (startDate.year != endDate.year) {
            sb.append(endDate.getYear(language))
        }

        sb.append(" ")
            .append(LanguageTranslator.translate("Ku", language))

        return sb.toString()
    }

    /**
     * Format Myanmar Month header
     */
    fun getHeaderForMyanmarMonth(startDate: MyanmarDate, endDate: MyanmarDate, language: Language): String {
        val sb = StringBuilder()

        if (startDate.year >= 2) {
            sb.append(startDate.getMonthName(language))
            if (startDate.month != endDate.month) {
                sb.append(" - ")
            }
        }

        if (startDate.month != endDate.month) {
            sb.append(endDate.getMonthName(language))
        }

        return sb.toString()
    }

    /**
     * Get calendar header for Myanmar year and month
     */
    fun getCalendarHeader(myear: Int, mmonth: Int, language: Language): String {
        return getCalendarHeader(myear, mmonth, 1, language)
    }

    /**
     * Get calendar header for Myanmar year, month and day
     */
    fun getCalendarHeader(myear: Int, mmonth: Int, mday: Int, language: Language): String {
        val julianDate = MyanmarDateKernel.myanmarDateToJulian(myear, mmonth, mday)
        val myanmarDate = MyanmarDate.of(julianDate)

        val js = myanmarDate.toJulian()
        val eml = myanmarDate.lengthOfMonth()
        val je = js + eml - 1

        val endMyanmarDate = MyanmarDate.of(je)

        return getCalendarHeader(myanmarDate, endMyanmarDate, language)
    }

    /**
     * Get calendar header for Western date
     */
    fun getCalendarHeaderForWesternStyle(year: Int, month: Int, language: Language): String {
        val monthLength = WesternDateKernel.getLengthOfMonth(year, month, 0)
        val startDate = MyanmarDate.of(year, month, 1)
        val endDate = MyanmarDate.of(startDate.toJulian() + monthLength - 1)

        return getCalendarHeader(startDate, endDate, language)
    }
}