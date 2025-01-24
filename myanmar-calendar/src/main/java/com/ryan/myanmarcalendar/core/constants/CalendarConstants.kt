package com.ryan.myanmarcalendar.core.constants

import java.time.ZoneId

object CalendarConstants {
    const val SY = 365.2587564814815 // Solar year
    const val LM = 29.53058794607172 // Lunar month
    const val MO = 1954168.050623 // Beginning of 0 ME
    const val SE3 = 1312 // Beginning of 3rd era
    const val BY = 640 // Beginning of English Calendar
    const val EY = 2140 // End of English Calendar
    const val MBY = 2 // Beginning of Myanmar Calendar
    const val MEY = 1500 // End of Myanmar Calendar
    const val LT = 1700 // Min accurate English Year
    const val UT = 2018 // Max accurate English Year
    const val MLT = 1062 // Min accurate Myanmar Year
    const val MUT = 1379 // Max accurate Myanmar Year
    const val SG = 2361222.0 // Gregorian start (1752/Sep/14)

    val MYANMAR_ZONE_ID = ZoneId.of("Asia/Rangoon")

    val EMA = arrayOf(
        "First Waso", "Tagu", "Kason", "Nayon", "Waso",
        "Wagaung", "Tawthalin", "Thadingyut", "Tazaungmon",
        "Nadaw", "Pyatho", "Tabodwe", "Tabaung", "Late Tagu", "Late Kason"
    )

    val MSA = arrayOf("Waxing", "Full Moon", "Waning", "New Moon")

    val WDA = arrayOf(
        "Saturday", "Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday"
    )
}