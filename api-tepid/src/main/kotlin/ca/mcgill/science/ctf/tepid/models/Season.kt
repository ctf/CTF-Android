package ca.mcgill.science.ctf.tepid.models

import java.util.*

enum class Season {
    // NOTE: Order matters! WINTER 2018 < SUMMER 2018 < FALL 2018
    WINTER,
    SUMMER, FALL;

    companion object {

        fun fromMonth(month: Int) = when (month) {
            in Calendar.SEPTEMBER..Calendar.DECEMBER -> FALL
            in Calendar.JANUARY..Calendar.MAY -> WINTER
            else -> SUMMER
        }

        operator fun invoke(name: String) = valueOf(name.toUpperCase(Locale.CANADA))
    }
}