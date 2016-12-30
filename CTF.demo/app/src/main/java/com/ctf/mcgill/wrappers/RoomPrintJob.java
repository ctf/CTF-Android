package com.ctf.mcgill.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctf.mcgill.enums.Room;
import com.ctf.mcgill.tepid.PrintJob;

/**
 * Created by Allan Wang on 28/12/2016.
 * <p>
 * Wrapper class for Room and PrintJob[]
 */

public class RoomPrintJob implements Parcelable {
    public final Room room;
    public PrintJob[] printJobs;

    public RoomPrintJob(Room room, PrintJob[] printJobs) {
        this.room = room;
        this.printJobs = printJobs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.room == null ? -1 : this.room.ordinal());
        dest.writeTypedArray(this.printJobs, flags);
    }

    protected RoomPrintJob(Parcel in) {
        int tmpRoom = in.readInt();
        this.room = tmpRoom == -1 ? null : Room.values()[tmpRoom];
        this.printJobs = in.createTypedArray(PrintJob.CREATOR);
    }

    public static final Parcelable.Creator<RoomPrintJob> CREATOR = new Parcelable.Creator<RoomPrintJob>() {
        @Override
        public RoomPrintJob createFromParcel(Parcel source) {
            return new RoomPrintJob(source);
        }

        @Override
        public RoomPrintJob[] newArray(int size) {
            return new RoomPrintJob[size];
        }
    };
}
