package ca.mcgill.science.ctf.api

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Created by Allan Wang on 18/03/2017.
 *
 * API for data retrieval
 */

class TEPIDAPI(token: String?, context: Context) {
    private val api: ITEPID

    init {
        val cacheDir = File(context.cacheDir, "responses")
        val cacheSize = 5L * 1024 * 1024 //10MiB
        val cache = Cache(cacheDir, cacheSize)

        val client = OkHttpClient.Builder()
                .addInterceptor(CTFInterceptor(token, context))
                .cache(cache)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://tepid.science.mcgill.ca:8443/tepid/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        api = retrofit.create(ITEPID::class.java)
    }

    fun getSession(username: String, password: String): Call<Session> {
        return api.getSession(SessionRequest(username, password))
    }

    fun removeSession(id: String): Call<Void> {
        return api.removeSession(id)
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

    fun getUserQuery(query: String): Call<List<UserQuery>> {
        return getUserQuery(query, 15)
    }

    fun getUserQuery(query: String, limit: Int): Call<List<UserQuery>> {
        return api.getUserQuery(query, limit)
    }


}