package ca.mcgill.science.ctf.api

import android.content.Context
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Allan Wang on 2017-03-19.
 *
 * Makes sure that only one call is used at a time; is many are called, only the newest one will keep executing
 */

abstract class SingleObservable<in I, C>(c: Context, token: String, subscriber:Subscriber<C>) {

    private var mObservable: Observable<C>? = null
    private val mAPI: ITEPID = TEPIDAPI.getInstance(token, c)
    val EMPTY_RESULT = -1
    val

    fun request(input: I) {
        cancel()
        mObservable = getAPICall(input)
        mObservable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {  }enqueue(object : Callback<C> {
            override fun onResponse(call: Call<C>, response: Response<C>) {
                if (response.body() == null || !response.isSuccessful)
                    onEnd(EMPTY_RESULT)
                else
                    onSuccess(response.body())
            }

            override fun onFailure(call: Call<C>, t: Throwable) {
                if (!call.isCanceled) onFail(t)
            }
        })
    }

    fun cancel() {
        mCall?.unsubscribeOn()
        mCall = null
    }

    protected abstract fun getAPICall(input: I): Observable<C>

    protected abstract fun onSuccess(result: C)

    protected abstract fun onFail(t: Throwable)

    protected abstract fun onEnd(flag: Int)


}
