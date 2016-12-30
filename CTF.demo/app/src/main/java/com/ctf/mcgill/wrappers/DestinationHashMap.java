package com.ctf.mcgill.wrappers;

import android.os.Parcel;

import com.ctf.mcgill.tepid.Destination;
import com.pitchedapps.capsule.library.parcelable.maps.ParcelableHashMap;

import java.util.HashMap;

/**
 * Created by Allan Wang on 2016-12-28.
 *
 * Parcelable HashMap<String, Destination> wrapper
 */
public class DestinationHashMap extends ParcelableHashMap<String, Destination> {

    public DestinationHashMap(HashMap<String, Destination> map) {
        super(map);
    }

    protected DestinationHashMap(Parcel in) {
        super(in);
    }

    @Override
    protected void writeKeyToParcel(Parcel dest, String key, int flags) {
        dest.writeString(key);
    }

    @Override
    protected void writeValueToParcel(Parcel dest, Destination value, int flags) {
        dest.writeParcelable(value, flags);
    }

    @Override
    protected String readKeyFromParcel(Parcel in) {
        return in.readString();
    }

    @Override
    protected Destination readValueFromParcel(Parcel in) {
        return in.readParcelable(Destination.class.getClassLoader());
    }

    public static final Creator<DestinationHashMap> CREATOR = new Creator<DestinationHashMap>() {
        @Override
        public DestinationHashMap createFromParcel(Parcel source) {
            return new DestinationHashMap(source);
        }

        @Override
        public DestinationHashMap[] newArray(int size) {
            return new DestinationHashMap[size];
        }
    };
}
