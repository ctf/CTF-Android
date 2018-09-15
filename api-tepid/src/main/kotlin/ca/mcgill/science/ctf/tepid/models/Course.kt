package ca.mcgill.science.ctf.tepid.models

import java.util.*

data class Course(val name: String, val season: Season, val year: Int) {
    fun semester() = Semester(season, year)
}

data class Semester(val season: Season, val year: Int) : Comparable<Semester> {

    override fun compareTo(other: Semester): Int =
            if (year != other.year) year - other.year
            else season.compareTo(other.season)

    companion object {
        /**
         * Get the current semester
         */
        val current: Semester
            get() = with(Calendar.getInstance()) {
                Semester(Season.fromMonth(get(Calendar.MONTH)), get(Calendar.YEAR))
            }

        fun winter(year: Int) = Semester(Season.WINTER, year)
        fun fall(year: Int) = Semester(Season.FALL, year)
        fun summer(year: Int) = Semester(Season.SUMMER, year)
    }
}