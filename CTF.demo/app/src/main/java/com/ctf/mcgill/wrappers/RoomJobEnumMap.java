package com.ctf.mcgill.wrappers;

import android.os.Parcel;

import com.ctf.mcgill.enums.Room;
import com.pitchedapps.capsule.library.parcelable.maps.ParcelableMap;

import java.util.EnumMap;

/**
 * Created by Allan Wang on 2016-12-28.
 *
 * Parcelable HashMap<String, Destination> wrapper
 */
public class RoomJobEnumMap extends ParcelableMap<Room, RoomPrintJob, EnumMap<Room, RoomPrintJob>> {

    public RoomJobEnumMap(EnumMap<Room, RoomPrintJob> map) {
        super(map);
    }

    @Override
    protected EnumMap<Room, RoomPrintJob> createMap(int mapSize) {
        return new EnumMap<>(Room.class);
    }

    protected RoomJobEnumMap(Parcel in) {
        super(in);
    }

    @Override
    protected void writeKeyToParcel(Parcel dest, Room key, int flags) {
        dest.writeSerializable(key);
    }

    @Override
    protected void writeValueToParcel(Parcel dest, RoomPrintJob value, int flags) {
        dest.writeParcelable(value, flags);
    }

    @Override
    protected Room readKeyFromParcel(Parcel in) {
        return (Room) in.readSerializable();
    }

    @Override
    protected RoomPrintJob readValueFromParcel(Parcel in) {
        return in.readParcelable(RoomPrintJob.class.getClassLoader());
    }

    public static final Creator<RoomJobEnumMap> CREATOR = new Creator<RoomJobEnumMap>() {
        @Override
        public RoomJobEnumMap createFromParcel(Parcel source) {
            return new RoomJobEnumMap(source);
        }

        @Override
        public RoomJobEnumMap[] newArray(int size) {
            return new RoomJobEnumMap[size];
        }
    };
}
