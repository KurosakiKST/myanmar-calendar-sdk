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
    val yearName: Int = 0,
    val moonPhase: Int = 0 // 0=waxing, 1=full moon, 2=waning, 3=dark moon
) : Serializable {

    fun isYatyaza(): Boolean = yatyaza > 0
    fun isPyathada(): Boolean = pyathada > 0
    fun isSabbath(): Boolean = sabbath == 1
    fun isSabbathEve(): Boolean = sabbath == 2
    fun isThamanyo(): Boolean = thamanyo > 0
    fun isAmyeittasote(): Boolean = amyeittasote > 0
    fun isWarameittugyi(): Boolean = warameittugyi > 0
    fun isWarameittunge(): Boolean = warameittunge > 0
    fun isYatpote(): Boolean = yatpote > 0
    fun isThamaphyu(): Boolean = thamaphyu > 0
    fun isNagapor(): Boolean = nagapor > 0
    fun isYatyotema(): Boolean = yatyotema > 0
    fun isMahayatkyan(): Boolean = mahayatkyan > 0
    fun isShanyat(): Boolean = shanyat > 0

    fun getPyathadaValue(): Int = pyathada
    fun getSabbathValue(): Int = sabbath
    fun getNagahleValue(): Int = nagahle
    fun getMahaboteValue(): Int = mahabote
    fun getNakhatValue(): Int = nakhat

    fun getYatyaza(language: Language = CalendarConfig.getInstance().language): String {
        return if (isYatyaza()) LanguageTranslator.translate("Yatyaza", language) else ""
    }

    fun getPyathada(language: Language = CalendarConfig.getInstance().language): String {
        return when (pyathada) {
            1 -> LanguageTranslator.translate("Pyathada", language)
            2 -> {
                val afternoon = LanguageTranslator.translate("Afternoon", language)
                val pyathada = LanguageTranslator.translate("Pyathada", language)
                "$afternoon $pyathada"
            }
            else -> ""
        }
    }

    fun getAstrologicalDay(language: Language = CalendarConfig.getInstance().language): String {
        val sb = StringBuilder()
        sb.append(getYatyaza(language))

        if (isYatyaza() && isPyathada()) {
            sb.append(language.punctuationMark)
        }

        sb.append(getPyathada(language))

        return sb.toString()
    }

    fun getSabbath(language: Language = CalendarConfig.getInstance().language): String {
        return if (isSabbath()) LanguageTranslator.translate("Sabbath", language) else ""
    }

    fun getSabbathEve(language: Language = CalendarConfig.getInstance().language): String {
        return if (isSabbathEve()) LanguageTranslator.translate("Sabbath Eve", language) else ""
    }

    fun getSabbathOrEve(language: Language = CalendarConfig.getInstance().language): String {
        return when (sabbath) {
            1 -> LanguageTranslator.translate("Sabbath", language)
            2 -> LanguageTranslator.translate("Sabbath Eve", language)
            else -> ""
        }
    }

    fun getThamanyo(language: Language = CalendarConfig.getInstance().language): String {
        return if (isThamanyo()) LanguageTranslator.translate("Thamanyo", language) else ""
    }

    fun getAmyeittasote(language: Language = CalendarConfig.getInstance().language): String {
        return if (isAmyeittasote()) LanguageTranslator.translate("Amyeittasote", language) else ""
    }

    fun getWarameittugyi(language: Language = CalendarConfig.getInstance().language): String {
        return if (isWarameittugyi()) LanguageTranslator.translate("Warameittugyi", language) else ""
    }

    fun getWarameittunge(language: Language = CalendarConfig.getInstance().language): String {
        return if (isWarameittunge()) LanguageTranslator.translate("Warameittunge", language) else ""
    }

    fun getYatpote(language: Language = CalendarConfig.getInstance().language): String {
        return if (isYatpote()) LanguageTranslator.translate("Yatpote", language) else ""
    }

    fun getThamaphyu(language: Language = CalendarConfig.getInstance().language): String {
        return if (isThamaphyu()) LanguageTranslator.translate("Thamaphyu", language) else ""
    }

    fun getNagapor(language: Language = CalendarConfig.getInstance().language): String {
        return if (isNagapor()) LanguageTranslator.translate("Nagapor", language) else ""
    }

    fun getYatyotema(language: Language = CalendarConfig.getInstance().language): String {
        return if (isYatyotema()) LanguageTranslator.translate("Yatyotema", language) else ""
    }

    fun getMahayatkyan(language: Language = CalendarConfig.getInstance().language): String {
        return if (isMahayatkyan()) LanguageTranslator.translate("Mahayatkyan", language) else ""
    }

    fun getShanyat(language: Language = CalendarConfig.getInstance().language): String {
        return if (isShanyat()) LanguageTranslator.translate("Shanyat", language) else ""
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

    override fun toString(): String {
        return toString(CalendarConfig.getInstance().language)
    }

    fun toString(language: Language): String {
        val sb = StringBuilder()
        sb.append(getAstrologicalDay(language))

        if (isSabbath() || isSabbathEve()) {
            sb.append(language.punctuationMark)
            sb.append(getSabbathOrEve(language))
        }

        if (isThamanyo()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getThamanyo(language))
        }

        if (isThamaphyu()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getThamaphyu(language))
        }

        if (isAmyeittasote()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getAmyeittasote(language))
        }

        if (isWarameittugyi()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getWarameittugyi(language))
        }

        if (isWarameittunge()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getWarameittunge(language))
        }

        if (isYatpote()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getYatpote(language))
        }

        if (isNagapor()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getNagapor(language))
        }

        if (isYatyotema()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getYatyotema(language))
        }

        if (isMahayatkyan()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getMahayatkyan(language))
        }

        if (isShanyat()) {
            sb.append(" ")
            sb.append(language.punctuationMark)
            sb.append(getShanyat(language))
        }

        sb.append(" ")
        sb.append(language.punctuationMark)
        sb.append(LanguageTranslator.translate("Naga", language))
        sb.append(" ")
        sb.append(LanguageTranslator.translate("Head", language))
        sb.append(" ")
        sb.append(getNagahle(language))
        sb.append(" ")
        sb.append(LanguageTranslator.translate("Facing", language))

        sb.append(" ")
        sb.append(language.punctuationMark)

        sb.append(getMahabote(language))
        sb.append(LanguageTranslator.translate("Born", language))

        sb.append(" ")
        sb.append(language.punctuationMark)

        sb.append(getNakhat(language))
        sb.append(" ")
        sb.append(LanguageTranslator.translate("Nakhat", language))

        sb.append(" ")
        sb.append(language.punctuationMark)

        sb.append(getYearName(language))

        return sb.toString()
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
                    yearName = AstroKernel.calculateYearName(year),
                    moonPhase = moonPhase
                )
            }
        }
    }
}