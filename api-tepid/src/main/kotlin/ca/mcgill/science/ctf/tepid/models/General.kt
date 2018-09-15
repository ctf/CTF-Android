package ca.mcgill.science.ctf.tepid.models

/**
 * General response for executing put requests, as denoted by CouchDb's documentation
 */
data class PutResponse(
        var ok: Boolean = false,
        var id: String = "",
        var rev: String = ""
)

/**
 * Response used whenever a request fails on the server end
 * Note that to keep things consistent, everything that isn't [status] and [error]
 * should have defaults.
 *
 * An error response with a status of -1 is also likely wrongly parsed and not actually an error response
 *
 * Extra parameters will only be updated if an error response is explicitly created
 */
data class ErrorResponse(
        val status: Int = -1,
        val error: String = "",
        val extras: List<String> = emptyList()
)

data class ChangeDelta(
        val id: String = "",
        val extras: List<String> = emptyList()
)