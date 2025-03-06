package com.ryan.myanmarcalendar.core.kernel

import com.ryan.myanmarcalendar.core.constants.CalendarConstants
import com.ryan.myanmarcalendar.core.exception.CalendarException
import com.ryan.myanmarcalendar.data.model.MonthType
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import kotlin.math.floor
import kotlin.math.roundToLong

internal object MyanmarDateKernel {
    fun julianToMyanmarDate(jd: Double): MyanmarDate {
        val jdn = jd.roundToLong()
        val myear = floor((jdn - 0.5 - CalendarConstants.MO) / CalendarConstants.SY).toInt()
        val yo = checkMyanmarYear(myear)
        val yearType = yo["myt"] ?: 0

        // Day count
        var dd = jdn - yo["tg1"]!! + 1

        // Month type: Hnaung =1 or Oo = 0 | late =1 or early = 0
        val monthType = ((dd - 1) / MyanmarCalendarKernel.calculateMyanmarYearLength(yearType)).toInt()
        dd -= monthType * MyanmarCalendarKernel.calculateMyanmarYearLength(yearType)

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

        // Month length
        val monthLength = 30 - month % 2 + (if (month == 3) yearType / 2 else 0)

        // Moon phase
        val moonPhase = ((monthDay + 1) / 16 + monthDay / 16 + monthDay / monthLength).toInt()

        // Fortnight day
        val fortnightDay = monthDay - 15 * (monthDay / 16)

        // Week day
        val weekDay = ((jdn + 2) % 7).toInt()

        return MyanmarDate(
            year = myear,
            month = month,
            day = monthDay,
            monthType = when {
                month == 4 && yearType > 0 -> MonthType.INTERCALARY
                month == 0 -> MonthType.SECOND_WASO
                else -> MonthType.REGULAR
            },
            yearType = yearType,
            yearLength = MyanmarCalendarKernel.calculateMyanmarYearLength(yearType),
            monthLength = monthLength,
            moonPhase = moonPhase,
            fortnightDay = fortnightDay,
            weekDay = weekDay,
            julianDayNumber = jd
        )
    }

    fun myanmarDateToJulian(myear: Int, mmonth: Int, mday: Int): Double {
        val yo = checkMyanmarYear(myear)
        val mmt = mmonth / 13
        val month = mmonth % 13 + mmt

        val b = yo["myt"]!! / 2
        val c = 1 - floor((yo["myt"]!! + 1.0) / 2.0).toInt()

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

    fun checkWatat(myear: Int): Map<String, Int> {
        // Get constants for the corresponding calendar era
        val eraId: Double
        val watatOffset: Double
        val numberOfMonths: Double

        if (myear >= CalendarConstants.SE3) {
            // The third era (after Independence 1312 ME)
            eraId = 3.0
            watatOffset = -0.5
            numberOfMonths = 8.0
        } else {
            // The second era (under British colony: 1217 ME - 1311 ME)
            eraId = 2.0
            watatOffset = -1.0
            numberOfMonths = 4.0
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
        val watat = if (eraId >= 2) {
            // If 2nd era or later, find watat based on excess days
            val tw = CalendarConstants.LM - (CalendarConstants.SY / 12 - CalendarConstants.LM) * numberOfMonths
            if (ed >= tw) 1 else 0
        } else {
            // If 1st era, find watat by 19 years metonic cycle
            val w = (myear * 7 + 2) % 19
            val adjustedW = if (w < 0) w + 19 else w
            floor(adjustedW / 12.0).toInt()
        }

        return mapOf(
            "fm" to fm.toInt(),
            "watat" to watat
        )
    }

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

    fun searchMoonPhase(phaseName: String): Int {
        return when (phaseName.lowercase()) {
            "waxing" -> 0
            "full moon" -> 1
            "waning" -> 2
            "new moon", "dark moon" -> 3
            else -> throw CalendarException.InvalidDateException("Invalid moon phase: $phaseName")
        }
    }

    fun getJulianDayNumber(myear: Int, monthName: String, day: Int): Double {
        val month = searchMyanmarMonthNumber(monthName)
        return myanmarDateToJulian(myear, month, day)
    }
}