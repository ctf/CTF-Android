package ca.mcgill.science.ctf.api

import ca.mcgill.science.ctf.models.PrintQueue
import ca.mcgill.science.ctf.models.RoomInfo
import ca.mcgill.science.ctf.models.User
import ca.mcgill.science.ctf.models.UserData
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

    @GET("/users/{shortUser}/quota")
    fun getQuota(@Path("shortUser") shortUser: String): Call<Int>

    @GET("/queues")
    fun getRoomInfo(): Call<RoomInfo>

    @GET("queues/{roomId}")
    fun getPrintQueue(@Path("roomId") roomId: String, @Query("limit") limit: Int): Call<PrintQueue>
}
