package ca.mcgill.science.ctf.tepid;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allan Wang on 2016-12-22.
 */

public class RoomInformation implements Parcelable {
    private List<Printer> mPrinters = new ArrayList<>();
    public final String roomName;
    public final boolean areComputersAvailable;

    public RoomInformation(String roomName, boolean areComputersAvailable) {
        this.roomName = roomName;
        this.areComputersAvailable = areComputersAvailable;
    }

    public void addPrinter(String printer, boolean isUp) {
        mPrinters.add(new Printer(printer, isUp));
    }

    public List<Printer> getPrinters() {
        return mPrinters;
    }

    public static class Printer implements Parcelable {
        public final boolean isUp;
        public final String name;

        Printer(String name, boolean isUp) {
            this.isUp = isUp;
            this.name = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.isUp ? (byte) 1 : (byte) 0);
            dest.writeString(this.name);
        }

        protected Printer(Parcel in) {
            this.isUp = in.readByte() != 0;
            this.name = in.readString();
        }

        public static final Parcelable.Creator<Printer> CREATOR = new Parcelable.Creator<Printer>() {
            @Override
            public Printer createFromParcel(Parcel source) {
                return new Printer(source);
            }

            @Override
            public Printer[] newArray(int size) {
                return new Printer[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mPrinters);
        dest.writeString(this.roomName);
        dest.writeByte(this.areComputersAvailable ? (byte) 1 : (byte) 0);
    }

    protected RoomInformation(Parcel in) {
        this.mPrinters = in.createTypedArrayList(Printer.CREATOR);
        this.roomName = in.readString();
        this.areComputersAvailable = in.readByte() != 0;
    }

    public static final Parcelable.Creator<RoomInformation> CREATOR = new Parcelable.Creator<RoomInformation>() {
        @Override
        public RoomInformation createFromParcel(Parcel source) {
            return new RoomInformation(source);
        }

        @Override
        public RoomInformation[] newArray(int size) {
            return new RoomInformation[size];
        }
    };
}
