package ca.mcgill.science.ctf.models

/**
 * Created by Allan Wang on 18/03/2017.
 */

class RoomInfo(val printers: List<PrinterInfo>)

class PrinterInfo(val name: String, val isUp: Boolean) {
    fun getRoom(): String {
        val hyphen = name.indexOf("-")
        return if (hyphen == -1) name else name.substring(0, hyphen)
    }

}