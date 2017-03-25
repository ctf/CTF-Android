package ca.mcgill.science.ctf.api

import android.content.Context
import ca.mcgill.science.ctf.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers
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
            if (instance == null) {
                instance = TEPIDAPI(token, context).api
                return instance!!
            } else return instance!!
        }

        //if there is no instance, things will go badly...
        fun getInstanceDangerously(): ITEPID {
            return instance!!
        }

        //forcefully define new instance
        fun setInstance(token: String?, context: Context): Unit {
            instance = TEPIDAPI(token, context).api
        }
    }

    init {
        val cacheDir = File(context.cacheDir, "responses")
        val cacheSize = 5L * 1024 * 1024 //10MiB
        val cache = Cache(cacheDir, cacheSize)

        val client = OkHttpClient.Builder()
                .addInterceptor(CTFInterceptor(token, context))
                .cache(cache)

        if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "releaseTest") { //log if not full release
            client.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        }


        val gson = GsonBuilder().setLenient()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://tepid.science.mcgill.ca:8443/tepid/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson.create()))
                .client(client.build())
                .build();
        api = retrofit.create(ITEPID::class.java)
    }

}