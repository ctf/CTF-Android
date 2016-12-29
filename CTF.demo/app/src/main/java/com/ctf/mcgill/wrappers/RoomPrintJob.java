package com.ctf.mcgill.wrappers;

import com.ctf.mcgill.enums.Room;
import com.ctf.mcgill.tepid.PrintJob;

/**
 * Created by Allan Wang on 28/12/2016.
 * <p>
 * Wrapper class for Room and PrintJob[]
 */

public class RoomPrintJob {
    public final Room room;
    public PrintJob[] printJobs;

    public RoomPrintJob(Room room, PrintJob[] printJobs) {
        this.room = room;
        this.printJobs = printJobs;
    }
}
