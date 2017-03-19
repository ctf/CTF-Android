package ca.mcgill.science.ctf.models

/**
 * Created by Allan Wang on 18/03/2017.
 */

class PrintQueue(jobs: List<PrintData>)

class PrintData(val name: String, val colorPages: Int, val pages: Int, val refunded: Boolean)