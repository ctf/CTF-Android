package ca.mcgill.science.ctf.api

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allan Wang on 2017-03-19.
 *
 * Makes sure that only one call is used at a time; is many are called, only the newest one will keep executing
 */

abstract class SingleCallRequest<in I, C>(c: Context, token: String) {

    private var mCall: Call<C>? = null
    private val mAPI: ITEPID = TEPIDAPI.getInstance(token, c)
    val EMPTY_RESULT = -1

    fun request(input: I) {
        cancel()
        mCall = getAPICall(input)
        mCall!!.enqueue(object : Callback<C> {
            override fun onResponse(call: Call<C>, response: Response<C>) {
                if (response.body() == null || !response.isSuccessful)
                    onEnd(EMPTY_RESULT)
                else
                    onSuccess(response.body())
                mCall = null
            }

            override fun onFailure(call: Call<C>, t: Throwable) {
                if (!call.isCanceled) onFail(t)
                mCall = null
            }
        })
    }

    fun cancel() {
        mCall?.cancel()
        mCall = null
    }

    protected abstract fun getAPICall(input: I): Call<C>

    protected abstract fun onSuccess(result: C)

    protected abstract fun onFail(t: Throwable)

    protected abstract fun onEnd(flag: Int)


}