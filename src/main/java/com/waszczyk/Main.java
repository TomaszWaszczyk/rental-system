package com.waszczyk;

import java.time.LocalDate;

import com.waszczyk.model.CarType;
import com.waszczyk.service.CarRentalService;

/**
 * Demo of the car rental system.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Car Rental System Demo ===\n");
        
        CarRentalService service = new CarRentalService();
        
        // Add cars to fleet
        service.addCars(CarType.SEDAN, 3);
        service.addCars(CarType.SUV, 2);
        service.addCars(CarType.VAN, 1);
        
        System.out.println("Fleet initialized:");
        for (CarType type : CarType.values()) {
            System.out.printf("  %s: %d cars%n", type, service.getTotalCount(type));
        }
        System.out.println("----------------------------");
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // Make reservations
        System.out.println("Making reservations...");
        String res1 = service.makeReservation("CUST001", CarType.SEDAN, tomorrow, 3);
        System.out.println("Sedan reserved: " + res1);
        
        String res2 = service.makeReservation("CUST002", CarType.SUV, tomorrow, 5);
        System.out.println("SUV reserved: " + res2);
        
        // Test limits
        System.out.println("\nTesting car limits...");
        service.makeReservation("CUST003", CarType.VAN, tomorrow, 2); // Use the only van
        String failedRes = service.makeReservation("CUST004", CarType.VAN, tomorrow, 2); // Should fail
        
        if (failedRes == null) {
            System.out.println("Van reservation correctly rejected - no more vans available");
        }
        
        // Show availability
        System.out.println("\nCurrent availability:");
        for (CarType type : CarType.values()) {
            System.out.printf("  %s: %d/%d available%n", type, service.getAvailableCount(type), service.getTotalCount(type));
        }
        
        // Cancel reservation
        System.out.println("\nCancelling sedan reservation...");
        service.cancelReservation(res1);
        System.out.printf("  Sedans now available: %d%n", service.getAvailableCount(CarType.SEDAN));
        
        System.out.println("\n=== Demo Complete ===");
    }
}