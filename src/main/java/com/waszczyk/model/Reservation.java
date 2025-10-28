package com.waszczyk.model;

import java.time.LocalDate;


public class Reservation {
    private final String id;
    private final String customerId;
    private final CarType carType;
    private final LocalDate startDate;
    private final int days;
    private boolean cancelled;

    public Reservation(String id, String customerId, CarType carType, LocalDate startDate, int days) {
        this.id = id;
        this.customerId = customerId;
        this.carType = carType;
        this.startDate = startDate;
        this.days = days;
        this.cancelled = false;
    }
    
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public CarType getCarType() { return carType; }
    public LocalDate getStartDate() { return startDate; }
    public int getDays() { return days; }
    public boolean isCancelled() { return cancelled; }
    
    public void cancel() { 
        this.cancelled = true; 
    }
    
    @Override
    public String toString() {
        return String.format("Reservation{id='%s', customer='%s', type=%s, start=%s, days=%d, cancelled=%s}",
                           id, customerId, carType, startDate, days, cancelled);
    }
}