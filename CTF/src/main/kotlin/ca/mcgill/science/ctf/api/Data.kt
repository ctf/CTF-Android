package ca.mcgill.science.ctf.api

import ca.mcgill.science.ctf.iitems.PairItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Allan Wang on 18/03/2017.
 *
 * All the valid data structures for ITEPID
 */

/**
 * User info; various bits of information for a given student
 */
class User(val salutation: String, val realName: String, val longUser: String, val studentId: Int, val colorPrinting: Boolean)

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

val dateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.CANADA)
