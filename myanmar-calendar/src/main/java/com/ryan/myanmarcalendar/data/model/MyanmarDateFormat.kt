package com.ryan.myanmarcalendar.data.model

/**
 * Provides constants and patterns for Myanmar date formatting
 */
object MyanmarDateFormat {
    /**
     * Sasana Year format character
     */
    const val SASANA_YEAR = 'S'

    /**
     * Buddhist Era format character
     */
    const val BUDDHIST_ERA = 's'

    /**
     * Burmese Year format character
     */
    const val BURMESE_YEAR = 'B'

    /**
     * Myanmar Year format character
     */
    const val MYANMAR_YEAR = 'y'

    /**
     * Ku (year suffix) format character
     */
    const val KU = 'k'

    /**
     * Month format character
     */
    const val MONTH_IN_YEAR = 'M'

    /**
     * Moon phase format character
     */
    const val MOON_PHASE = 'p'

    /**
     * Fortnight day format character
     */
    const val FORTNIGHT_DAY = 'f'

    /**
     * Day name (weekday) format character
     */
    const val DAY_NAME_IN_WEEK = 'E'

    /**
     * Nay (day) format character
     */
    const val NAY = 'n'

    /**
     * Yat (day suffix) format character
     */
    const val YAT = 'r'

    /**
     * Default pattern for Myanmar date formatting
     */
    const val SIMPLE_MYANMAR_DATE_FORMAT_PATTERN = "S s k, B y k, M p f r E n"
}