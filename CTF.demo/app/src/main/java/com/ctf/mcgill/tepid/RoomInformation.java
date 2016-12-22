package com.ctf.mcgill.tepid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allan Wang on 2016-12-22.
 */

public class RoomInformation {
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

    public static class Printer {
        public final boolean isUp;
        public final String name;

        Printer(String name, boolean isUp) {
            this.isUp = isUp;
            this.name = name;
        }
    }


}
