package edu.uci.ics.mobile;

public class Movie {
    private String name;
    private short year;

    public Movie(String name, short year) {
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }
}