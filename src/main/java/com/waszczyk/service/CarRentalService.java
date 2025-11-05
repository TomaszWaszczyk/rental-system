package com.waszczyk.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.waszczyk.model.CarType;
import com.waszczyk.model.Reservation;


public class CarRentalService {
    // Counters for each car type
    private final Map<CarType, Integer> carCounts = new HashMap<>();
    private final Map<CarType, Integer> availableCounts = new HashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();
    
    public CarRentalService() {
        // Initialize all car types with 0 cars
        for (CarType type : CarType.values()) {
            carCounts.put(type, 0);
            availableCounts.put(type, 0);
        }
    }
    
    /**
     * Add cars of a specific type to the fleet.
     */
    public void addCars(CarType type, int count) {
        carCounts.put(type, carCounts.get(type) + count);
        availableCounts.put(type, availableCounts.get(type) + count);
    }
    
    /**
     * Make a reservation - returns reservation ID if successful, null if failed.
     */
    public String makeReservation(String customerId, CarType carType, LocalDate startDate, int days) {
        // As simple validation as possible
        if (customerId == null || customerId.trim().isEmpty()) return null;
        if (carType == null || startDate == null || days <= 0) return null;
        if (startDate.isBefore(LocalDate.now())) return null;
        
        // Check if any cars are available
        if (availableCounts.get(carType) <= 0) {
            return null; // No cars available
        }
        
        // Create reservation
        String reservationId = "RES-" + System.currentTimeMillis();
        Reservation reservation = new Reservation(reservationId, customerId, carType, startDate, days);
        
        reservations.add(reservation);
        availableCounts.put(carType, availableCounts.get(carType) - 1);
        
        return reservationId;
    }
    
    /**
     * Cancel a reservation.
     */
    public boolean cancelReservation(String reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(reservationId) && !reservation.isCancelled()) {
                reservation.cancel();
                availableCounts.put(reservation.getCarType(), 
                    availableCounts.get(reservation.getCarType()) + 1);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get available car count for a type.
     */
    public int getAvailableCount(CarType type) {
        return availableCounts.get(type);
    }
    
    /**
     * Get total car count for a type.
     */
    public int getTotalCount(CarType type) {
        return carCounts.get(type);
    }
    
    /**
     * Get reservations for a customer.
     */
    public List<Reservation> getCustomerReservations(String customerId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getCustomerId().equals(customerId)) {
                result.add(reservation);
            }
        }
        return result;
    }
}