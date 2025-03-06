package com.ryan.myanmarcalendar.data.model

import com.ryan.myanmarcalendar.core.constants.CalendarConstants
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.round

/**
 * Represents Thingyan (Myanmar New Year) calculations and information
 */
class Thingyan private constructor(
    // Atat Time (သင်္ကြန်တက်ချိန်)
    val atatTime: Double,

    // Akya Time (သင်္ကြန်ကျချိန်)
    val akyaTime: Double,

    // Atat Day (သင်္ကြန်အတက်နေ့)
    val atatDay: Double,

    // Akya Day (အကျနေ့)
    val akyaDay: Double
) : Serializable {

    /**
     * Thingyan Akyo day (သင်္ကြန်အကြိုနေ့)
     */
    fun getAkyoDay(): Double = akyaDay - 1

    /**
     * Thingyan Akyat days (အကြတ်နေ့)
     * Returns one or two days depending on the year
     */
    fun getAkyatDay(): DoubleArray {
        return if ((atatDay - akyaDay) > 2) {
            doubleArrayOf(akyaDay + 1, akyaDay + 2)
        } else {
            doubleArrayOf(akyaDay + 1)
        }
    }

    /**
     * Myanmar New Year's Day (နှစ်ဆန်းတစ်ရက်နေ့)
     */
    fun getMyanmarNewYearDay(): Double = atatDay + 1

    companion object {
        /**
         * Calculate the Thingyan (Myanmar new year) for a given Myanmar year
         *
         * @param myear Myanmar year
         * @return Thingyan object with calculated dates
         * @throws IllegalArgumentException if the year is before 1100 ME
         */
        fun of(myear: Int): Thingyan {
            // start of Thingyan (BGNTG)
            val bgntg = 1100
            if (myear < bgntg) {
                throw IllegalArgumentException("Thingyan calculations start from $bgntg Myanmar year")
            }

            // Atat Time (သင်္ကြန်တက်ချိန်)
            val ja = CalendarConstants.SY * myear + CalendarConstants.MO

            // Akya Time (သင်္ကြန်ကျချိန်)
            val jk = if (myear >= CalendarConstants.SE3) {
                ja - 2.169918982
            } else {
                ja - 2.1675
            }

            // Atat Day (သင်္ကြန်အတက်နေ့)
            val da = round(ja)

            // Akya Day (အကျနေ့)
            val dk = round(jk)

            return Thingyan(ja, jk, da, dk)
        }
    }

    override fun toString(): String {
        return "Thingyan [Atat Time = $atatTime, Akya Time = $akyaTime, Atat Day = $atatDay, Akya Day = $akyaDay]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Thingyan) return false

        if (abs(atatTime - other.atatTime) > 0.0000001) return false
        if (abs(akyaTime - other.akyaTime) > 0.0000001) return false
        if (abs(atatDay - other.atatDay) > 0.0000001) return false
        if (abs(akyaDay - other.akyaDay) > 0.0000001) return false

        return true
    }

    override fun hashCode(): Int {
        var result = atatTime.hashCode()
        result = 31 * result + akyaTime.hashCode()
        result = 31 * result + atatDay.hashCode()
        result = 31 * result + akyaDay.hashCode()
        return result
    }
}