package com.ryan.myanmarcalendar.data.model

import com.ryan.myanmarcalendar.core.kernel.MyanmarCalendarKernel
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
import java.io.Serializable

data class MyanmarMonths(
    val monthList: List<Int>,
    val monthNameList: List<String>,
    val calculationMonth: Int
) : Serializable {

    fun getMonthNameList(language: Language = Language.ENGLISH): List<String> {
        return if (language == Language.ENGLISH) {
            monthNameList
        } else {
            monthNameList.map { monthName ->
                LanguageTranslator.translateSentence(monthName, Language.ENGLISH, language)
            }
        }
    }

    fun getCalculationMonthName(): String {
        val index = monthList.indexOf(calculationMonth)
        return monthNameList[index]
    }

    fun getCalculationMonthIndex(): Int {
        return monthList.indexOf(calculationMonth)
    }

    companion object {
        fun of(myear: Int, mmonth: Int): MyanmarMonths {
            return MyanmarCalendarKernel.calculateRelatedMyanmarMonths(myear, mmonth)
        }
    }
}