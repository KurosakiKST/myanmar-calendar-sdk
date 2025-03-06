package com.ryan.myanmarcalendar.core.translator

import com.ryan.myanmarcalendar.data.model.Language

object LanguageTranslator {
    // This is a simplified version of the catalog from the Java implementation
    // Expand with more translations as needed
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
        arrayOf("Karen", "ကရင်"),
        arrayOf("Pwe", "ပွဲ"),
        arrayOf("Thingyan", "သင်္ကြန်"),
        arrayOf("Akyo", "အကြို"),
        arrayOf("Akyat", "အကြတ်"),
        arrayOf("Akya", "အကျ"),
        arrayOf("Atat", "အတက်"),
        arrayOf("Amyeittasote", "အမြိတ္တစုတ်"),
        arrayOf("Warameittugyi", "ဝါရမိတ္တုကြီး"),
        arrayOf("Warameittunge", "ဝါရမိတ္တုငယ်"),
        arrayOf("Thamaphyu", "သမားဖြူ"),
        arrayOf("Thamanyo", "သမားညို"),
        arrayOf("Yatpote", "ရက်ပုပ်"),
        arrayOf("Yatyotema", "ရက်ယုတ်မာ"),
        arrayOf("Mahayatkyan", "မဟာရက်ကြမ်း"),
        arrayOf("Nagapor", "နဂါးပေါ်"),
        arrayOf("Shanyat", "ရှမ်းရက်"),
        arrayOf("Yatyaza", "ရက်ရာဇာ"),
        arrayOf("Pyathada", "ပြဿဒါး"),
        arrayOf("Afternoon", "မွန်းလွဲ"),
        arrayOf("Sabbath", "ဥပုသ်"),
        arrayOf("Sabbath Eve", "အဖိတ်"),
        arrayOf("Naga", "နဂါး"),
        arrayOf("Head", "ခေါင်း"),
        arrayOf("Facing", "လှည့်"),
        arrayOf("East", "အရှေ့"),
        arrayOf("West", "အနောက်"),
        arrayOf("South", "တောင်"),
        arrayOf("North", "မြောက်"),
        arrayOf("Mahabote", "မဟာဘုတ်"),
        arrayOf("Born", "ဖွား"),
        arrayOf("Binga", "ဘင်္ဂ"),
        arrayOf("Atun", "အထွန်း"),
        arrayOf("Yaza", "ရာဇ"),
        arrayOf("Adipati", "အဓိပတိ"),
        arrayOf("Marana", "မရဏ"),
        arrayOf("Thike", "သိုက်"),
        arrayOf("Puti", "ပုတိ"),
        arrayOf("Ogre", "ဘီလူး"),
        arrayOf("Elf", "နတ်"),
        arrayOf("Human", "လူ"),
        arrayOf("Nakhat", "နက္ခတ်"),
        arrayOf("Hpusha", "ပုဿ"),
        arrayOf("Magha", "မာခ"),
        arrayOf("Phalguni", "ဖ္လကိုန်"),
        arrayOf("Chitra", "စယ်"),
        arrayOf("Visakha", "ပိသျက်"),
        arrayOf("Jyeshtha", "စိဿ"),
        arrayOf("Ashadha", "အာသတ်"),
        arrayOf("Sravana", "သရဝန်"),
        arrayOf("Bhadrapaha", "ဘဒြ"),
        arrayOf("Asvini", "အာသိန်"),
        arrayOf("Krittika", "ကြတိုက်"),
        arrayOf("Mrigasiras", "မြိက္ကသိုဝ်"),
        arrayOf("Sasana Year", "သာသနာနှစ်"),
        arrayOf("Eid", "အိဒ်"),
        arrayOf("Diwali", "ဒီဝါလီ"),
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
        arrayOf("Late", "နှောင်း"),
        arrayOf("Day", "နေ့"),
        arrayOf("Nay", "နေ့"),
        arrayOf("Yat", "ရက်"),
        arrayOf("Ku", "ခု"),
        arrayOf("Year", "နှစ်"),
        arrayOf("Waxing", "လဆန်း"),
        arrayOf("Waning", "လဆုတ်"),
        arrayOf("Full Moon", "လပြည့်"),
        arrayOf("New Moon", "လကွယ်"),
        arrayOf("0", "၀"),
        arrayOf("1", "၁"),
        arrayOf("2", "၂"),
        arrayOf("3", "၃"),
        arrayOf("4", "၄"),
        arrayOf("5", "၅"),
        arrayOf("6", "၆"),
        arrayOf("7", "၇"),
        arrayOf("8", "၈"),
        arrayOf("9", "၉"),
        arrayOf("Saturday", "စနေ"),
        arrayOf("Sunday", "တနင်္ဂနွေ"),
        arrayOf("Monday", "တနင်္လာ"),
        arrayOf("Tuesday", "အင်္ဂါ"),
        arrayOf("Wednesday", "ဗုဒ္ဓဟူး"),
        arrayOf("Thursday", "ကြာသပတေး"),
        arrayOf("Friday", "သောကြာ")
    )

    /**
     * Translate a whole sentence by replacing all occurrences of words from source language to target language
     */
    fun translateSentence(str: String, from: Language, to: Language): String {
        if (from == to) return str

        var result = str
        for (entry in CATALOG) {
            result = result.replace(entry[from.languageIndex], entry[to.languageIndex])
        }
        return result
    }

    /**
     * Translate a specific word from source language to target language
     */
    fun translate(str: String, from: Language, to: Language): String {
        if (from == to) return str

        for (entry in CATALOG) {
            if (entry[from.languageIndex] == str) {
                return entry[to.languageIndex]
            }
        }
        return str
    }

    /**
     * Convenience method to translate from English to the specified language
     */
    fun translate(str: String, to: Language): String {
        return translate(str, Language.ENGLISH, to)
    }

    /**
     * Translate a list of sentences from one language to another
     */
    fun translateSentenceList(list: List<String>, from: Language, to: Language): List<String> {
        if (from == to) return list
        return list.map { translateSentence(it, from, to) }
    }

    /**
     * Convert a number to its representation in the specified language
     */
    fun translate(number: Double, language: Language): String {
        if (number < 0) return "-${translate(Math.abs(number), language)}"

        if (language == Language.ENGLISH) return number.toString()

        val result = StringBuilder()
        var n = number

        // Handle zero case separately
        if (n == 0.0) {
            return translate("0", language)
        }

        while (n > 0) {
            val digit = (n % 10).toInt()
            n = Math.floor(n / 10)
            result.insert(0, translate(digit.toString(), language))
        }

        return result.toString()
    }
}