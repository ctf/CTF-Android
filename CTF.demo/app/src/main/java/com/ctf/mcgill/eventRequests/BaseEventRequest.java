package com.ctf.mcgill.eventRequests;

import android.content.Context;
import android.support.annotation.Nullable;

import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;
import com.ctf.mcgill.requests.BaseTepidRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.utils.EventUtils;

/**
 * Created by Allan Wang on 28/12/2016.
 */

public abstract class BaseEventRequest<T> {

    public void execute(SpiceManager manager, Context context, String token, @Nullable Object extra) {
        if (!manager.isStarted()) manager.start(context);
        manager.execute(getRequest(token, extra), getListener());
    }

    RequestListener<T> getListener() {
        return new EventListener();
    }

    abstract BaseTepidRequest<T> getRequest(String token, @Nullable Object extra);

    abstract DataType.Single getDataType();

    /**
     * Action for listener after successful request
     *
     * @param type dataType
     * @param data data received
     */
    void onRequestSuccess(DataType.Single type, T data) {
        postLoadEvent(type, data);
    }

    private class EventListener implements RequestListener<T> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            postErrorEvent(getDataType(), String.format("%s request failed...", getDataType()));
        }

        @Override
        public void onRequestSuccess(T data) {
            BaseEventRequest.this.onRequestSuccess(getDataType(), data);
        }
    }

    <E> E getExtra(Object extra, Class<E> clazz, String error) {
        if (extra == null || !clazz.isAssignableFrom(extra.getClass()))
            throw new IllegalArgumentException(error);
        return (E) extra;
    }

    @Nullable
    <E> E getExtraNullable(Object extra, Class<E> clazz) {
        if (extra == null || !clazz.isAssignableFrom(extra.getClass()))
            return null;
        return (E) extra;
    }

    /**
     * Post loadEvent marked as successful
     * Event will be received by all subscribed Activities and Fragments
     *
     * @param type Single DataType
     * @param data Castable object containing appropriate data
     */
    protected static void postLoadEvent(DataType.Single type, Object data) {
        EventUtils.post(new LoadEvent(type, true, data));
    }

    /**
     * Post loadEvent marked as successful
     * Furthermore, it will only be run through RoomActivity (which will likely reformat and push to fragments)
     *
     * @param type Single DataType
     * @param data Castable object containing appropriate data
     */
    protected static void postLoadEventActivityOnly(DataType.Single type, Object data) {
        EventUtils.post(new LoadEvent(type, true, data).activityOnly());
    }

    /**
     * Post loadEvent marked as unsuccessful
     *
     * @param type  Single DataType
     * @param error String detailing the error; will be put into event data object and logged
     */
    protected static void postErrorEvent(DataType.Single type, String error) {
        EventUtils.post(new LoadEvent(type, false, error));
    }

}
