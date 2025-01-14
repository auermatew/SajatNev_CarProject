package com.example.carproject;

import java.time.Year;

public class Car {
    private String licensePlateNumber;
    private String name;
    private int year;
    private static final int MIN_YEAR = 1950;
    private static final int CURRENT_YEAR = Year.now().getValue();

    public Car(String licensePlateNumber, String name, int year) {
        if (year < MIN_YEAR || year > CURRENT_YEAR) {
            throw new IllegalArgumentException("Year must be between " + MIN_YEAR + " and " + CURRENT_YEAR);
        }
        this.licensePlateNumber = licensePlateNumber;
        this.name = name;
        this.year = year;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }
}