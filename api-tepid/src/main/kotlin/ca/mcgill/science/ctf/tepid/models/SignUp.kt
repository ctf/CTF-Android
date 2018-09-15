package ca.mcgill.science.ctf.tepid.models

data class SignUp(
        var name: String,
        var givenName: String = name,
        var nickname: String? = null,
        var slots: Map<String, Array<String>> = emptyMap()
)