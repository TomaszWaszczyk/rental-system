package com.waszczyk.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.waszczyk.exception.NoCarAvailableException;
import com.waszczyk.model.CarType;
import com.waszczyk.model.Reservation;


public class CarRentalService {
    private final Map<CarType, Integer> totalCars = new HashMap<>();
    private final Map<CarType, Integer> availableCars = new HashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();
    
    public CarRentalService() {
        // Initialize all car types with 0 cars
        for (CarType type : CarType.values()) {
            totalCars.put(type, 0);
            availableCars.put(type, 0);
        }
    }
    
    /**
     * Add cars of a specific type to the fleet.
     */
    public void addCars(CarType type, int count) {
        totalCars.put(type, totalCars.get(type) + count);
        availableCars.put(type, availableCars.get(type) + count);
    }
    
    /**
     * Make a reservation - returns reservation ID if successful.
     * @throws NoCarAvailableException 
     */
    public String makeReservation(String customerId, CarType carType, LocalDate startDate, int days) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (carType == null || startDate == null || days <= 0) return null;
        if (startDate.isBefore(LocalDate.now())) return null;
        
        // Check if any cars are available
        if (availableCars.get(carType) <= 0) {
            throw new NoCarAvailableException("No cars available for type:" + carType);
        }
        
        // Create reservation
        String reservationId = "RES-" + System.currentTimeMillis();
        Reservation reservation = new Reservation(reservationId, customerId, carType, startDate, days);
        
        reservations.add(reservation);
        availableCars.put(carType, availableCars.get(carType) - 1);
        
        return reservationId;
    }
    
    /**
     * Cancel a reservation.
     */
    public boolean cancelReservation(String reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(reservationId) && !reservation.isCancelled()) {
                reservation.cancel();
                availableCars.put(reservation.getCarType(), 
                    availableCars.get(reservation.getCarType()) + 1);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get available car count for a type.
     */
    public int getAvailableCount(CarType type) {
        return availableCars.get(type);
    }
    
    /**
     * Get total car count for a type.
     */
    public int getTotalCount(CarType type) {
        return totalCars.get(type);
    }
    
    /**
     * Get reservations for a customer.
     */
    public List<Reservation> getCustomerReservations(String customerId) {
        return reservations.stream()
            .filter(reservation -> reservation.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
}