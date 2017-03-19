package ca.mcgill.science.ctf.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Allan Wang on 18/03/2017.
 *
 * Interface for tepid paths
 * See Data class for what each item holds
 *
 * Refs
 * https://github.com/ruler88/GithubDemo
 * https://zeroturnaround.com/rebellabs/getting-started-with-retrofit-2/
 */

interface ITEPID {

    @GET("users/{shortUser}")
    fun getUser(@Path("shortUser") shortUser: String): Call<User>

    @GET("users/{shortUser}/quota")
    fun getQuota(@Path("shortUser") shortUser: String): Call<Int>

    @GET("destinations")
    fun getPrinterInfo(): Call<Map<String, PrinterInfo>>

    @GET("queues/{roomId}")
    fun getPrintQueue(@Path("roomId") roomId: String, @Query("limit") limit: Int): Call<List<PrintData>>

}
