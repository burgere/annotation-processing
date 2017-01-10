package io.rama.vehicle;

import io.rama.builder.Builder;

@Builder
public class Vehicle {
    private String make;
    private String model;
    private int year;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
            "make='" + make + '\'' +
            ", model='" + model + '\'' +
            ", year=" + year +
            '}';
    }
}
