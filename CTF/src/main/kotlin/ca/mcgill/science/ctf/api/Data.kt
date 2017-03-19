package ca.mcgill.science.ctf.api

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
class PrinterInfoList(val list: List<PrinterInfo>)

class PrinterInfo(val name: String, val isUp: Boolean) {
    fun getRoomName(): String {
        val hyphen = name.indexOf("-")
        return if (hyphen == -1) name else name.substring(0, hyphen)
    }
}

/**
 * PrintData info; gets list of printJobs
 */
class PrintDataList(val list: List<PrintData>)

class PrintData(val name: String, val colorPages: Int, val pages: Int, val refunded: Boolean)