package com.ryan.myanmarcalendar.core.translator

import com.ryan.myanmarcalendar.data.model.Language

internal object LanguageTranslator {
    private val CATALOG = arrayOf(
        arrayOf("Myanmar Year", "မြန်မာနှစ်"),
        arrayOf("Good Friday", "သောကြာနေ့ကြီး"),
        arrayOf("New Year's", "နှစ်ဆန်း"),
        arrayOf("Independence", "လွတ်လပ်ရေး"),
        arrayOf("Union", "ပြည်ထောင်စု"),
        arrayOf("Peasants'", "တောင်သူလယ်သမား"),
        arrayOf("Resistance", "တော်လှန်ရေး"),
        arrayOf("Labour", "အလုပ်သမား"),
        arrayOf("Martyrs'", "အာဇာနည်"),
        arrayOf("Christmas", "ခရစ္စမတ်"),
        arrayOf("Buddha", "ဗုဒ္ဓ"),
        arrayOf("Start of Buddhist Lent", "ဓမ္မစကြာနေ့"),
        arrayOf("End of Buddhist Lent", "မီးထွန်းပွဲ"),
        arrayOf("Tazaungdaing", "တန်ဆောင်တိုင်"),
        arrayOf("National", "အမျိုးသား"),
        arrayOf("Pwe", "ပွဲ"),
        arrayOf("Thingyan", "သင်္ကြန်"),
        arrayOf("Tagu", "တန်ခူး"),
        arrayOf("Kason", "ကဆုန်"),
        arrayOf("Nayon", "နယုန်"),
        arrayOf("Waso", "ဝါဆို"),
        arrayOf("Wagaung", "ဝါခေါင်"),
        arrayOf("Tawthalin", "တော်သလင်း"),
        arrayOf("Thadingyut", "သီတင်းကျွတ်"),
        arrayOf("Tazaungmon", "တန်ဆောင်မုန်း"),
        arrayOf("Nadaw", "နတ်တော်"),
        arrayOf("Pyatho", "ပြာသို"),
        arrayOf("Tabodwe", "တပို့တွဲ"),
        arrayOf("Tabaung", "တပေါင်း"),
        arrayOf("First", "ပ"),
        arrayOf("Second", "ဒု"),
        arrayOf("Late", "နှောင်း")
    )

    fun translateSentence(str: String, from: Language, to: Language): String {
        var updatedString = str
        for (dic in CATALOG) {
            updatedString = str.replace(dic[from.languageIndex], dic[to.languageIndex])
        }
        return updatedString
    }

    fun translate(str: String, from: Language, to: Language): String {
        for (dic in CATALOG) {
            if (dic[from.languageIndex] == str) {
                return dic[to.languageIndex]
            }
        }
        return str
    }

    fun translate(str: String, to: Language): String {
        return translate(str, Language.ENGLISH, to)
    }

    fun translate(number: Double, language: Language): String {
        if (number < 0) return "-${translate(Math.abs(number), language)}"

        val result = StringBuilder()
        var n = number
        while (n > 0) {
            val digit = (n % 10).toInt()
            n = Math.floor(n / 10)
            result.insert(0, translate(digit.toString(), language))
        }
        return result.toString()
    }
}