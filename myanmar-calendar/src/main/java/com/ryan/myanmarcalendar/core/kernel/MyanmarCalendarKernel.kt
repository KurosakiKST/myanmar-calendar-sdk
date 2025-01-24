package com.ryan.myanmarcalendar.core.kernel

import com.ryan.myanmarcalendar.core.constants.CalendarConstants
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
}