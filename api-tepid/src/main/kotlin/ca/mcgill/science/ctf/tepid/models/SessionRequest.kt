package ca.mcgill.science.ctf.tepid.models

data class SessionRequest(
        var username: String = "",
        var password: String = "",
        var persistent: Boolean = true,
        var permanent: Boolean = true
)