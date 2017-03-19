package ca.mcgill.science.ctf.api

import ca.mcgill.science.ctf.models.PrintQueue
import ca.mcgill.science.ctf.models.RoomInfo
import ca.mcgill.science.ctf.models.User
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
    private val interceptor: LoginInterceptor

    init {
        interceptor = LoginInterceptor(token)
        val client = OkHttpClient()
        client.interceptors().add(interceptor)

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

    fun getRoomInfo(): Call<RoomInfo> {
        return api.getRoomInfo()
    }

    fun getPrintQueue(roomId: String, limit: Int): Call<PrintQueue> {
        return api.getPrintQueue(roomId, limit)
    }

}