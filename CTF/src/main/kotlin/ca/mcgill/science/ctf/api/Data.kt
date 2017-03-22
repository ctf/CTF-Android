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
class SessionRequest(val username: String, val password: String, val persistent: Boolean = true, val permanent: Boolean = true) {
    constructor(username: String, password: String) : this(username, password, true, true)
}

class Session(val role: String, val user: User, val _id: String)

/**
 * User info; various bits of information for a given student
 */
class User(val _id: String, val salutation: String, val realName: String, val displayName:String, val shortUser: String, var nick: String, val email: String, val studentId: Int, val colorPrinting: Boolean)

/**
 * User Query; student info from autoSuggest
 * A shorter version of user
 */
class UserQuery(val displayName: String, val shortUser: String, val email: String, val colorPrinting: Boolean, val type: String)

/**
 * Printer info; tells you which printers are up and down in which rooms
 */
class PrinterInfoMap(val data: Map<String, PrinterInfo>)

class PrinterInfo(val _id: String, val name: String, val up: Boolean, var ticket: PrinterTicket?) {
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

/**
 * Printer Ticket
 * if printer is marked down, this is the ticket detailing the description and user who marked it down
 */
class PrinterTicket(val up: Boolean, var reason: String?, var user: User?)

val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CANADA)
