package ca.mcgill.science.ctf.api

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Allan Wang on 18/03/2017.
 *
 * All the valid data structures for ITepid
 */

/**
 * Session JSON object
 */
class SessionRequest(val username: String, val password: String, val persistent: Boolean, val permanent: Boolean) {
    constructor(username: String, password: String) : this(username, password, true, true)
}

class Session(val role: String, val user: User, val _id: String)

/**
 * User info; various bits of information for a given student
 */
class User(val _id: String, val salutation: String, val realName: String, val displayName: String, val shortUser: String, var nick: String?, val email: String, val studentId: Int?, val faculty: String, val colorPrinting: Boolean)

/**
 * User Query; student info from autoSuggest
 * A shorter version of user
 */
class UserQuery(val displayName: String, val shortUser: String, val email: String, val colorPrinting: Boolean, val type: String)

/**
 * Printer info; tells you which printers are up and down in which rooms
 */
class PrinterInfo(val _id: String, val name: String, val up: Boolean, var ticket: PrinterTicket?) {
    fun getRoomName(): String {
        val hyphen = name.indexOf("-")
        return if (hyphen == -1) name else name.substring(0, hyphen)
    }
}

/**
 * PrintData info; gets list of printJobs
 * TODO check if long or int
 * Keep in mind that some dates do not always exist
 */

class PrintData(val _id: String, val name: String, val colorPages: Long, val pages: Long, val refunded: Boolean, val started: Long, val processed: Long?, val failed: Long?, val printed: Long?, val queueName: String, val originalHost: String, val error: String?, val userIdentification: String) {
    fun getFormattedDate(): String {
        return dateFormat.format(if (started == -1L) Date() else Date(started)).toString()
    }

    fun getRelativeDate(): String {
        return DateUtils.getRelativeTimeSpanString(started, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
    }
}

/**
 * Printer Ticket
 * if printer is marked down, this is the ticket detailing the description and user who marked it down
 */
class PrinterTicket(val up: Boolean, val reason: String, val user: User, val reported: Long) {
    fun getReportedDate(): String {
        return dateFormat.format(Date(reported)).toString()
    }
}

/**
 * Printer Ticket to submit
 * Only contains the necessary params
 */
class PrinterTicketSubmission(val up: Boolean, val reason: String?)

/**
 * Response when toggling colour printing (also has rev: String but we don't need it)
 */
class ColorResponse(val ok: Boolean, val id: String)

/**
 * User response from a barcode scan
 */
class UserBarcode(val _id: String, val code: Long, val time: Long)

val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CANADA)

class FullUser(val user: User, val quota: Int)
