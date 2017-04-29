package ca.mcgill.science.ctf.api

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
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

    @Headers("CTFA-Type: NewSession")
    @POST("sessions")
    fun getSession(@Body body: SessionRequest): Call<Session>

    @DELETE("sessions/{id}")
    fun removeSession(@Path("id") id: String): Call<Void>

    @GET("users/{shortUser}")
    fun getUser(@Path("shortUser") shortUser: String): Call<User>

    @GET("users/{shortUser}")
    fun getUserObservable(@Path("shortUser") shortUser: String): Observable<User>

    @GET("users/{id}?noRedirect")
    fun getUser(@Path("id") id: Long): Call<User>

    @GET("users/{shortUser}/quota")
    fun getQuota(@Path("shortUser") shortUser: String): Call<Int>

    @GET("users/{shortUser}/quota")
    fun getQuotaObservable(@Path("shortUser") shortUser: String): Observable<Int>

    @GET("destinations")
    fun getPrinterInfo(): Call<Map<String, PrinterInfo>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("destinations/{printerId}")
    fun setPrinterStatus(@Path("printerId") printerId: String, @Body body: PrinterTicket): Call<String>

    @GET("queues/{roomId}")
    fun getPrintQueue(@Path("roomId") roomId: String, @Query("limit") limit: Int): Call<List<PrintData>>

    //TODO add limit query
    @GET("jobs/{shortUser}")
    fun getUserPrintJobs(@Path("shortUser") shortUser: String): Call<List<PrintData>>

    @GET("jobs/{shortUser}")
    fun getUserPrintJobsObservable(@Path("shortUser") shortUser: String): Observable<List<PrintData>>

    @GET("users/autosuggest/{expr}")
    fun getUserQuery(@Path("expr") query: String, @Query("limit") limit: Int): Call<List<UserQuery>>

    @GET("users/autosuggest/{expr}?limit=10") //use default query limit
    fun getUserQuery(@Path("expr") query: String): Call<List<UserQuery>>

    @PUT("jobs/job/{id}/refunded")
    fun refund(@Path("id") id: String, @Body refund: Boolean): Call<Void>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @PUT("users/{shortUser}/color")
    fun enableColor(@Path("shortUser") shortUser: String, @Body enable: Boolean): Call<ColorResponse>

    @GET("barcode/_wait")
    fun scanBarcode(): Call<UserBarcode>

}
