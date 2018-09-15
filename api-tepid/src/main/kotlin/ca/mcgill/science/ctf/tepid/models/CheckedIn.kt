package ca.mcgill.science.ctf.tepid.models

data class CheckedIn(
        var currentCheckIn: Map<String, Array<String>> = emptyMap(),
        var lateCheckIns: Map<String, Array<String>> = emptyMap(),
        var lateCheckOuts: Map<String, Array<String>> = emptyMap()
)
