package com.waszczyk.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        
        // Try to reserve another SUV - should fail
        String res2 = service.makeReservation("CUST002", CarType.SUV, tomorrow, 3);
        assertNull(res2);
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
        assertNull(service.makeReservation(null, CarType.SEDAN, tomorrow, 3));
        
        // Empty customer ID
        assertNull(service.makeReservation("", CarType.SEDAN, tomorrow, 3));
        
        // Null car type
        assertNull(service.makeReservation("CUST001", null, tomorrow, 3));
        
        // Past date
        assertNull(service.makeReservation("CUST001", CarType.SEDAN, LocalDate.now().minusDays(1), 3));
        
        // Invalid number of days
        assertNull(service.makeReservation("CUST001", CarType.SEDAN, tomorrow, 0));
        assertNull(service.makeReservation("CUST001", CarType.SEDAN, tomorrow, -1));
    }
}