package com.ctf.mcgill.items;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctf.mcgill.tepid.Destination;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Allan Wang on 27/12/2016.
 * <p>
 * Wrapper class containing a parcelable destination map
 */

public class DestinationMap implements Parcelable {
    public Map<String, Destination> map;

    public DestinationMap(Map<String, Destination> map) {
        this.map = map;
    }

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

    protected DestinationMap(Parcel in) {
        int mapSize = in.readInt();
        this.map = new HashMap<String, Destination>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            String key = in.readString();
            Destination value = in.readParcelable(Destination.class.getClassLoader());
            this.map.put(key, value);
        }
    }

    public static final Creator<DestinationMap> CREATOR = new Creator<DestinationMap>() {
        @Override
        public DestinationMap createFromParcel(Parcel source) {
            return new DestinationMap(source);
        }

        @Override
        public DestinationMap[] newArray(int size) {
            return new DestinationMap[size];
        }
    };
}
