package ca.mcgill.science.ctf.api

import ca.allanwang.swiperecyclerview.library.items.PairItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Allan Wang on 18/03/2017.
 *
 * All the valid data structures for ITEPID
 */

/**
 * Session JSON object
 */
class UserSession(val username: String, val password: String, val persistent: Boolean = true, val permanent: Boolean = true)

class UserSessionResponse(val role: String, val user: User)

/**
 * User info; various bits of information for a given student
 */
class User(val salutation: String, val realName: String, val longUser: String, val studentId: Int, val colorPrinting: Boolean)

/**
 * User Query; student info from autoSuggest
 */
class UserQuery(val displayName: String, val shortUser: String, val email: String, val colorPrinting: Boolean, val type: String)

/**
 * Printer info; tells you which printers are up and down in which rooms
 */
class PrinterInfoMap(val data: Map<String, PrinterInfo>)

class PrinterInfo(val name: String, val up: Boolean) {
    fun getRoomName(): String {
        val hyphen = name.indexOf("-")
        return if (hyphen == -1) name else name.substring(0, hyphen)
    }
}

/**
 * PrintData info; gets list of printJobs
 * TODO check if long or int
 */

class PrintData(val name: String, val colorPages: Long, val pages: Long, val refunded: Boolean, val printed: Long) {
    fun getPairData(): PairItem {
        return PairItem(name, dateFormat.format(if (printed == -1L) Date() else Date(printed)).toString())
    }
}

val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CANADA)
