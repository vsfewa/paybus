package com.example.pay;

public class Record {
    private String time;
    private String location;

    public Record(String time, String location) {
        this.time = time;
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Record(){}
}
