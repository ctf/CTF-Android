package ca.mcgill.science.ctf.api

import android.content.Context
import ca.mcgill.science.ctf.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Allan Wang on 18/03/2017.
 */

class CTFInterceptor(val token: String?, val context: Context) : Interceptor {

    val maxStale = 60 * 60 * 24 * 28 //maxAge to get from cache if online (4 weeks)

    override fun intercept(chain: Interceptor.Chain): Response? {
        if (token == null) return null //no token; no response
        //add token to response
        val request = chain.request().newBuilder().header("Authorization", "Token " + token)
        if (!Utils.isNetworkAvailable(context)) request.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
        return chain.proceed(request.build())
    }

}
