package com.waszczyk.model;


public enum CarType {
    SEDAN("Sedan"),
    SUV("SUV"), 
    VAN("Van");
    
    private final String displayName;
    
    CarType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}