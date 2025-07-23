package com.skiresort.components.finances.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single financial transaction
 * Immutable record of financial activity for audit trail and analytics
 */
public class Transaction {
    private final TransactionType type;
    private final double amount;
    private final String description;
    private final LocalDateTime timestamp;
    private final RevenueSource revenueSource; // null for expenses
    
    public Transaction(TransactionType type, double amount, String description, 
                      LocalDateTime timestamp, RevenueSource revenueSource) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.revenueSource = revenueSource;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public RevenueSource getRevenueSource() {
        return revenueSource;
    }
    
    public boolean isRevenue() {
        return amount > 0;
    }
    
    public boolean isExpense() {
        return amount < 0;
    }
    
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    }
    
    public String getFormattedAmount() {
        return String.format("$%.2f", Math.abs(amount));
    }
    
    @Override
    public String toString() {
        String sign = isRevenue() ? "+" : "-";
        return String.format("%s %s%s - %s (%s)", 
            getFormattedTimestamp(), sign, getFormattedAmount(), description, type);
    }
} 