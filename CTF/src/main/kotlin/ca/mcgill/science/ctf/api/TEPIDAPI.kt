package ca.mcgill.science.ctf.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Allan Wang on 18/03/2017.
 *
 * API for data retrieval
 */

class TEPIDAPI(token: String?) {
    private val api: ITEPID

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor(LoginInterceptor(token))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://tepid.science.mcgill.ca:8443/tepid/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        api = retrofit.create(ITEPID::class.java)
    }

    fun getUser(shortUser: String): Call<User> {
        return api.getUser(shortUser)
    }

    fun getQuota(shortUser: String): Call<Int> {
        return api.getQuota(shortUser)
    }

    fun getPrinterInfo(): Call<Map<String, PrinterInfo>> {
        return api.getPrinterInfo()
    }

    fun getPrintQueue(roomId: String, limit: Int): Call<List<PrintData>> {
        return api.getPrintQueue(roomId, limit)
    }

    fun getUserPrintJobs(shortUser: String): Call<List<PrintData>> {
        return api.getUserPrintJobs(shortUser)
    }

}