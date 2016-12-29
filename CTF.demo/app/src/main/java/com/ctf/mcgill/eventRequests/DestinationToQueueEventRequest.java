package com.ctf.mcgill.eventRequests;

import com.ctf.mcgill.enums.DataType;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;

/**
 * Created by Allan Wang on 28/12/2016.
 *
 * Workaround for getting Queue, since it requires going the Destination
 */

public class DestinationToQueueEventRequest extends DestinationEventRequest {

    private RequestListener<HashMap> callback;

    DestinationToQueueEventRequest(RequestListener<HashMap> callback) {
        this.callback = callback;
    }

    @Override
    DataType.Single getDataType() {
        return DataType.Single.DESTINATIONS_TO_QUEUE;
    }

    @Override
    RequestListener<HashMap> getListener() {
        return callback;
    }
}
