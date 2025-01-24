package com.ryan.myanmarcalendar.data.model

import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.core.kernel.AstroKernel
import com.ryan.myanmarcalendar.core.translator.LanguageTranslator
import java.io.Serializable

data class Astro(
    val sabbath: Int = 0,
    val yatyaza: Int = 0,
    val pyathada: Int = 0,
    val thamanyo: Int = 0,
    val amyeittasote: Int = 0,
    val warameittugyi: Int = 0,
    val warameittunge: Int = 0,
    val yatpote: Int = 0,
    val thamaphyu: Int = 0,
    val nagapor: Int = 0,
    val yatyotema: Int = 0,
    val mahayatkyan: Int = 0,
    val shanyat: Int = 0,
    val nagahle: Int = 0,  // 0=west, 1=north, 2=east, 3=south
    val mahabote: Int = 0, // 0=Binga, 1=Atun, 2=Yaza, 3=Adipati, 4=Marana, 5=Thike, 6=Puti
    val nakhat: Int = 0,   // 0=orc, 1=elf, 2=human
    val yearName: Int = 0
) : Serializable {

    fun isYatyaza() = yatyaza > 0
    fun isPyathada() = pyathada > 0
    fun isSabbath() = sabbath == 1
    fun isSabbathEve() = sabbath == 2
    fun isThamanyo() = thamanyo > 0
    fun isAmyeittasote() = amyeittasote > 0
    fun isWarameittugyi() = warameittugyi > 0
    fun isWarameittunge() = warameittunge > 0
    fun isYatpote() = yatpote > 0
    fun isThamaphyu() = thamaphyu > 0
    fun isNagapor() = nagapor > 0
    fun isYatyotema() = yatyotema > 0
    fun isMahayatkyan() = mahayatkyan > 0
    fun isShanyat() = shanyat > 0

    fun getYatyaza(language: Language = CalendarConfig.getInstance().language): String {
        return if (isYatyaza()) LanguageTranslator.translate("Yatyaza", language) else ""
    }

    fun getPyathada(language: Language = CalendarConfig.getInstance().language): String {
        return when (pyathada) {
            1 -> LanguageTranslator.translate("Pyathada", language)
            2 -> "${LanguageTranslator.translate("Afternoon", language)} ${LanguageTranslator.translate("Pyathada", language)}"
            else -> ""
        }
    }

    fun getNagahle(language: Language = CalendarConfig.getInstance().language): String {
        val directions = arrayOf("West", "North", "East", "South")
        return LanguageTranslator.translate(directions[nagahle], language)
    }

    fun getMahabote(language: Language = CalendarConfig.getInstance().language): String {
        val mahaboteTypes = arrayOf("Binga", "Atun", "Yaza", "Adipati", "Marana", "Thike", "Puti")
        return LanguageTranslator.translate(mahaboteTypes[mahabote], language)
    }

    fun getNakhat(language: Language = CalendarConfig.getInstance().language): String {
        val nakhatTypes = arrayOf("Ogre", "Elf", "Human")
        return LanguageTranslator.translate(nakhatTypes[nakhat], language)
    }

    fun getYearName(language: Language = CalendarConfig.getInstance().language): String {
        val yearNames = arrayOf(
            "Hpusha", "Magha", "Phalguni", "Chitra", "Visakha", "Jyeshtha",
            "Ashadha", "Sravana", "Bhadrapaha", "Asvini", "Krittika", "Mrigasiras"
        )
        return LanguageTranslator.translate(yearNames[yearName], language)
    }

    companion object {
        fun of(myanmarDate: MyanmarDate): Astro {
            with(myanmarDate) {
                return Astro(
                    sabbath = AstroKernel.calculateSabbath(yearType, month, day),
                    yatyaza = AstroKernel.calculateYatyaza(month, weekDay),
                    pyathada = AstroKernel.calculatePyathada(month, weekDay),
                    thamanyo = AstroKernel.calculateThamanyo(month, weekDay),
                    amyeittasote = AstroKernel.calculateAmyeittasote(day, weekDay),
                    warameittugyi = AstroKernel.calculateWarameittugyi(day, weekDay),
                    warameittunge = AstroKernel.calculateWarameittunge(day, weekDay),
                    yatpote = AstroKernel.calculateYatpote(day, weekDay),
                    thamaphyu = AstroKernel.calculateThamaphyu(day, weekDay),
                    nagapor = AstroKernel.calculateNagapor(day, weekDay),
                    yatyotema = AstroKernel.calculateYatyotema(month, day),
                    mahayatkyan = AstroKernel.calculateMahayatkyan(month, day),
                    shanyat = AstroKernel.calculateShanyat(month, day),
                    nagahle = AstroKernel.calculateNagahle(month),
                    mahabote = AstroKernel.calculateMahabote(year, weekDay),
                    nakhat = AstroKernel.calculateNakhat(year),
                    yearName = AstroKernel.calculateYearName(year)
                )
            }
        }
    }
}