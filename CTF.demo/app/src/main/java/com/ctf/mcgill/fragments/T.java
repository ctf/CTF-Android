package com.ctf.mcgill.fragments;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctf.mcgill.tepid.Destination;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Allan Wang on 27/12/2016.
 */

public class T implements Parcelable {
    private Map<String, Destination> map;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.map.size());
        for (Map.Entry<String, Destination> entry : this.map.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    public T() {
    }

    protected T(Parcel in) {
        int mapSize = in.readInt();
        this.map = new HashMap<String, Destination>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            String key = in.readString();
            Destination value = in.readParcelable(Destination.class.getClassLoader());
            this.map.put(key, value);
        }
    }

    public static final Parcelable.Creator<T> CREATOR = new Parcelable.Creator<T>() {
        @Override
        public T createFromParcel(Parcel source) {
            return new T(source);
        }

        @Override
        public T[] newArray(int size) {
            return new T[size];
        }
    };
}
