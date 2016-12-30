package com.ctf.mcgill.enums;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * Valid rooms and their respective names
 */

public enum Room {
    _1B16("1B16"), _1B17("1B17"), _1B18("1B18");

    private String name;

    Room(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }
}
