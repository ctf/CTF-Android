package ca.mcgill.science.ctf.tepid.api

/**
 * Imitation of javax's RolesAllowed
 *
 * Used purely for display
 *
 * Unlike RolesAllowed, min authority will only indicate
 * the lowest level needed.
 *
 * Authorities go in order of
 *
 * none < user < ctfer < elder
 */
@Retention(AnnotationRetention.SOURCE)
internal annotation class MinAuthority(val role: String)
