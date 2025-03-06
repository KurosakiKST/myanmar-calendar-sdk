package com.ryan.myanmarcalendar.core.kernel

import kotlin.math.floor

internal object AstroKernel {
    /**
     * Calculate Thamaphyu (သမားဖြူ)
     * Returns 1 if Thamaphyu, 0 otherwise
     */
    fun calculateThamaphyu(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(1, 2, 6, 6, 5, 6, 7)
        val wdb = intArrayOf(0, 1, 0, 0, 0, 3, 3)

        return if (mf == wda[weekDay] || mf == wdb[weekDay] || (mf == 4 && weekDay == 5)) 1 else 0
    }

    /**
     * Calculate Nagapor (နဂါးပေါ်)
     * Returns 1 if Nagapor, 0 otherwise
     */
    fun calculateNagapor(md: Int, weekDay: Int): Int {
        val wda = intArrayOf(26, 21, 2, 10, 18, 2, 21)
        val wdb = intArrayOf(17, 19, 1, 0, 9, 0, 0)

        return if (md == wda[weekDay] || md == wdb[weekDay] ||
            ((md == 2 && weekDay == 1) || ((md == 12 || md == 4 || md == 18) && weekDay == 2))) 1 else 0
    }

    /**
     * Calculate Yatyotema (ရက်ယုတ်မာ)
     * Returns 1 if Yatyotema, 0 otherwise
     */
    fun calculateYatyotema(mmonth: Int, md: Int): Int {
        val mmt = mmonth / 13
        var month = mmonth % 13 + mmt
        if (month <= 0) month = 4

        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val m1 = if (month % 2 != 0) month else (month + 9) % 12
        val targetDay = (m1 + 4) % 12 + 1

        return if (mf == targetDay) 1 else 0
    }

    /**
     * Calculate Mahayatkyan (မဟာရက်ကြမ်း)
     * Returns 1 if Mahayatkyan, 0 otherwise
     */
    fun calculateMahayatkyan(mmonth: Int, md: Int): Int {
        var month = mmonth
        if (month <= 0) month = 4

        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val m1 = (((month % 12) / 2) + 4) % 6 + 1

        return if (mf == m1) 1 else 0
    }

    /**
     * Calculate Shanyat (ရှမ်းရက်)
     * Returns 1 if Shanyat, 0 otherwise
     */
    fun calculateShanyat(mmonth: Int, md: Int): Int {
        val mmt = mmonth / 13
        var month = mmonth % 13 + mmt
        if (month <= 0) month = 4

        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val sya = intArrayOf(8, 8, 2, 2, 9, 3, 3, 5, 1, 4, 7, 4)

        return if (mf == sya[month - 1]) 1 else 0
    }

    /**
     * Calculate Sabbath status
     * Returns:
     * 1 = Sabbath
     * 2 = Sabbath Eve
     * 0 = Neither
     */
    fun calculateSabbath(yearType: Int, mmonth: Int, md: Int): Int {
        val mml = MyanmarCalendarKernel.calculateLengthOfMonth(mmonth, yearType)

        return when {
            md == 8 || md == 15 || md == 23 || md == mml -> 1  // Sabbath
            md == 7 || md == 14 || md == 22 || md == (mml - 1) -> 2  // Sabbath Eve
            else -> 0  // Neither
        }
    }

    /**
     * Calculate Yatyaza (ရက်ရာဇာ)
     * Returns 1 if Yatyaza, 0 otherwise
     */
    fun calculateYatyaza(mm: Int, weekDay: Int): Int {
        // Handle month 0 (First Waso)
        val month = if (mm <= 0) 4 else mm

        val m1 = month % 4
        val wd1 = (m1 / 2) + 4
        val wd2 = ((1 - (m1 / 2)) + m1 % 2) * (1 + 2 * (m1 % 2))

        return if (weekDay == wd1 || weekDay == wd2) 1 else 0
    }

    /**
     * Calculate Pyathada (ပြဿဒါး)
     * Returns:
     * 1 = Pyathada
     * 2 = Afternoon Pyathada
     * 0 = No Pyathada
     */
    fun calculatePyathada(mmonth: Int, weekDay: Int): Int {
        // Handle month 0 (First Waso)
        val month = if (mmonth <= 0) 4 else mmonth

        val m1 = month % 4
        val wda = intArrayOf(1, 3, 3, 0, 2, 1, 2)

        return when {
            m1 == wda[weekDay] -> 1  // Pyathada
            m1 == 0 && weekDay == 4 -> 2  // Afternoon Pyathada
            else -> 0  // No Pyathada
        }
    }

    /**
     * Calculate Nagahle (နဂါးခေါင်း လှည့်ရာ အရပ်)
     * Returns:
     * 0 = west
     * 1 = north
     * 2 = east
     * 3 = south
     */
    fun calculateNagahle(mmonth: Int): Int {
        val month = if (mmonth <= 0) 4 else mmonth
        return (month % 12) / 3
    }

    /**
     * Calculate Mahabote (မဟာဘုတ်)
     * Returns:
     * 0 = Binga
     * 1 = Atun
     * 2 = Yaza
     * 3 = Adipati
     * 4 = Marana
     * 5 = Thike
     * 6 = Puti
     */
    fun calculateMahabote(myear: Int, weekDay: Int): Int {
        return (myear - weekDay) % 7
    }

    /**
     * Calculate Nakhat (နက္ခတ်)
     * Returns:
     * 0 = Ogre (ဘီလူး)
     * 1 = Elf (နတ်)
     * 2 = Human (လူ)
     */
    fun calculateNakhat(myear: Int): Int {
        return myear % 3
    }

    /**
     * Calculate Thamanyo (သမားညို)
     * Returns 1 if Thamanyo, 0 otherwise
     */
    fun calculateThamanyo(mmonth: Int, weekDay: Int): Int {
        val mmt = mmonth / 13
        var month = mmonth % 13 + mmt
        if (month <= 0) month = 4

        val m1 = month - 1 - (month / 9)
        val wd1 = (m1 * 2 - (m1 / 8)) % 7
        val wd2 = (weekDay + 7 - wd1) % 7

        return if (wd2 <= 1) 1 else 0
    }

    /**
     * Calculate Amyeittasote (အမြိတ္တစုတ်)
     * Returns 1 if Amyeittasote, 0 otherwise
     */
    fun calculateAmyeittasote(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(5, 8, 3, 7, 2, 4, 1)
        return if (mf == wda[weekDay]) 1 else 0
    }

    /**
     * Calculate Warameittugyi (ဝါရမိတ္တုကြီး)
     * Returns 1 if Warameittugyi, 0 otherwise
     */
    fun calculateWarameittugyi(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(7, 1, 4, 8, 9, 6, 3)
        return if (mf == wda[weekDay]) 1 else 0
    }

    /**
     * Calculate Warameittunge (ဝါရမိတ္တုငယ်)
     * Returns 1 if Warameittunge, 0 otherwise
     */
    fun calculateWarameittunge(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wn = (weekDay + 6) % 7
        return if ((12 - mf) == wn) 1 else 0
    }

    /**
     * Calculate Yatpote (ရက်ပုပ်)
     * Returns 1 if Yatpote, 0 otherwise
     */
    fun calculateYatpote(md: Int, weekDay: Int): Int {
        val mf = MyanmarCalendarKernel.calculateFortnightDay(md)
        val wda = intArrayOf(8, 1, 4, 6, 9, 8, 7)
        return if (mf == wda[weekDay]) 1 else 0
    }

    /**
     * Calculate Year Name (Year zodiac sign)
     * Returns 0-11 for the Myanmar year name
     */
    fun calculateYearName(myear: Int): Int {
        return myear % 12
    }
}