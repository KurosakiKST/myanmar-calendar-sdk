package com.ryan.mmcalendar.util

import android.util.Log
import com.ryan.myanmarcalendar.api.MyanmarCalendar
import com.ryan.myanmarcalendar.data.model.Astro
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import java.util.*

/**
 * Utility class for diagnosing Myanmar Calendar issues
 */
object DiagnosticUtil {

    private const val TAG = "MyanmarCalendar"

    /**
     * Logs complete information about a Myanmar date
     */
    fun logMyanmarDate(date: Date, calendar: MyanmarCalendar) {
        try {
            val myanmarDate = calendar.convertToMyanmarDate(date)

            Log.d(TAG, "=== Myanmar Date Diagnostic ===")
            Log.d(TAG, "Western Date: ${date.toLocaleString()}")
            Log.d(TAG, "Myanmar Year: ${myanmarDate.year}")
            Log.d(TAG, "Myanmar Month: ${myanmarDate.month}")
            Log.d(TAG, "Myanmar Month Name: ${myanmarDate.getMonthName()}")
            Log.d(TAG, "Myanmar Day: ${myanmarDate.day}")
            Log.d(TAG, "Moon Phase: ${myanmarDate.moonPhase}")
            Log.d(TAG, "Fortnight Day: ${myanmarDate.fortnightDay}")
            Log.d(TAG, "Week Day: ${myanmarDate.weekDay}")

            // Get astrological information
            val astro = calendar.getAstrological(date)
            logAstroInfo(astro)

        } catch (e: Exception) {
            Log.e(TAG, "Error in logMyanmarDate", e)
        }
    }

    /**
     * Logs astrological information
     */
    fun logAstroInfo(astro: Astro) {
        Log.d(TAG, "=== Astrological Information ===")
        Log.d(TAG, "Yatyaza: ${astro.isYatyaza()}")
        Log.d(TAG, "Pyathada: ${astro.isPyathada()} (value: ${astro.getPyathadaValue()})")
        Log.d(TAG, "Sabbath: ${astro.isSabbath()}")
        Log.d(TAG, "Sabbath Eve: ${astro.isSabbathEve()}")
        Log.d(TAG, "Thamanyo: ${astro.isThamanyo()}")
        Log.d(TAG, "Thamaphyu: ${astro.isThamaphyu()}")
        Log.d(TAG, "Nagapor: ${astro.isNagapor()}")
        Log.d(TAG, "Yatyotema: ${astro.isYatyotema()}")
        Log.d(TAG, "Mahayatkyan: ${astro.isMahayatkyan()}")
        Log.d(TAG, "Shanyat: ${astro.isShanyat()}")
    }

    /**
     * Logs calendar result information for a month
     */
    fun logCalendarMonth(year: Int, month: Int, calendar: MyanmarCalendar) {
        try {
            val cal = Calendar.getInstance()
            cal.set(year, month, 1)
            val result = calendar.getMonthCalendar(cal.time.time)

            Log.d(TAG, "=== Calendar Month Diagnostic ===")
            Log.d(TAG, "Year: $year, Month: ${month + 1}")
            Log.d(TAG, "Gregorian Month: ${result.gregorianMonth}")
            Log.d(TAG, "Myanmar Month: ${result.myanmarMonth}")

            // Log specific days
            result.days.forEach { dayInfo ->
                if (dayInfo.gregorianDay > 0) {
                    Log.d(TAG, "Day ${dayInfo.gregorianDay}: " +
                            "Myanmar Month=${dayInfo.myanmarMonth}, " +
                            "Myanmar Day=${dayInfo.myanmarDay}, " +
                            "Moon Phase=${dayInfo.moonPhase}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in logCalendarMonth", e)
        }
    }

    /**
     * Logs holiday information for a specific date
     */
    fun logHolidays(date: Date, calendar: MyanmarCalendar) {
        try {
            val myanmarDate = calendar.convertToMyanmarDate(date)
            val holidays = com.ryan.myanmarcalendar.core.calculator.HolidayCalculator.getHoliday(myanmarDate)

            Log.d(TAG, "=== Holiday Diagnostic ===")
            Log.d(TAG, "Western Date: ${date.toLocaleString()}")
            Log.d(TAG, "Holiday Count: ${holidays.size}")
            holidays.forEachIndexed { index, holiday ->
                Log.d(TAG, "Holiday ${index + 1}: $holiday")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in logHolidays", e)
        }
    }
}