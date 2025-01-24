package com.ryan.myanmarcalendar.core.kernel

import com.ryan.myanmarcalendar.data.model.CalendarType
import com.ryan.myanmarcalendar.data.model.WesternDate
import kotlin.math.floor

internal object WesternDateKernel {
    fun julianToWestern(julianDate: Double, calType: Int = 0, sg: Double = 2361222.0): WesternDate {
        val cleanCalType = calType.coerceAtLeast(0)
        var j: Double
        var jf: Double
        var y: Double
        var m: Double
        var d: Double
        val h: Double
        val n: Double
        val s: Double

        if (calType == 2 || (calType == 0 && julianDate < sg)) {
            j = floor(julianDate + 0.5)
            jf = julianDate + 0.5 - j

            val b = j + 1524
            val c = floor((b - 122.1) / 365.25)
            val f = floor(365.25 * c)
            val e = floor((b - f) / 30.6001)

            m = if (e > 13) e - 13 else e - 1
            d = b - f - floor(30.6001 * e)
            y = if (m < 3) c - 4715 else c - 4716
        } else {
            j = floor(julianDate + 0.5)
            jf = julianDate + 0.5 - j
            j -= 1721119

            y = floor((4 * j - 1) / 146097.0)
            j = 4 * j - 1 - 146097 * y
            d = floor(j / 4.0)
            j = floor((4 * d + 3) / 1461.0)
            d = 4 * d + 3 - 1461 * j
            d = floor((d + 4) / 4.0)
            m = floor((5 * d - 3) / 153.0)
            d = 5 * d - 3 - 153 * m
            d = floor((d + 5) / 5.0)
            y = 100 * y + j

            if (m < 10) {
                m += 3
            } else {
                m -= 9
                y += 1
            }
        }

        jf *= 24
        h = floor(jf)
        jf = (jf - h) * 60
        n = floor(jf)
        s = ((jf - n) * 60).toInt().toDouble()

        return WesternDate(
            year = y.toInt(),
            month = m.toInt(),
            day = d.toInt(),
            hour = h.toInt(),
            minute = n.toInt(),
            second = s.toInt()
        )
    }

    fun westernToJulian(
        year: Int,
        month: Int,
        day: Int,
        calType: Int = 0,
        sg: Double = 2361222.0
    ): Double {
        val cleanCalType = calType.coerceAtLeast(0)

        val a = floor((14 - month) / 12.0)
        val y = year + 4800 - a
        val m = month + (12 * a).toInt() - 3

        var jd = day + floor((153 * m + 2) / 5.0) + 365 * y + floor(y / 4.0)

        when (cleanCalType) {
            1 -> jd = jd - floor(y / 100.0) + floor(y / 400.0) - 32045
            2 -> jd = jd - 32083
            else -> {
                jd = jd - floor(y / 100.0) + floor(y / 400.0) - 32045
                if (jd < sg) {
                    jd = day + floor((153.0 * m + 2) / 5) + 365 * y + floor(y / 4.0) - 32083
                    if (jd > sg) jd = sg
                }
            }
        }

        return jd
    }

    fun westernToJulian(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
        calendarType: CalendarType,
        sg: Double = 2361222.0
    ): Double {
        val timeFraction = timeToDayFraction(hour, minute, second)
        return westernToJulian(year, month, day, calendarType.number, sg) + timeFraction
    }

    fun getLengthOfMonth(year: Int, month: Int, calenderType: Int): Int {
        var leap = 0
        var mLen = (30 + (month + floor(month / 8.0)) % 2).toInt()

        if (month == 2) {
            when {
                calenderType == 1 || (calenderType == 0 && year > 1752) -> {
                    if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) leap = 1
                }
                year % 4 == 0 -> leap = 1
            }
            mLen += leap - 2
        }

        if (year == 1752 && month == 9 && calenderType == 0) {
            mLen = 19
        }

        return mLen
    }

    private fun timeToDayFraction(hour: Int, minute: Int, second: Int): Double {
        return ((hour - 12) / 24.0 + minute / 1440.0 + second / 86400.0)
    }
}