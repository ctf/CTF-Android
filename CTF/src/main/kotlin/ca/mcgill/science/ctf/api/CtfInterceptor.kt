package ca.mcgill.science.ctf.api

import android.content.Context
import ca.mcgill.science.ctf.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Allan Wang on 18/03/2017.
 */

class CtfInterceptor(val token: String?, val context: Context) : Interceptor {

    val maxStale = 60 * 60 * 24 * 28 //maxAge to get from cache if online (4 weeks)

    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request().newBuilder()
        if (chain.request().header("CTFA-Type") == "NewSession") { //new session request
            request.removeHeader("CTFA-Type")
            request.addHeader("Content-Type", "application/json;charset=UTF-8")
            request.addHeader("Cache-Control", "public, max-age=0")
        } else {
            request.addHeader("Authorization", "Token " + token)
            if (!Utils.isNetworkAvailable(context)) request.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
        }
        return chain.proceed(request.build())
    }

}
