package com.ryan.myanmarcalendar.core.kernel

import com.ryan.myanmarcalendar.core.constants.CalendarConstants
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
import com.ryan.myanmarcalendar.data.model.Language
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import com.ryan.myanmarcalendar.data.model.MyanmarMonths
import kotlin.math.floor
import kotlin.math.roundToLong

internal object MyanmarCalendarKernel {
    fun calculateFortnightDay(md: Int): Int {
        return md - 15 * (md / 16)
    }

    fun calculateLengthOfMonth(mm: Int, myt: Int): Int {
        var mml = 30 - mm % 2
        if (mm == 3) {
            mml += myt / 2
        }
        return mml
    }

    fun calculateMoonPhase(myt: Int, mm: Int, md: Int): Int {
        val mml = calculateLengthOfMonth(mm, myt)
        val d = md / mml
        return (floor((md + 1) / 16.0) + floor(md / 16.0) + d).toInt()
    }

    fun calculateMyanmarYearLength(myt: Int): Int {
        return 354 + (1 - floor(1.0 / (myt + 1)).toInt()) * 30 + floor(myt / 2.0).toInt()
    }

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

    fun calculateYearType(myear: Int): Int {
        return MyanmarDateKernel.checkMyanmarYear(myear)["myt"] ?: 0
    }

    fun calculateDayOfMonth(myear: Int, mmonth: Int, moonPhase: Int, fortnightDay: Int): Int {
        val yo = MyanmarDateKernel.checkMyanmarYear(myear)
        val yearType = yo["myt"] ?: 0

        // Adjust month length
        val monthLength = 30 - mmonth % 2 + (if (mmonth == 3) yearType / 2 else 0)

        val m1 = moonPhase % 2
        val m2 = moonPhase / 2

        return m1 * (15 + m2 * (monthLength - 15)) + (1 - m1) * (fortnightDay + 15 * m2)
    }

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

    fun getCalendarHeader(myear: Int, mmonth: Int, language: Language): String {
        return getCalendarHeader(myear, mmonth, 1, language)
    }

    fun getCalendarHeader(myear: Int, mmonth: Int, mday: Int, language: Language): String {
        val julianDate = MyanmarDateKernel.myanmarDateToJulian(myear, mmonth, mday)
        val myanmarDate = MyanmarDate.of(julianDate)

        val js = myanmarDate.toJulian()
        val eml = myanmarDate.lengthOfMonth()
        val je = js + eml - 1

        val endMyanmarDate = MyanmarDate.of(je)

        return getCalendarHeader(myanmarDate, endMyanmarDate, language)
    }

    fun getCalendarHeaderForWesternStyle(year: Int, month: Int, language: Language): String {
        val monthLength = WesternDateKernel.getLengthOfMonth(year, month, 0)
        val startDate = MyanmarDate.of(year, month, 1)
        val endDate = MyanmarDate.of(startDate.toJulian() + monthLength - 1)

        return getCalendarHeader(startDate, endDate, language)
    }
}