package ca.mcgill.science.ctf.tepid.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

abstract class BaseInterceptor : Interceptor {

    abstract fun apply(request: Request.Builder, originalChain: Interceptor.Chain)

    override fun intercept(chain: Interceptor.Chain): Response? {
        val origRequest = chain.request()
        val request = origRequest.newBuilder()

        // unless specified, put requests should be done by json
        if (origRequest.method() == "PUT" && origRequest.header(CONTENT_TYPE) == null)
            request.addHeader(CONTENT_TYPE, APPLICATION_JSON)

        apply(request, chain)
        return chain.proceed(request.build())
    }

}

private const val CONTENT_TYPE = "Content-Type"
private const val APPLICATION_JSON = "application/json;charset=UTF-8"

/**
 * Injects the token to each request
 */
class TokenInterceptor(private val token: () -> String) : BaseInterceptor() {

    override fun apply(request: Request.Builder, originalChain: Interceptor.Chain) {
        val token = token()
        if (token.isNotBlank())
            request.addHeader("Authorization", "Token $token")
    }

}
