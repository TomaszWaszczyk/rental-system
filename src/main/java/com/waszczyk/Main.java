package com.waszczyk;

import java.time.LocalDate;

import com.waszczyk.exception.NoCarAvailableException;
import com.waszczyk.model.CarType;
import com.waszczyk.service.CarRentalService;

/**
 * Demo of the car rental system.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Car Rental System Demo ===");
        
        CarRentalService service = new CarRentalService();
        
        // Add cars to fleet
        service.addCars(CarType.SEDAN, 3);
        service.addCars(CarType.SUV, 2);
        service.addCars(CarType.VAN, 1);
        
        System.out.println("Fleet initialized:");
        for (CarType type : CarType.values()) {
            System.out.printf("%s: %d cars%n", type, service.getTotalCount(type));
        }
        System.out.println("----------------------------");
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        System.out.println("Making reservations...");
        String res1 = service.makeReservation("CUST001", CarType.SEDAN, tomorrow, 3);
        System.out.println("Sedan reserved: " + res1);
        
        String res2 = service.makeReservation("CUST002", CarType.SUV, tomorrow, 5);
        System.out.println("SUV reserved: " + res2);
        
        // Test limits
        System.out.println("Testing car limits...");
        service.makeReservation("CUST003", CarType.VAN, tomorrow, 2);
        try {
        	service.makeReservation("CUST004", CarType.VAN, tomorrow, 2); // Should fail
        } catch (NoCarAvailableException e) {
            System.out.println("Van reservation correctly rejected - no more vans available");

        }
        
        // Show availability
        System.out.println("Current availability:");
        for (CarType type : CarType.values()) {
            System.out.printf("%s: %d/%d available%n", type, service.getAvailableCount(type), service.getTotalCount(type));
        }
        
        // Cancel reservation
        System.out.println("Cancelling sedan reservation...");
        service.cancelReservation(res1);
        System.out.printf("Sedans now available: %d%n", service.getAvailableCount(CarType.SEDAN));
        
        System.out.println("=== Demo Complete ===");
    }
}