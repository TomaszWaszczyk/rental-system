package com.waszczyk.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.waszczyk.exception.NoCarAvailableException;
import com.waszczyk.model.CarType;
import com.waszczyk.model.Reservation;

/**
 * Tests for the car rental service.
 */
class CarRentalServiceTest {
    
    private CarRentalService service;
    private LocalDate tomorrow;
    
    @BeforeEach
    void setUp() {
        service = new CarRentalService();
        tomorrow = LocalDate.now().plusDays(1);
        
        // Add some cars
        service.addCars(CarType.SEDAN, 2);
        service.addCars(CarType.SUV, 1);
        service.addCars(CarType.VAN, 1);
    }
    
    @Test
    @DisplayName("Should allow reservation of each car type")
    void testBasicReservations() {
        // Test sedan reservation
        String sedanRes = service.makeReservation("CUST001", CarType.SEDAN, tomorrow, 3);
        assertNotNull(sedanRes);
        
        // Test SUV reservation
        String suvRes = service.makeReservation("CUST002", CarType.SUV, tomorrow, 5);
        assertNotNull(suvRes);
        
        // Test van reservation
        String vanRes = service.makeReservation("CUST003", CarType.VAN, tomorrow, 7);
        assertNotNull(vanRes);
    }
    
    @Test
    @DisplayName("Should enforce limited number of cars")
    void testCarLimits() {
        // Reserve all SUVs (only 1 available)
        String res1 = service.makeReservation("CUST001", CarType.SUV, tomorrow, 3);
        assertNotNull(res1);
        assertEquals(0, service.getAvailableCount(CarType.SUV));
        
        // Try to reserve another SUV - should throw exception
        NoCarAvailableException exception = assertThrows(NoCarAvailableException.class, () -> {
            service.makeReservation("CUST002", CarType.SUV, tomorrow, 3);
        });
        assertNotNull(exception);
    }
    
    @Test
    @DisplayName("Should handle reservation cancellation")
    void testCancellation() {
        // Make reservation
        String resId = service.makeReservation("CUST001", CarType.SEDAN, tomorrow, 3);
        assertNotNull(resId);
        assertEquals(1, service.getAvailableCount(CarType.SEDAN)); // 2 total - 1 reserved = 1 available
        
        // Cancel reservation
        boolean cancelled = service.cancelReservation(resId);
        assertTrue(cancelled);
        assertEquals(2, service.getAvailableCount(CarType.SEDAN)); // Back to 2 available
    }
    
    @Test
    @DisplayName("Should track customer reservations")
    void testCustomerReservations() {
        String customerId = "CUST001";
        
        // Make multiple reservations for same customer
        service.makeReservation(customerId, CarType.SEDAN, tomorrow, 3);
        service.makeReservation(customerId, CarType.VAN, tomorrow.plusDays(4), 2);
        
        List<Reservation> customerReservations = service.getCustomerReservations(customerId);
        assertEquals(2, customerReservations.size());
    }
    
    @Test
    @DisplayName("Should validate input parameters")
    void testInputValidation() {
        // Null customer ID
        assertThrows(IllegalArgumentException.class, () -> 
            service.makeReservation(null, CarType.SEDAN, tomorrow, 3));
        
        // Empty customer ID
        assertThrows(IllegalArgumentException.class, () -> 
            service.makeReservation("", CarType.SEDAN, tomorrow, 3));
        
        // Null car type
        assertNull(service.makeReservation("CUST001", null, tomorrow, 3));
        
        // Past date
        assertNull(service.makeReservation("CUST001", CarType.SEDAN, LocalDate.now().minusDays(1), 3));
        
        // Invalid number of days
        assertNull(service.makeReservation("CUST001", CarType.SEDAN, tomorrow, 0));
        assertNull(service.makeReservation("CUST001", CarType.SEDAN, tomorrow, -1));
    }

    @Test
    @DisplayName("Demonstrates the whole flow of rental system")
    void testRentalFlow() {
        
        // Use exactly 1 SUV 
        assertEquals(1, service.getTotalCount(CarType.SUV));
        assertEquals(1, service.getAvailableCount(CarType.SUV));
        
        // Step 1: Customer A books SUV for Dec 10-17 (8 days)
        String resA = service.makeReservation("customerA", CarType.SUV, LocalDate.of(2025, 12, 10), 8);
        assertNotNull(resA, "Customer A should get the SUV");
        assertEquals(0, service.getAvailableCount(CarType.SUV), "No SUVs left");

        // Step 2: Customer B tries to book overlapping dates Dec 14-20 - should throw exception
        NoCarAvailableException exception2 = assertThrows(NoCarAvailableException.class, () -> {
            service.makeReservation("customerB", CarType.SUV, LocalDate.of(2025, 12, 14), 7);
        });
        assertNotNull(exception2);

        // Step 3: Customer A cancels their reservation
        boolean cancelled = service.cancelReservation(resA);
        assertTrue(cancelled, "Customer A's cancellation should work");
        assertEquals(1, service.getAvailableCount(CarType.SUV), "SUV is available again");

        // Step 4: Customer B books for Dec 14-20 (succeeds because count = 1)
        String resB2 = service.makeReservation("customerB", CarType.SUV, LocalDate.of(2025, 12, 14), 7);
        assertNotNull(resB2, "Customer B should succeed now");
        assertEquals(0, service.getAvailableCount(CarType.SUV), "No SUVs left again");

        // Step 5: Customer A tries to rebook their original dates Dec 10-17 - should throw exception
        NoCarAvailableException exception = assertThrows(NoCarAvailableException.class, () -> {
            service.makeReservation("customerA", CarType.SUV, LocalDate.of(2025, 12, 10), 8);
        });
        assertNotNull(exception);
        
        System.out.println("Counting approach has limitations:");
        System.out.println("1. No way to check availability for specific date ranges");
        System.out.println("2. Vulnerable to race conditions in multi-threaded environment");
        System.out.println("SOLUTION NEEDED:");
        System.out.println("Implement date-range availability checking instead of simple counting");
        System.out.println("Example: isAvailableForDateRange(CarType.SUV, Dec5, 4) -> boolean");
    }
}
