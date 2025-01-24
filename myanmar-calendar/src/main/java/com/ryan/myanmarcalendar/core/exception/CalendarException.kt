package com.ryan.myanmarcalendar.core.exception

internal sealed class CalendarException(message: String) : Exception(message) {
    class InvalidDateException(message: String) : CalendarException(message)
    class ConversionException(message: String) : CalendarException(message)
}