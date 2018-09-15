package ca.mcgill.science.ctf.tepid.models

data class PrintQueue(
        var loadBalancer: String? = null,
        var defaultOn: String? = null,
        var name: String? = null,
        var destinations: List<String> = emptyList()
)