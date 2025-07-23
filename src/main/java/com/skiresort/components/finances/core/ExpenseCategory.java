package com.skiresort.components.finances.core;

/**
 * Enumeration of different expense categories for the ski resort
 * Extensible for detailed cost tracking and budget analysis
 */
public enum ExpenseCategory {
    LIFT_OPERATIONS("Lift Operations"),
    SLOPE_MAINTENANCE("Slope Maintenance"),
    SNOWMAKING("Snowmaking"),
    GROOMING("Slope Grooming"),
    BASE_OPERATIONS("Base Operations"),
    STAFF_WAGES("Staff Wages"),
    STAFF_BENEFITS("Staff Benefits"),
    UTILITIES("Utilities"),
    INSURANCE("Insurance"),
    MARKETING("Marketing"),
    ADVERTISING("Advertising"),
    EQUIPMENT_PURCHASE("Equipment Purchase"),
    EQUIPMENT_MAINTENANCE("Equipment Maintenance"),
    FACILITY_CONSTRUCTION("Facility Construction"),
    FACILITY_MAINTENANCE("Facility Maintenance"),
    LOAN_PAYMENTS("Loan Payments"),
    TAXES("Taxes"),
    LICENSES_PERMITS("Licenses & Permits"),
    PROFESSIONAL_SERVICES("Professional Services"),
    SUPPLIES("Supplies"),
    FOOD_BEVERAGE_COSTS("Food & Beverage Costs"),
    RETAIL_INVENTORY("Retail Inventory"),
    SECURITY("Security"),
    FIRST_AID("First Aid & Safety"),
    ENVIRONMENTAL("Environmental Compliance"),
    OTHER("Other Expenses");
    
    private final String displayName;
    
    ExpenseCategory(String displayName) {
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