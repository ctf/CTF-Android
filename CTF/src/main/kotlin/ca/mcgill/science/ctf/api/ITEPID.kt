package ca.mcgill.science.ctf.api

import retrofit2.Call
import retrofit2.http.*

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

    @POST("sessions")
    @Headers("CTFA-Type: NewSession")
    fun getSession(@Body body: SessionRequest): Call<Session>

    @DELETE("sessions/{id}")
    fun removeSession(@Path("id") id: String): Call<Void>

    @GET("users/{shortUser}")
    fun getUser(@Path("shortUser") shortUser: String): Call<User>

    @GET("users/{shortUser}/quota")
    fun getQuota(@Path("shortUser") shortUser: String): Call<Int>

    @GET("destinations")
    fun getPrinterInfo(): Call<Map<String, PrinterInfo>>

    @GET("queues/{roomId}")
    fun getPrintQueue(@Path("roomId") roomId: String, @Query("limit") limit: Int): Call<List<PrintData>>

    //TODO add limit query
    @GET("jobs/{shortUser}")
    fun getUserPrintJobs(@Path("shortUser") shortUser: String): Call<List<PrintData>>

    @GET("users/autosuggest/{expr}")
    fun getUserQuery(@Path("expr") query: String, @Query("limit") limit: Int): Call<List<UserQuery>>

}
