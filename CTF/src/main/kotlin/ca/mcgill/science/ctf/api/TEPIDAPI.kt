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

class TEPIDAPI private constructor(token: String?, context: Context) {

    private val api: ITEPID

    companion object {
        private var instance: ITEPID? = null

        fun getInstance(token: String?, context: Context): ITEPID {
            return if (instance == null) TEPIDAPI(token, context).api else instance!!
        }
    }

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

}