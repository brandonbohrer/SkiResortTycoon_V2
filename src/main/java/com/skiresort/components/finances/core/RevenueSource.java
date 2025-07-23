package com.skiresort.components.finances.core;

/**
 * Enumeration of different revenue sources for the ski resort
 * Extensible for detailed revenue tracking and analytics
 */
public enum RevenueSource {
    LIFT_TICKETS("Lift Tickets"),
    SLOPE_PASSES("Slope Passes"),
    EQUIPMENT_RENTAL("Equipment Rental"),
    FOOD_BEVERAGE("Food & Beverage"),
    LODGING("Lodging"),
    LESSONS("Ski Lessons"),
    PARKING("Parking Fees"),
    RETAIL("Retail Sales"),
    SEASON_PASSES("Season Passes"),
    GROUP_SALES("Group Sales"),
    EVENTS("Special Events"),
    ADVERTISING("Advertising Revenue"),
    SPONSORSHIPS("Sponsorships"),
    LOANS("Loan Proceeds"),
    INVESTMENTS("Investment Income"),
    INITIAL_INVESTMENT("Initial Capital"),
    OTHER("Other Revenue");
    
    private final String displayName;
    
    RevenueSource(String displayName) {
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