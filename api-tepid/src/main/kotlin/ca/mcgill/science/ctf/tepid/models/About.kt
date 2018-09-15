package ca.mcgill.science.ctf.tepid.models

import java.util.concurrent.TimeUnit

data class About(val debug: Boolean, val ldapEnabled: Boolean,
                 val startTimestamp: Long = -1,
                 val startTime: String = "",
                 val tag: String = "",
                 val creationTimestamp: Long = -1,
                 val creationTime: String = "",
                 val hash: String = "", val warnings: List<String> = emptyList()) {

    val uptime: String
        get() {
            val up = System.currentTimeMillis() - startTimestamp
            val hours = TimeUnit.MILLISECONDS.toHours(up)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(up) - TimeUnit.HOURS.toMinutes(hours)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(up) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(up))
            return String.format("%02d hours, %02d min, %02d sec", hours, minutes, seconds)
        }

}