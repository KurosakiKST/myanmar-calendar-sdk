package com.ryan.myanmarcalendar.data.model

import java.io.Serializable
import java.util.*

/**
 * Provides Myanmar date information for all Thingyan dates
 */
class MyanmarThingyanDateTime private constructor(
    /**
     * Thingyan Akyo day (သင်္ကြန်အကြိုနေ့)
     */
    val akyoDay: MyanmarDate,

    /**
     * Akya time (သင်္ကြန်ကျချိန်)
     */
    val akyaTime: MyanmarDate,

    /**
     * Akya day (အကျနေ့)
     */
    val akyaDay: MyanmarDate,

    /**
     * Atat Time (သင်္ကြန်တက်ချိန်)
     */
    val atatTime: MyanmarDate,

    /**
     * Atat day (သင်္ကြန်အတက်နေ့)
     */
    val atatDay: MyanmarDate,

    /**
     * Thingyan Akyat days (အကြတ်နေ့)
     */
    val akyatDays: Array<MyanmarDate>,

    /**
     * Myanmar New Year's Day (နှစ်ဆန်းတစ်ရက်နေ့)
     */
    val myanmarNewYearDay: MyanmarDate
) : Serializable {

    companion object {
        /**
         * Creates a MyanmarThingyanDateTime instance for a specific Myanmar year
         *
         * @param myear Myanmar year
         * @return MyanmarThingyanDateTime with all Thingyan date information
         */
        fun of(myear: Int): MyanmarThingyanDateTime {
            val thingyan = Thingyan.of(myear)

            val akyoDay = MyanmarDate.of(thingyan.getAkyoDay())
            val akyaTime = MyanmarDate.of(thingyan.akyaTime)
            val akyaDay = MyanmarDate.of(thingyan.akyaDay)
            val atatTime = MyanmarDate.of(thingyan.atatTime)
            val atatDay = MyanmarDate.of(thingyan.atatDay)

            val akyatDayJds = thingyan.getAkyatDay()
            val akyatDays = if (akyatDayJds.size > 1) {
                arrayOf(
                    MyanmarDate.of(akyatDayJds[0]),
                    MyanmarDate.of(akyatDayJds[1])
                )
            } else {
                arrayOf(MyanmarDate.of(akyatDayJds[0]))
            }

            val myanmarNewYearDay = MyanmarDate.of(thingyan.getMyanmarNewYearDay())

            return MyanmarThingyanDateTime(
                akyoDay, akyaTime, akyaDay, atatTime, atatDay, akyatDays, myanmarNewYearDay
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MyanmarThingyanDateTime) return false

        if (akyoDay != other.akyoDay) return false
        if (akyaTime != other.akyaTime) return false
        if (akyaDay != other.akyaDay) return false
        if (atatTime != other.atatTime) return false
        if (atatDay != other.atatDay) return false
        if (!akyatDays.contentEquals(other.akyatDays)) return false
        if (myanmarNewYearDay != other.myanmarNewYearDay) return false

        return true
    }

    override fun hashCode(): Int {
        var result = akyoDay.hashCode()
        result = 31 * result + akyaTime.hashCode()
        result = 31 * result + akyaDay.hashCode()
        result = 31 * result + atatTime.hashCode()
        result = 31 * result + atatDay.hashCode()
        result = 31 * result + akyatDays.contentHashCode()
        result = 31 * result + myanmarNewYearDay.hashCode()
        return result
    }

    override fun toString(): String {
        return "MyanmarThingyanDateTime(akyoDay=$akyoDay, akyaTime=$akyaTime, akyaDay=$akyaDay, " +
                "atatTime=$atatTime, atatDay=$atatDay, akyatDays=${Arrays.toString(akyatDays)}, " +
                "myanmarNewYearDay=$myanmarNewYearDay)"
    }
}