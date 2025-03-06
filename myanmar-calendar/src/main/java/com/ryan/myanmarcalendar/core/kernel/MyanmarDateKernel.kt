package com.ryan.myanmarcalendar.core.kernel

import com.ryan.myanmarcalendar.core.constants.CalendarConstants
import com.ryan.myanmarcalendar.core.exception.CalendarException
import com.ryan.myanmarcalendar.data.model.MonthType
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import kotlin.math.floor
import kotlin.math.roundToLong

internal object MyanmarDateKernel {
    /**
     * Convert Julian Day Number to Myanmar Date
     */
    fun julianToMyanmarDate(jd: Double): MyanmarDate {
        val jdn = jd.roundToLong()
        val myear = floor((jdn - 0.5 - CalendarConstants.MO) / CalendarConstants.SY).toInt()
        val yo = checkMyanmarYear(myear)
        val yearType = yo["myt"] ?: 0

        // Day count
        var dd = jdn - yo["tg1"]!! + 1

        // Calculate year length based on year type
        val yearLength = MyanmarCalendarKernel.calculateMyanmarYearLength(yearType)

        // Month type: Hnaung =1 or Oo = 0 | late =1 or early = 0
        val monthType = ((dd - 1) / yearLength).toInt()
        dd -= monthType * yearLength

        // Calculate big watat and common year values
        val b = yearType / 2
        val c = 1 - floor((yearType + 1.0) / 2.0).toInt()

        // Adjust day count
        val a = floor((dd + 423) / 512.0).toInt()

        // Calculate month
        var month = floor((dd - b * a + c * a * 30 + 29.26) / 29.544).toInt()

        val e = (month + 12) / 16
        val f = (month + 11) / 16

        // Day of month
        val monthDay = (dd - floor(29.544 * month - 29.26) - b * e + c * f * 30).toInt()

        // Adjust month numbers for late months
        month += f * 3 - e * 4 + 12 * monthType

        // Calculate month length
        val monthLength = 30 - month % 2 + (if (month == 3) yearType / 2 else 0)

        // Calculate moon phase [0=waxing, 1=full moon, 2=waning, 3=new moon]
        val moonPhase = MyanmarCalendarKernel.calculateMoonPhase(yearType, month, monthDay)

        // Calculate fortnight day [1-15]
        val fortnightDay = MyanmarCalendarKernel.calculateFortnightDay(monthDay)

        // Calculate week day [0=sat, 1=sun, ..., 6=fri]
        val weekDay = ((jdn + 2) % 7).toInt()

        // Determine month type enum
        val monthTypeEnum = when {
            month == 0 -> MonthType.SECOND_WASO
            month == 4 && yearType > 0 -> MonthType.INTERCALARY
            else -> MonthType.REGULAR
        }

        return MyanmarDate(
            year = myear,
            month = month,
            day = monthDay,
            monthType = monthTypeEnum,
            yearType = yearType,
            yearLength = yearLength,
            monthLength = monthLength,
            moonPhase = moonPhase,
            fortnightDay = fortnightDay,
            weekDay = weekDay,
            julianDayNumber = jd
        )
    }

    /**
     * Convert Myanmar date to Julian Day Number
     */
    fun myanmarDateToJulian(myear: Int, mmonth: Int, mday: Int): Double {
        val yo = checkMyanmarYear(myear)
        val yearType = yo["myt"] ?: 0

        val mmt = mmonth / 13
        var month = mmonth % 13 + mmt

        // Handle special case for first Waso (month 0)
        if (month <= 0 && mmonth != 0) {
            month = 4  // Default to regular Waso if calculation gives negative or zero
        }

        val b = yearType / 2
        val c = 1 - floor((yearType + 1.0) / 2.0).toInt()

        val adjustedMonth = month + 4 - floor((month + 15) / 16.0).toInt() * 4 +
                floor((month + 12) / 16.0).toInt()

        val dd = mday + floor(29.544 * adjustedMonth - 29.26) -
                c * floor((adjustedMonth + 11) / 16.0).toInt() * 30 +
                b * floor((adjustedMonth + 12) / 16.0).toInt()

        val myl = 354 + (1 - c) * 30 + b

        // Adjust day count with year length
        val adjustedDD = dd + mmt * myl

        return (adjustedDD + yo["tg1"]!! - 1).toDouble()
    }

    /**
     * Check Myanmar Year and return info about the year
     */
    fun checkMyanmarYear(myear: Int): Map<String, Int> {
        val y2 = checkWatat(myear)
        var myt = y2["watat"] ?: 0

        var yd = 0
        var y1: Map<String, Int>

        do {
            yd++
            y1 = checkWatat(myear - yd)
        } while (y1["watat"] == 0 && yd < 3)

        var fm = y2["fm"] ?: 0
        var werr = 0

        if (myt > 0) {
            val nd = (y2["fm"]!! - y1["fm"]!!) % 354
            myt = floor(nd / 31.0).toInt() + 1
            fm = y2["fm"]!!
            if (nd != 30 && nd != 31) {
                werr = 1
            }
        } else {
            fm = y1["fm"]!! + 354 * yd
        }

        val tg1 = y1["fm"]!! + 354 * yd - 102

        return mapOf(
            "myt" to myt,
            "tg1" to tg1,
            "fm" to fm,
            "werr" to werr
        )
    }

    /**
     * Check if the year is a watat year (intercalary month year)
     */
    fun checkWatat(myear: Int): Map<String, Int> {
        // Get constants for the corresponding calendar era
        val eraId: Double
        var watatOffset: Double
        val numberOfMonths: Double
        var exceptionInWatatYear = 0.0

        if (myear >= CalendarConstants.SE3) {
            // The third era (after Independence 1312 ME)
            eraId = 3.0
            watatOffset = -0.5
            numberOfMonths = 8.0

            // Special watat exceptions for specific years
            if (myear == 1344 || myear == 1345) {
                exceptionInWatatYear = 1.0
            }
        } else {
            // The second era (under British colony: 1217 ME - 1311 ME)
            eraId = 2.0
            watatOffset = -1.0
            numberOfMonths = 4.0

            // Special watat exceptions for specific years
            if (myear == 1263 || myear == 1264) {
                exceptionInWatatYear = 1.0
            }
        }

        // Calculate full moon offset adjustments for specific years
        if (myear == 1234) {
            watatOffset += 1
        } else if (myear == 1261) {
            watatOffset -= 1
        }

        // Threshold to adjust
        val threshold = (CalendarConstants.SY / 12 - CalendarConstants.LM) * (12 - numberOfMonths)

        // Excess day
        var ed = (CalendarConstants.SY * (myear + 3739)) % CalendarConstants.LM

        if (ed < threshold) {
            // Adjust excess days
            ed += CalendarConstants.LM
        }

        // Full moon day of 2nd Waso
        val fm = (CalendarConstants.SY * myear + CalendarConstants.MO - ed +
                4.5 * CalendarConstants.LM + watatOffset).roundToLong()

        // Find watat
        var watat = if (eraId >= 2) {
            // If 2nd era or later, find watat based on excess days
            val tw = CalendarConstants.LM - (CalendarConstants.SY / 12 - CalendarConstants.LM) * numberOfMonths
            if (ed >= tw) 1 else 0
        } else {
            // If 1st era, find watat by 19 years metonic cycle
            val w = (myear * 7 + 2) % 19
            val adjustedW = if (w < 0) w + 19 else w
            floor(adjustedW / 12.0).toInt()
        }

        // Apply watat exception
        watat = watat xor exceptionInWatatYear.toInt()

        return mapOf(
            "fm" to fm.toInt(),
            "watat" to watat
        )
    }

    /**
     * Convert Myanmar month name to month number
     */
    fun searchMyanmarMonthNumber(monthName: String): Int {
        return when (monthName.lowercase()) {
            "first waso" -> 0
            "tagu" -> 1
            "kason" -> 2
            "nayon" -> 3
            "waso" -> 4
            "wagaung" -> 5
            "tawthalin" -> 6
            "thadingyut" -> 7
            "tazaungmon" -> 8
            "nadaw" -> 9
            "pyatho" -> 10
            "tabodwe" -> 11
            "tabaung" -> 12
            "late tagu" -> 13
            "late kason" -> 14
            else -> throw CalendarException.InvalidDateException("Invalid Myanmar month name: $monthName")
        }
    }

    /**
     * Convert moon phase name to moon phase number
     */
    fun searchMoonPhase(phaseName: String): Int {
        return when (phaseName.lowercase()) {
            "waxing" -> 0
            "full moon" -> 1
            "waning" -> 2
            "new moon", "dark moon" -> 3
            else -> throw CalendarException.InvalidDateException("Invalid moon phase: $phaseName")
        }
    }

    /**
     * Get Julian Day Number from Myanmar year, month name and day
     */
    fun getJulianDayNumber(myear: Int, monthName: String, day: Int): Double {
        val month = searchMyanmarMonthNumber(monthName)
        return myanmarDateToJulian(myear, month, day)
    }
}