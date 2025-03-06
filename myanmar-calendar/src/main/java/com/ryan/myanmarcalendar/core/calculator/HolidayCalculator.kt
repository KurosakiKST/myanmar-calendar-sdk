package com.ryan.myanmarcalendar.core.calculator

import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.core.kernel.BinarySearchUtil
import com.ryan.myanmarcalendar.core.kernel.WesternDateKernel
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
import com.ryan.myanmarcalendar.data.model.CalendarType
import com.ryan.myanmarcalendar.data.model.Language
import com.ryan.myanmarcalendar.data.model.MonthType
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import com.ryan.myanmarcalendar.data.model.WesternDate
import kotlin.math.floor

object HolidayCalculator {

    // Eid (ghEid2)
    private val GH_EID_2 = intArrayOf(2456936, 2457290, 2457644, 2457998, 2458353)

    // Chinese New Year (ghCNY)
    private val GH_CHINESE_NEW_YEAR = intArrayOf(
        2456689, 2456690, 2457073, 2457074, 2457427, 2457428, 2457782,
        2457783, 2458166, 2458520, 2458874, 2459257, 2459612, 2459967, 2460351,
        2460705, 2461089, 2461443, 2461797, 2462181, 2462536
    )

    // Diwali (ghDiwali)
    private val GH_DIWALI = intArrayOf(
        2456599, 2456953, 2457337, 2457691, 2458045, 2458430, 2458784, 2459168,
        2459523, 2459877
    )

    // EID ghEid
    private val GH_EID = intArrayOf(
        2456513, 2456867, 2457221, 2457576, 2457930, 2458285, 2458640, 2459063,
        2459416, 2459702, 2460125, 2460261
    )

    // Substitute holidays
    private val SUBSTITUTE_HOLIDAY = intArrayOf(
        // 2019
        2458768, 2458772, 2458785, 2458800,
        // 2020
        2458855, 2458918, 2458950, 2459051, 2459062,
        2459152, 2459156, 2459167, 2459181, 2459184,
        // 2021
        2459300, 2459303, 2459323, 2459324,
        2459335, 2459548, 2459573
    )

    /**
     * Check for English holidays
     */
    fun englishHoliday(gy: Int, gm: Int, gd: Int): List<String> {
        val holidays = mutableListOf<String>()

        if ((gy >= 2018 && gy <= 2021) || (gy >= 2025) && gm == 1 && gd == 1) {
            holidays.add("New Year's Day")
        } else if (gy >= 1948 && gm == 1 && gd == 4) {
            holidays.add("Independence Day")
        } else if (gy >= 1947 && gm == 2 && gd == 12) {
            holidays.add("Union Day")
        } else if (gy >= 1958 && gm == 3 && gd == 2) {
            holidays.add("Peasants' Day")
        } else if (gy >= 1945 && gm == 3 && gd == 27) {
            holidays.add("Resistance Day")
        } else if (gy >= 1923 && gm == 5 && gd == 1) {
            holidays.add("Labour Day")
        } else if (gy >= 1947 && gm == 7 && gd == 19) {
            holidays.add("Martyrs' Day")
        } else if (gm == 12 && gd == 25) {
            holidays.add("Christmas Day")
        } else if (gy == 2017 && gm == 12 && gd == 30) {
            holidays.add("Holiday")
        } else if ((gy >= 2017 && gy <= 2021) && gm == 12 && gd == 31) {
            holidays.add("Holiday")
        }

        return holidays
    }

    /**
     * Check for Myanmar holidays
     */
    fun myanmarHoliday(myear: Double, mmonth: Int, monthDay: Int, moonPhase: Int): List<String> {
        val holidays = mutableListOf<String>()

        if ((mmonth == 2) && (moonPhase == 1)) {
            holidays.add("Buddha Day")
        } else if ((mmonth == 4) && (moonPhase == 1)) {
            holidays.add("Start of Buddhist Lent")
        } else if ((mmonth == 7) && (moonPhase == 1)) {
            holidays.add("End of Buddhist Lent")
        } else if ((myear >= 1379) && (mmonth == 7) && (monthDay == 14 || monthDay == 16)) {
            holidays.add("Holiday")
        } else if ((mmonth == 8) && (moonPhase == 1)) {
            holidays.add("Tazaungdaing")
        } else if ((myear >= 1379 && myear <= 1385) && (mmonth == 8) && (monthDay == 14)) {
            holidays.add("Holiday")
        } else if ((myear >= 1282) && (mmonth == 8) && (monthDay == 25)) {
            holidays.add("National Day")
        } else if ((mmonth == 10) && (monthDay == 1)) {
            holidays.add("Karen New Year's Day")
        } else if ((mmonth == 12) && (moonPhase == 1)) {
            holidays.add("Tabaung Pwe")
        }

        return holidays
    }

    /**
     * Thingyan (Myanmar New Year) holidays
     */
    fun thingyan(jdn: Double, myear: Double, monthType: Int): List<String> {
        // start of Thingyan (BGNTG)
        val bgntg = 1100

        val holidays = mutableListOf<String>()

        val aknTime: Double
        val atnTime: Double
        // start of third era
        val SE3 = 1312

        val ja = myear * 365.2587564814815 + monthType * 365.2587564814815 + 1954168.050623
        val jk: Double

        if (myear >= SE3) {
            jk = ja - 2.169918982
        } else {
            jk = ja - 2.1675
        }

        aknTime = Math.round(jk).toDouble()
        atnTime = Math.round(ja).toDouble()

        if (Math.abs(jdn - (atnTime + 1)) < 0.0000001) {
            holidays.add("Myanmar New Year's Day")
        }

        if ((myear + monthType) >= bgntg) {
            if (jdn == atnTime) {
                holidays.add("Thingyan Atat")
            } else if ((jdn > aknTime) && (jdn < atnTime)) {
                holidays.add("Thingyan Akyat")
            } else if (jdn == aknTime) {
                holidays.add("Thingyan Akya")
            } else if (jdn == (aknTime - 1)) {
                holidays.add("Thingyan Akyo")
            } else if (((myear + monthType) >= 1369) && ((myear + monthType) < 1379)
                && ((jdn == (aknTime - 2)) || ((jdn >= (atnTime + 2)) && (jdn <= (aknTime + 7))))) {
                holidays.add("Holiday")
            } else if ((((myear + monthType) >= 1384) && (myear + monthType) <= 1385)
                && ((jdn == (aknTime - 5)) || (jdn == (aknTime - 4)) || (jdn == (aknTime - 3)) || (jdn == (aknTime - 2)))) {
                holidays.add("Holiday")
            } else if ((myear + monthType) >= 1386
                && (((jdn >= (atnTime + 2)) && (jdn <= (aknTime + 7))))) {
                holidays.add("Holiday")
            }
        }

        return holidays
    }

    /**
     * Other holidays (Diwali, Eid, etc.)
     */
    fun getOtherHolidays(jd: Double): List<String> {
        val holidays = mutableListOf<String>()

        if (BinarySearchUtil.search(jd, GH_DIWALI) >= 0) {
            holidays.add("Diwali")
        }
        if (BinarySearchUtil.search(jd, GH_EID) >= 0) {
            holidays.add("Eid")
        }
        if (jd > 2460677 && BinarySearchUtil.search(jd, GH_CHINESE_NEW_YEAR) >= 0) {
            holidays.add("Chinese New Year's")
        }

        return holidays
    }

    /**
     * Substitute holiday list
     */
    fun getSubstituteHoliday(jd: Double): List<String> {
        val holidays = mutableListOf<String>()

        if (BinarySearchUtil.search(jd, SUBSTITUTE_HOLIDAY) >= 0) {
            holidays.add("Holiday")
        }

        return holidays
    }

    /**
     * Anniversary days
     */
    fun getAnniversaryDay(jd: Double, calendarType: CalendarType = CalendarType.ENGLISH): List<String> {
        val anniversaries = mutableListOf<String>()

        val wd = WesternDate.of(jd, calendarType)
        val doe = dateOfEaster(wd.year)

        if ((wd.year <= 2017) && (wd.month == 1) && (wd.day == 1)) {
            anniversaries.add("New Year Day")
        } else if ((wd.year >= 1915) && (wd.month == 2) && (wd.day == 13)) {
            anniversaries.add("G. Aung San BD")
        } else if ((wd.year >= 1969) && (wd.month == 2) && (wd.day == 14)) {
            anniversaries.add("Valentines Day")
        } else if ((wd.year >= 1970) && (wd.month == 4) && (wd.day == 22)) {
            anniversaries.add("Earth Day")
        } else if ((wd.year >= 1392) && (wd.month == 4) && (wd.day == 1)) {
            anniversaries.add("April Fools' Day")
        } else if ((wd.year >= 1948) && (wd.month == 5) && (wd.day == 8)) {
            anniversaries.add("Red Cross Day")
        } else if ((wd.year >= 1994) && (wd.month == 10) && (wd.day == 5)) {
            anniversaries.add("World Teachers' Day")
        } else if ((wd.year >= 1947) && (wd.month == 10) && (wd.day == 24)) {
            anniversaries.add("United Nations Day")
        } else if ((wd.year >= 1753) && (wd.month == 10) && (wd.day == 31)) {
            anniversaries.add("Halloween")
        }

        if ((wd.year >= 1876) && (jd == doe)) {
            anniversaries.add("Easter")
        } else if ((wd.year >= 1876) && (jd == (doe - 2))) {
            anniversaries.add("Good Friday")
        } else if (BinarySearchUtil.search(jd, GH_EID_2) >= 0) {
            anniversaries.add("Eid")
        }

        if (BinarySearchUtil.search(jd, GH_CHINESE_NEW_YEAR) >= 0) {
            anniversaries.add("Chinese New Year's")
        }

        return anniversaries
    }

    /**
     * Myanmar anniversary days
     */
    fun getMyanmarAnniversaryDay(myear: Double, mmonth: Int, monthDay: Int, moonPhase: Int): List<String> {
        val anniversaries = mutableListOf<String>()

        if ((myear >= 1309) && (mmonth == 11) && (monthDay == 16)) {
            anniversaries.add("'Mon' National Day")
        } else if ((mmonth == 9) && (monthDay == 1)) {
            anniversaries.add("Shan New Year's Day")
            if (myear >= 1306) {
                anniversaries.add("Authors' Day")
            }
        } else if ((mmonth == 3) && (moonPhase == 1)) {
            anniversaries.add("Mahathamaya Day")
        } else if ((mmonth == 6) && (moonPhase == 1)) {
            anniversaries.add("Garudhamma Day")
        } else if ((myear >= 1356) && (mmonth == 10) && (moonPhase == 1)) {
            anniversaries.add("Mothers' Day")
        } else if ((myear >= 1370) && (mmonth == 12) && (moonPhase == 1)) {
            anniversaries.add("Fathers' Day")
        } else if ((mmonth == 5) && (moonPhase == 1)) {
            anniversaries.add("Metta Day")
        } else if ((mmonth == 5) && (monthDay == 10)) {
            anniversaries.add("Taungpyone Pwe")
        } else if ((mmonth == 5) && (monthDay == 23)) {
            anniversaries.add("Yadanagu Pwe")
        }

        return anniversaries
    }

    /**
     * Calculate date of Easter using "Meeus/Jones/Butcher" algorithm
     */
    private fun dateOfEaster(year: Int): Double {
        val a = year % 19
        val b = floor(year / 100.0)
        val c = year % 100
        val d = floor(b / 4)
        val e = b % 4
        val f = floor((b + 8) / 25)
        val g = floor((b - f + 1) / 3)
        val h = (19 * a + b - d - g + 15) % 30
        val i = floor(c / 4.0)
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = floor((a + 11 * h + 22 * l) / 451)
        val q = h + l - 7 * m + 114
        val day = ((q % 31) + 1).toInt()
        val month = floor(q / 31.0).toInt()

        // This is for Gregorian calendar
        return WesternDateKernel.westernToJulian(year, month, day, 1)
    }

    /**
     * Get all holidays for a given Myanmar date
     */
    fun getHoliday(myanmarDate: MyanmarDate, language: Language = CalendarConfig.getInstance().language): List<String> {
        return getHoliday(myanmarDate, CalendarConfig.getInstance().calendarType, language)
    }

    /**
     * Get all holidays for a given Myanmar date with specified calendar type and language
     */
    fun getHoliday(myanmarDate: MyanmarDate, calendarType: CalendarType, language: Language): List<String> {
        val westernDate = WesternDate.of(myanmarDate.toJulian(), calendarType)

        // Office holidays
        val englishHolidays = englishHoliday(westernDate.year, westernDate.month, westernDate.day)
        val myanmarHolidays = myanmarHoliday(myanmarDate.year.toDouble(), myanmarDate.month, myanmarDate.day, myanmarDate.moonPhase)

        // Determine the month type value for Thingyan calculations
        val monthTypeValue = when (myanmarDate.monthType) {
            MonthType.REGULAR -> 0
            MonthType.INTERCALARY -> 1
            MonthType.SECOND_WASO -> 0
        }
        val thingyanHolidays = thingyan(myanmarDate.toJulian(), myanmarDate.year.toDouble(), monthTypeValue)
        val otherHolidays = getOtherHolidays(myanmarDate.toJulian())

        val allHolidays = mutableListOf<String>()

        // Translate holiday names to desired language
        allHolidays.addAll(LanguageTranslator.translateSentenceList(englishHolidays, Language.ENGLISH, language))
        allHolidays.addAll(LanguageTranslator.translateSentenceList(myanmarHolidays, Language.ENGLISH, language))
        allHolidays.addAll(LanguageTranslator.translateSentenceList(thingyanHolidays, Language.ENGLISH, language))
        allHolidays.addAll(LanguageTranslator.translateSentenceList(otherHolidays, Language.ENGLISH, language))

        // Add substitute holidays for years 2019-2021
        if (westernDate.year in 2019..2021) {
            val substituteHolidays = getSubstituteHoliday(myanmarDate.toJulian())
            allHolidays.addAll(LanguageTranslator.translateSentenceList(substituteHolidays, Language.ENGLISH, language))
        }

        return allHolidays
    }

    /**
     * Check if a date is a holiday
     */
    fun isHoliday(myanmarDate: MyanmarDate): Boolean {
        return getHoliday(myanmarDate).isNotEmpty()
    }

    /**
     * Get anniversaries for a given Myanmar date
     */
    fun getAnniversary(myanmarDate: MyanmarDate, language: Language = CalendarConfig.getInstance().language): List<String> {
        return getAnniversary(myanmarDate, CalendarConfig.getInstance().calendarType, language)
    }

    /**
     * Get anniversaries for a given Myanmar date with specified calendar type and language
     */
    fun getAnniversary(myanmarDate: MyanmarDate, calendarType: CalendarType, language: Language): List<String> {
        val englishAnniversaries = getAnniversaryDay(myanmarDate.toJulian(), calendarType)
        val myanmarAnniversaries = getMyanmarAnniversaryDay(
            myanmarDate.year.toDouble(),
            myanmarDate.month,
            myanmarDate.day,
            myanmarDate.moonPhase
        )

        val allAnniversaries = mutableListOf<String>()

        // Translate anniversary names
        allAnniversaries.addAll(LanguageTranslator.translateSentenceList(englishAnniversaries, Language.ENGLISH, language))
        allAnniversaries.addAll(LanguageTranslator.translateSentenceList(myanmarAnniversaries, Language.ENGLISH, language))

        return allAnniversaries
    }
}