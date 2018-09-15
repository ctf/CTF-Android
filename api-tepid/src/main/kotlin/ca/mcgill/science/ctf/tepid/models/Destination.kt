package ca.mcgill.science.ctf.tepid.models

/**
 * Created by Allan Wang on 2017-05-03.
 */
data class Destination(
        var name: String = "",
        var protocol: String? = null,
        var username: String? = null,
        var password: String? = null,
        var path: String? = null,
        var domain: String? = null,
        var ticket: DestinationTicket? = null,
        var up: Boolean = false,
        var ppm: Int = 0
)

data class DestinationTicket(
        var up: Boolean = false,
        var reason: String? = null,
        var user: User? = null,
        var reported: Long = System.currentTimeMillis()
)
