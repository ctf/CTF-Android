package ca.mcgill.science.ctf.tepid.models

/**
 * Response when toggling colour printing
 */
data class ColorResponse(
        var ok: Boolean = false,
        var id: String = ""
)