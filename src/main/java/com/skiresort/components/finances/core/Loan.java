package com.skiresort.components.finances.core;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a loan taken by the ski resort
 * Tracks principal, interest, payments, and loan status
 */
public class Loan {
    private final String id;
    private final double originalAmount;
    private final double annualInterestRate;
    private final int termDays;
    private final String purpose;
    private final LocalDate startDate;
    
    private double remainingBalance;
    private double totalInterestPaid;
    private int daysRemaining;
    
    public Loan(double amount, double annualInterestRate, int termDays, String purpose) {
        this.id = UUID.randomUUID().toString();
        this.originalAmount = amount;
        this.annualInterestRate = annualInterestRate;
        this.termDays = termDays;
        this.purpose = purpose;
        this.startDate = LocalDate.now();
        
        this.remainingBalance = amount;
        this.totalInterestPaid = 0.0;
        this.daysRemaining = termDays;
    }
    
    /**
     * Calculate daily payment amount (principal + interest)
     */
    public double getDailyPayment() {
        if (daysRemaining <= 0) {
            return 0.0;
        }
        
        // Simple daily payment calculation
        double dailyInterestRate = annualInterestRate / 365.0;
        double principalPayment = remainingBalance / daysRemaining;
        double interestPayment = remainingBalance * dailyInterestRate;
        
        return principalPayment + interestPayment;
    }
    
    /**
     * Calculate daily interest amount
     */
    public double getDailyInterest() {
        double dailyInterestRate = annualInterestRate / 365.0;
        return remainingBalance * dailyInterestRate;
    }
    
    /**
     * Make a payment on the loan
     */
    public void makePayment(double payment) {
        if (remainingBalance <= 0 || daysRemaining <= 0) {
            return;
        }
        
        double interestPortion = getDailyInterest();
        double principalPortion = Math.min(payment - interestPortion, remainingBalance);
        
        totalInterestPaid += interestPortion;
        remainingBalance -= principalPortion;
        daysRemaining--;
        
        if (remainingBalance < 0.01) { // Close enough to zero
            remainingBalance = 0.0;
        }
    }
    
    /**
     * Check if loan is fully paid off
     */
    public boolean isFullyPaid() {
        return remainingBalance <= 0.01 || daysRemaining <= 0;
    }
    
    /**
     * Calculate total amount that will be paid over life of loan
     */
    public double getTotalPayment() {
        return getDailyPayment() * termDays;
    }
    
    /**
     * Calculate total interest over life of loan
     */
    public double getTotalInterest() {
        return getTotalPayment() - originalAmount;
    }
    
    /**
     * Calculate percentage of loan paid off
     */
    public double getPercentPaid() {
        return ((originalAmount - remainingBalance) / originalAmount) * 100.0;
    }
    
    /**
     * Get loan status summary
     */
    public String getStatusSummary() {
        if (isFullyPaid()) {
            return "PAID OFF";
        } else {
            return String.format("%.1f%% paid, %d days remaining", getPercentPaid(), daysRemaining);
        }
    }
    
    // Getters
    public String getId() { return id; }
    public double getOriginalAmount() { return originalAmount; }
    public double getAnnualInterestRate() { return annualInterestRate; }
    public int getTermDays() { return termDays; }
    public String getPurpose() { return purpose; }
    public LocalDate getStartDate() { return startDate; }
    public double getRemainingBalance() { return remainingBalance; }
    public double getTotalInterestPaid() { return totalInterestPaid; }
    public int getDaysRemaining() { return daysRemaining; }
    
    @Override
    public String toString() {
        return String.format("Loan{purpose='%s', remaining=$%.0f, rate=%.1f%%, %s}", 
            purpose, remainingBalance, annualInterestRate * 100, getStatusSummary());
    }
} 