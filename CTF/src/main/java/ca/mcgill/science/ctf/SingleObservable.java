package ca.mcgill.science.ctf

import android.content.Context;

import org.reactivestreams.Subscriber;

import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 2017-03-19.
 * <p>
 * Makes sure that only one call is used at a time; is many are called, only the newest one will keep executing
 */

public abstract class SingleObservable<I, C> {
    Context c;
    String token;
    private Observable<C> mObservable;
    private Subscriber<C> mSubscriber;
    private final ITEPID mAPI;

    public SingleObservable(Context c, String token) {
        this.c = c;
        this.token = token;
        mAPI = TEPIDAPI.Companion.getInstance(token, c);
    }

    int EMPTY_RESULT = -1

    public void request(input:I) {
        cancel()
        mCall = getAPICall(input)
        mCall !!.enqueue(object :Callback<C> {
            override fun onResponse(call:Call<C>,response:
            Response<C>){
                if (response.body() == null || !response.isSuccessful)
                    onEnd(EMPTY_RESULT)
                else
                    onSuccess(response.body())
            }

            override fun onFailure(call:Call<C>,t:
            Throwable){
                if (!call.isCanceled) onFail(t)
            }
        })
    }

    fun cancel() {
        mCall ?.unsubscribeOn()
        mCall = null
    }

    protected abstract fun getAPICall(input:I):Call<C>

    protected abstract fun onSuccess(result:C)

    protected abstract fun onFail(t:Throwable)

    protected abstract fun onEnd(flag:Int)


}
