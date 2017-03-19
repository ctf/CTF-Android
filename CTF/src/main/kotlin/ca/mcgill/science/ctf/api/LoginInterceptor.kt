package ca.mcgill.science.ctf.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Allan Wang on 18/03/2017.
 */

class LoginInterceptor(val token: String?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response? {
        if (token == null) return null
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
                .header("Authorization", "Token " + token)
                .build()
        return chain.proceed(request)
    }

}
