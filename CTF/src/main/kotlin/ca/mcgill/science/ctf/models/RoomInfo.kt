package ca.mcgill.science.ctf.models

/**
 * Created by Allan Wang on 18/03/2017.
 */

class RoomInfo(val data: RoomInfoData)

class RoomInfoData(val printers: List<PrinterInfo>)

class PrinterInfo(val name: String, val id: String)