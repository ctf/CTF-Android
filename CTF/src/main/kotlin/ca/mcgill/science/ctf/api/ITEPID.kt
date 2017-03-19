package ca.mcgill.science.ctf.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Allan Wang on 18/03/2017.
 *
 * Interface for tepid paths
 *
 * Refs
 * https://github.com/ruler88/GithubDemo
 * https://zeroturnaround.com/rebellabs/getting-started-with-retrofit-2/
 */

interface ITEPID {

    @GET("/users/{shortUser}")
    fun getUser(@Path("shortUser") shortUser: String): Call<User>

    class User(val salutation: String, val realName: String, val longUser: String, val studentId: Int, val colorPrinting: Boolean)

    @GET("/users/{shortUser}/quota")
    fun getQuota(@Path("shortUser") shortUser: String): Call<Int>

    @GET("/queues")
    fun getPrinterInfo(): Call<PrinterInfoList>

    class PrinterInfoList(val list: List<PrinterInfo>)
    class PrinterInfo(val name: String, val isUp: Boolean) {
        fun getRoomName(): String {
            val hyphen = name.indexOf("-")
            return if (hyphen == -1) name else name.substring(0, hyphen)
        }
    }

    @GET("queues/{roomId}")
    fun getPrintQueue(@Path("roomId") roomId: String, @Query("limit") limit: Int): Call<PrintDataList>

    class PrintDataList(val list: List<PrintData>)
    class PrintData(val name: String, val colorPages: Int, val pages: Int, val refunded: Boolean)
}
