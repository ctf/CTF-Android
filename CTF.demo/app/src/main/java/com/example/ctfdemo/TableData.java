package com.example.ctfdemo;

import android.widget.TableRow;

/**
 * Created by erasmas on 1/8/16.
 */
// data object used by the table row adapter to fill the recent jobs tables with user and date info
public class TableData {
    private String user;
    private String date;
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
