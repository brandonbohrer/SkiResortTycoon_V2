package com.skiresort.components.finances.core;

/**
 * Enumeration of different transaction types for financial tracking
 */
public enum TransactionType {
    REVENUE("Revenue"),
    EXPENSE("Expense"),
    LOAN_PROCEEDS("Loan Proceeds"),
    LOAN_PAYMENT("Loan Payment"),
    INITIAL_CAPITAL("Initial Capital"),
    EQUIPMENT_PURCHASE("Equipment Purchase"),
    FACILITY_CONSTRUCTION("Facility Construction"),
    MISSED_PAYMENT("Missed Payment"),
    LOAN_COMPLETION("Loan Completion"),
    BANKRUPTCY_WARNING("Bankruptcy Warning"),
    REFUND("Refund"),
    ADJUSTMENT("Adjustment"),
    OTHER("Other");
    
    private final String displayName;
    
    TransactionType(String displayName) {
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