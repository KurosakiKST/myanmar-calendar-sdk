package com.ryan.myanmarcalendar.core.kernel

import kotlin.math.floor

internal object AstroKernel {
    fun calculateThamaphyu(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(1, 2, 6, 6, 5, 6, 7)
        val wdb = intArrayOf(0, 1, 0, 0, 0, 3, 3)

        return if (mf == wda[weekDay] || mf == wdb[weekDay] || (mf == 4 && weekDay == 5)) 1 else 0
    }

    fun calculateNagapor(md: Int, weekDay: Int): Int {
        val wda = intArrayOf(26, 21, 2, 10, 18, 2, 21)
        val wdb = intArrayOf(17, 19, 1, 0, 9, 0, 0)

        return if (md == wda[weekDay] || md == wdb[weekDay] ||
            ((md == 2 && weekDay == 1) || ((md == 12 || md == 4 || md == 18) && weekDay == 2))) 1 else 0
    }

    fun calculateYatyotema(mmonth: Int, md: Int): Int {
        val mmt = mmonth / 13
        var month = mmonth % 13 + mmt
        if (month <= 0) month = 4

        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val m1 = if (month % 2 != 0) month else (month + 9) % 12
        val targetDay = (m1 + 4) % 12 + 1

        return if (mf == targetDay) 1 else 0
    }

    fun calculateMahayatkyan(mmonth: Int, md: Int): Int {
        var month = mmonth
        if (month <= 0) month = 4

        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val m1 = (((month % 12) / 2) + 4) % 6 + 1

        return if (mf == m1) 1 else 0
    }

    fun calculateShanyat(mmonth: Int, md: Int): Int {
        val mmt = mmonth / 13
        var month = mmonth % 13 + mmt
        if (month <= 0) month = 4

        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val sya = intArrayOf(8, 8, 2, 2, 9, 3, 3, 5, 1, 4, 7, 4)

        return if (mf == sya[month - 1]) 1 else 0
    }

    fun calculateSabbath(yearType: Int, mmonth: Int, md: Int): Int {
        val mml = MyanmarCalendarKernel.calculateLengthOfMonth(mmonth, yearType)

        return when {
            md == 8 || md == 15 || md == 23 || md == mml -> 1
            md == 7 || md == 14 || md == 22 || md == (mml - 1) -> 2
            else -> 0
        }
    }

    fun calculateYatyaza(mm: Int, weekDay: Int): Int {
        val m1 = mm % 4
        val wd1 = (m1 / 2) + 4
        val wd2 = ((1 - (m1 / 2)) + m1 % 2) * (1 + 2 * (m1 % 2))

        return if (weekDay == wd1 || weekDay == wd2) 1 else 0
    }

    fun calculatePyathada(mmonth: Int, weekDay: Int): Int {
        val m1 = mmonth % 4
        val wda = intArrayOf(1, 3, 3, 0, 2, 1, 2)

        return when {
            m1 == wda[weekDay] -> 1
            m1 == 0 && weekDay == 4 -> 2
            else -> 0
        }
    }

    fun calculateNagahle(mmonth: Int): Int {
        val month = if (mmonth <= 0) 4 else mmonth
        return (month % 12) / 3
    }

    fun calculateMahabote(myear: Int, weekDay: Int): Int {
        return (myear - weekDay) % 7
    }

    fun calculateNakhat(myear: Int): Int {
        return myear % 3
    }

    fun calculateThamanyo(mmonth: Int, weekDay: Int): Int {
        val mmt = mmonth / 13
        var month = mmonth % 13 + mmt
        if (month <= 0) month = 4

        val m1 = month - 1 - (month / 9)
        val wd1 = (m1 * 2 - (m1 / 8)) % 7
        val wd2 = (weekDay + 7 - wd1) % 7

        return if (wd2 <= 1) 1 else 0
    }

    fun calculateAmyeittasote(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(5, 8, 3, 7, 2, 4, 1)
        return if (mf == wda[weekDay]) 1 else 0
    }

    fun calculateWarameittugyi(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(7, 1, 4, 8, 9, 6, 3)
        return if (mf == wda[weekDay]) 1 else 0
    }

    fun calculateWarameittunge(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wn = (weekDay + 6) % 7
        return if ((12 - mf) == wn) 1 else 0
    }

    fun calculateYatpote(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(8, 1, 4, 6, 9, 8, 7)
        return if (mf == wda[weekDay]) 1 else 0
    }

    fun calculateYearName(myear: Int): Int {
        return myear % 12
    }
}