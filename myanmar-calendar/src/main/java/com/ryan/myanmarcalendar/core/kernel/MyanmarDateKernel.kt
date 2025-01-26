package com.ryan.myanmarcalendar.core.kernel

import com.ryan.myanmarcalendar.core.constants.CalendarConstants
import com.ryan.myanmarcalendar.data.model.MonthType
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import kotlin.math.floor
import kotlin.math.roundToLong

internal object MyanmarDateKernel {
    fun julianToMyanmarDate(jd: Double): MyanmarDate {
        val jdn = jd.roundToLong()
        val myear = floor((jdn - 0.5 - CalendarConstants.MO) / CalendarConstants.SY).toInt()
        val yo = checkMyanmarYear(myear)
        val yearType = yo["myt"]!!

        // Calculate month type [0-regular, 1-late]
        val monthType = ((jdn - yo["tg1"]!! + 1) / calculateMyanmarYearLength(yearType)).toInt()
        var dd = jdn - yo["tg1"]!! + 1 - monthType * calculateMyanmarYearLength(yearType)

        // Calculate month and adjust for intercalary months
        val b = yearType / 2
        val c = 1 - floor((yearType + 1.0) / 2.0).toInt()
        val monthAdjust = floor((dd + 423) / 512.0).toInt()
        var month = floor((dd - b * monthAdjust + c * monthAdjust * 30 + 29.26) / 29.544).toInt()

        val e = (month + 12) / 16
        val f = (month + 11) / 16
        val monthDay = (dd - floor(29.544 * month - 29.26) - b * e + c * f * 30).toInt()

        // Adjust month for intercalary months
        month += f * 3 - e * 4 + 12 * monthType

        val monthLength = 30 - month % 2 + (if (month == 3) yearType / 2 else 0)
        val moonPhase = ((monthDay + 1) / 16 + monthDay / 16 + monthDay / monthLength).toInt()
        val fortnightDay = monthDay - 15 * (monthDay / 16)
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
            yearLength = calculateMyanmarYearLength(yearType),
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

        return (dd + yo["tg1"]!! - 1).toDouble()
    }

    private fun checkMyanmarYear(myear: Int): Map<String, Int> {
        val y2 = checkWatat(myear)
        var myt = y2["watat"]!!

        var yd = 0
        var y1: Map<String, Int>
        do {
            yd++
            y1 = checkWatat(myear - yd)
        } while (y1["watat"] == 0 && yd < 3)

        if (myt > 0) {
            val nd = (y2["fm"]!! - y1["fm"]!!) % 354
            myt = floor(nd / 31.0).toInt() + 1
            if (nd != 30 && nd != 31) {
                myt = 0
            }
        }

        return mapOf(
            "myt" to myt,
            "tg1" to (y1["fm"]!! + 354 * yd - 102),
            "fm" to y2["fm"]!!
        )
    }

    private fun checkWatat(myear: Int): Map<String, Int> {
        val (eraId, watatOffset, numberOfMonths) = when {
            myear >= CalendarConstants.SE3 -> Triple(3.0, -0.5, 8.0)
            else -> Triple(2.0, -1.0, 4.0)
        }

        val threshold = (CalendarConstants.SY / 12 - CalendarConstants.LM) * (12 - numberOfMonths)
        var ed = (CalendarConstants.SY * (myear + 3739)) % CalendarConstants.LM

        if (ed < threshold) {
            ed += CalendarConstants.LM
        }

        val fm = (CalendarConstants.SY * myear + CalendarConstants.MO - ed +
                4.5 * CalendarConstants.LM + watatOffset).roundToLong()

        val watat = if (eraId >= 2) {
            val tw = CalendarConstants.LM - (CalendarConstants.SY / 12 - CalendarConstants.LM) * numberOfMonths
            if (ed >= tw) 1 else 0
        } else {
            val w = (myear * 7 + 2) % 19
            floor(w / 12.0).toInt()
        }

        return mapOf("fm" to fm.toInt(), "watat" to watat)
    }

    private fun calculateMyanmarYearLength(yearType: Int): Int {
        val b = yearType / 2
        val c = 1 - floor((yearType + 1.0) / 2.0).toInt()
        return 354 + (1 - c) * 30 + b
    }
}