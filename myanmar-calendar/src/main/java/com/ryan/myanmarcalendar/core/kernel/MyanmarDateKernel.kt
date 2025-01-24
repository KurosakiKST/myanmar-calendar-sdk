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
        var dd = jdn - yo["tg1"]!! + 1
        val b = yo["myt"]!! / 2
        val c = 1 / (yo["myt"]!! + 1)

        val yearLength = (354 + (1 - c) * 30 + b).toShort()
        val monthType = ((dd - 1) / yearLength).toShort()

        dd -= monthType * yearLength
        val a = ((dd + 423) / 512).toInt()

        var mmonth = floor((dd - b * a + c * a * 30 + 29.26) / 29.544).toInt().toShort()
        val e = (mmonth + 12) / 16
        val f = (mmonth + 11) / 16

        val monthDay = (dd - floor(29.544 * mmonth - 29.26) - b * e + c * f * 30).toInt().toShort()
        mmonth = (f * 3 - e * 4 + 12 * monthType.toInt()).toShort()

        val monthLength = 30 - mmonth % 2

        return MyanmarDate(
            year = myear,
            month = mmonth.toInt(),
            day = monthDay.toInt(),
            monthType = if (monthType > 0) MonthType.INTERCALARY else MonthType.REGULAR
        )
    }

    fun myanmarDateToJulian(myear: Int, mmonth: Int, mmday: Int): Int {
        val yo = checkMyanmarYear(myear)
        val mmt = mmonth / 13
        var monthNum = mmonth % 13 + mmt

        val b = yo["myt"]!! / 2
        val c = 1 - floor((yo["myt"]!!.toFloat() + 1) / 2).toInt()

        monthNum += 4 - floor((monthNum + 15) / 16.0).toInt() * 4 + floor((monthNum + 12) / 16.0).toInt()

        var dd = mmday + floor(29.544 * monthNum - 29.26).toInt() -
                c * floor((monthNum + 11) / 16.0).toInt() * 30 +
                b * floor((monthNum + 12) / 16.0).toInt()

        val myl = 354 + (1 - c) * 30 + b
        dd += mmt * myl

        return dd + yo["tg1"]!! - 1
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

        var fm: Int
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

    private fun checkWatat(myear: Int): Map<String, Int> {
        val constants = getMyanmarYearConstants(myear)
        val threshold = (CalendarConstants.SY / 12 - CalendarConstants.LM) * (12 - constants["NM"]!!)
        var ed = (CalendarConstants.SY * (myear + 3739)) % CalendarConstants.LM

        if (ed < threshold) {
            ed += CalendarConstants.LM
        }

        val fm = (CalendarConstants.SY * myear + CalendarConstants.MO - ed + 4.5 * CalendarConstants.LM +
                constants["WO"]!!).roundToLong().toInt()

        var watat = 0

        if (constants["EI"]!! >= 2) {
            val tw = (CalendarConstants.LM - (CalendarConstants.SY / 12 - CalendarConstants.LM) * constants["NM"]!!)
            if (ed >= tw) watat = 1
        } else {
            watat = ((myear * 7 + 2) % 19).let { if (it < 0) it + 19 else it }
            watat = floor(watat / 12.0).toInt()
        }

        watat = watat xor constants["EW"]!!.toInt()

        return mapOf("fm" to fm, "watat" to watat)
    }

    private fun getMyanmarYearConstants(myear: Int): Map<String, Double> {
        // Values depend on era
        val (eraId, watatOffset, numberOfMonths, exceptionInWatatYear) = when {
            myear >= 1312 -> listOf(3.0, -0.5, 8.0, 0.0)
            myear >= 1217 -> listOf(2.0, -1.0, 4.0, 0.0)
            myear >= 1100 -> listOf(1.3, -0.85, -1.0, 0.0)
            myear >= 798 -> listOf(1.2, -1.1, -1.0, 0.0)
            else -> listOf(1.1, -1.1, -1.0, 0.0)
        }

        return mapOf(
            "EI" to eraId,
            "WO" to watatOffset,
            "NM" to numberOfMonths,
            "EW" to exceptionInWatatYear
        )
    }
}