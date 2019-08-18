package com.app.expensetracker.model;

public class Months {

    String monthname;
    String monthindex;

    public Months(String monthname, String monthindex) {
        this.monthname = monthname;
        this.monthindex = monthindex;
    }

    public String getMonthname() {
        return monthname;
    }

    public void setMonthname(String monthname) {
        this.monthname = monthname;
    }

    public String getMonthindex() {
        return monthindex;
    }

    public void setMonthindex(String monthindex) {
        this.monthindex = monthindex;
    }
}
