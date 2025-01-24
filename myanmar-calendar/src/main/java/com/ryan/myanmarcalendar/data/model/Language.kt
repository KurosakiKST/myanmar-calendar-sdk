package com.ryan.myanmarcalendar.data.model

enum class Language(
    val languageIndex: Int,
    val punctuationMark: String,
    val punctuation: String
) {
    ENGLISH(0, ", ", "."),
    MYANMAR(1, "၊ ", "။ ")
}