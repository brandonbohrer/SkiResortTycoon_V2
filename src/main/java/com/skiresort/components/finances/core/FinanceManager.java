package com.skiresort.components.finances.core;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.guests.core.Guest;
import com.skiresort.components.guests.core.GuestManager;
import java.util.*;
import java.time.LocalDateTime;

/**
 * Manages all financial aspects of the ski resort
 * Designed with extensibility for complex economic features:
 * - Dynamic pricing based on demand/satisfaction
 * - Seasonal revenue variations
 * - Complex loan and investment systems
 * - Staff wages and benefits
 * - Marketing and advertising costs
 * - Insurance and regulatory expenses
 * 
 * Balanced for challenging but fair progression - you start poor!
 */
public class FinanceManager {
    
    // Current financial state
    private double currentMoney;
    private double totalRevenue;
    private double totalExpenses;
    private double dailyRevenue;
    private double dailyExpenses;
    
    // Loans and debt (extensible for complex loan products)
    private final List<Loan> activeLoans;
    private double totalDebt;
    private double creditRating; // 0-100, affects loan terms
    
    // Revenue tracking (extensible for detailed analytics)
    private final Map<RevenueSource, Double> revenueBySource;
    private final Map<ExpenseCategory, Double> expensesByCategory;
    private final List<Transaction> transactionHistory;
    
    // Pricing configuration (extensible for complex pricing strategies)
    private double baseLiftTicketPrice;
    private double demandMultiplier;        // Based on guest satisfaction
    private double seasonalMultiplier;      // Future: seasonal pricing
    private double weatherMultiplier;       // Future: weather effects
    private double competitionMultiplier;   // Future: competitor pricing
    
    // Operating costs (extensible for detailed cost modeling)
    private double dailyBaseCosts;         // Base resort operations
    private double dailyStaffCosts;        // Future: detailed staff system
    private double dailyUtilityCosts;      // Future: realistic utility costs
    private double dailyMarketingCosts;    // Future: marketing system
    private double dailyInsuranceCosts;    // Future: insurance and safety
    
    // Financial configuration (tuned for challenging progression)
    private static final double STARTING_MONEY = 20000.0;     // Barely enough for basic setup
    private static final double BANKRUPTCY_THRESHOLD = -5000.0; // Game over threshold
    private static final double DAILY_BASE_COSTS = 200.0;      // Base operating costs
    private static final double BASE_LIFT_TICKET_PRICE = 45.0; // Starting ticket price
    
    // Loan configuration (extensible for multiple loan products)
    private static final double STARTER_LOAN_AMOUNT = 25000.0;
    private static final double STARTER_LOAN_RATE = 0.12;      // 12% annual - expensive!
    private static final int STARTER_LOAN_TERM_DAYS = 365;     // 1 year to pay back
    
    public FinanceManager() {
        // Initialize with challenging starting conditions
        this.currentMoney = STARTING_MONEY;
        this.totalRevenue = 0.0;
        this.totalExpenses = 0.0;
        this.dailyRevenue = 0.0;
        this.dailyExpenses = 0.0;
        
        // Initialize debt tracking
        this.activeLoans = new ArrayList<>();
        this.totalDebt = 0.0;
        this.creditRating = 75.0; // Start with decent credit
        
        // Initialize tracking systems
        this.revenueBySource = new EnumMap<>(RevenueSource.class);
        this.expensesByCategory = new EnumMap<>(ExpenseCategory.class);
        this.transactionHistory = new ArrayList<>();
        
        // Initialize pricing with conservative defaults
        this.baseLiftTicketPrice = BASE_LIFT_TICKET_PRICE;
        this.demandMultiplier = 1.0;
        this.seasonalMultiplier = 1.0;
        this.weatherMultiplier = 1.0;
        this.competitionMultiplier = 1.0;
        
        // Initialize operating costs
        this.dailyBaseCosts = DAILY_BASE_COSTS;
        this.dailyStaffCosts = 0.0;      // No staff initially
        this.dailyUtilityCosts = 50.0;   // Basic utilities
        this.dailyMarketingCosts = 0.0;  // No marketing budget initially
        this.dailyInsuranceCosts = 75.0; // Basic insurance required
        
        // Log initial state as both transaction and revenue
        recordTransaction(TransactionType.INITIAL_CAPITAL, STARTING_MONEY, 
            "Initial resort capital", RevenueSource.INITIAL_INVESTMENT);
        revenueBySource.put(RevenueSource.INITIAL_INVESTMENT, STARTING_MONEY);
    }
    
    /**
     * Process daily financial operations
     * Called once per game day
     */
    public void processDailyFinances(Mountain mountain, GuestManager guestManager) {
        // Reset daily counters
        dailyRevenue = 0.0;
        dailyExpenses = 0.0;
        
        // Process revenue from guests
        processGuestRevenue(guestManager);
        
        // Process operating expenses
        processOperatingExpenses(mountain);
        
        // Process loan payments
        processLoanPayments();
        
        // Update financial state
        double netDaily = dailyRevenue - dailyExpenses;
        currentMoney += netDaily;
        totalRevenue += dailyRevenue;
        totalExpenses += dailyExpenses;
        
        // Update credit rating based on financial performance
        updateCreditRating(netDaily);
        
        // Check for bankruptcy
        if (currentMoney < BANKRUPTCY_THRESHOLD) {
            // In a full game, this would trigger game over
            recordTransaction(TransactionType.BANKRUPTCY_WARNING, 0.0, 
                "WARNING: Approaching bankruptcy!", null);
        }
    }
    
    /**
     * Process revenue from guests using facilities
     */
    private void processGuestRevenue(GuestManager guestManager) {
        for (Guest guest : guestManager.getActiveGuests()) {
            // Calculate what this guest should pay based on usage and satisfaction
            double guestPayment = calculateGuestPayment(guest);
            
            if (guestPayment > 0) {
                addRevenue(guestPayment, RevenueSource.LIFT_TICKETS, 
                    "Guest " + guest.getName() + " payment");
                
                // Track guest spending
                guest.addSpending(guestPayment);
            }
        }
    }
    
    /**
     * Calculate how much a guest should pay based on usage and satisfaction
     */
    private double calculateGuestPayment(Guest guest) {
        // Base payment calculation (extensible for complex pricing)
        double basePrice = getCurrentLiftTicketPrice();
        
        // Adjust for guest's willingness to pay
        double willingnessMultiplier = guest.getWillingnessToPayMultiplier();
        
        // Only charge if guest has used facilities today (simplified)
        if (guest.getState() == Guest.GuestState.SKIING_SLOPE || 
            guest.getState() == Guest.GuestState.RIDING_LIFT) {
            
            // Daily payment model (could be per-ride in the future)
            return basePrice * willingnessMultiplier * 0.1; // 10% of daily ticket per use
        }
        
        return 0.0;
    }
    
    /**
     * Get current lift ticket price with all multipliers applied
     */
    public double getCurrentLiftTicketPrice() {
        return baseLiftTicketPrice * demandMultiplier * seasonalMultiplier * 
               weatherMultiplier * competitionMultiplier;
    }
    
    /**
     * Process all operating expenses
     */
    private void processOperatingExpenses(Mountain mountain) {
        // Base resort operations
        addExpense(dailyBaseCosts, ExpenseCategory.BASE_OPERATIONS, 
            "Daily base resort operations");
        
        // Utilities
        addExpense(dailyUtilityCosts, ExpenseCategory.UTILITIES, 
            "Daily utilities");
        
        // Insurance
        addExpense(dailyInsuranceCosts, ExpenseCategory.INSURANCE, 
            "Daily insurance costs");
        
        // Lift operating costs
        processLiftOperatingCosts(mountain);
        
        // Slope maintenance costs
        processSlopeMaintenanceCosts(mountain);
        
        // Staff costs (if any)
        if (dailyStaffCosts > 0) {
            addExpense(dailyStaffCosts, ExpenseCategory.STAFF_WAGES, 
                "Daily staff wages");
        }
        
        // Marketing costs (if any)
        if (dailyMarketingCosts > 0) {
            addExpense(dailyMarketingCosts, ExpenseCategory.MARKETING, 
                "Daily marketing expenses");
        }
    }
    
    /**
     * Process lift operating costs
     */
    private void processLiftOperatingCosts(Mountain mountain) {
        for (Lift lift : mountain.getLifts()) {
            if (lift.isOperational()) {
                // Daily operating cost is annual cost / 365
                double dailyCost = lift.getOperationalCost() / 365.0;
                addExpense(dailyCost, ExpenseCategory.LIFT_OPERATIONS, 
                    "Operating " + lift.getName());
            }
        }
    }
    
    /**
     * Process slope maintenance costs
     */
    private void processSlopeMaintenanceCosts(Mountain mountain) {
        for (Slope slope : mountain.getSlopes()) {
            if (slope.isOpen()) {
                // Daily maintenance cost is annual cost / 365
                double dailyCost = slope.getMaintenanceCost() / 365.0;
                addExpense(dailyCost, ExpenseCategory.SLOPE_MAINTENANCE, 
                    "Maintaining " + slope.getName());
            }
        }
    }
    
    /**
     * Process loan payments
     */
    private void processLoanPayments() {
        Iterator<Loan> loanIterator = activeLoans.iterator();
        while (loanIterator.hasNext()) {
            Loan loan = loanIterator.next();
            
            double dailyPayment = loan.getDailyPayment();
            
            if (currentMoney >= dailyPayment) {
                // Make payment
                addExpense(dailyPayment, ExpenseCategory.LOAN_PAYMENTS, 
                    "Loan payment: " + loan.getPurpose());
                
                loan.makePayment(dailyPayment);
                totalDebt -= (dailyPayment - loan.getDailyInterest());
                
                // Remove completed loans
                if (loan.isFullyPaid()) {
                    loanIterator.remove();
                    recordTransaction(TransactionType.LOAN_COMPLETION, 0.0, 
                        "Loan completed: " + loan.getPurpose(), null);
                }
            } else {
                // Missed payment - impact credit rating
                creditRating = Math.max(0, creditRating - 5.0);
                recordTransaction(TransactionType.MISSED_PAYMENT, dailyPayment, 
                    "MISSED PAYMENT: " + loan.getPurpose(), null);
            }
        }
    }
    
    /**
     * Take out a loan for resort expansion
     */
    public boolean takeLoan(double amount, String purpose) {
        // Check if eligible for loan
        if (creditRating < 30) {
            return false; // Credit too poor
        }
        
        if (totalDebt > currentMoney * 5) {
            return false; // Too much existing debt
        }
        
        // Calculate loan terms based on credit rating
        double interestRate = calculateLoanInterestRate(amount);
        int termDays = calculateLoanTerm(amount);
        
        Loan newLoan = new Loan(amount, interestRate, termDays, purpose);
        activeLoans.add(newLoan);
        totalDebt += amount;
        
        // Add money to account
        addRevenue(amount, RevenueSource.LOANS, "Loan: " + purpose);
        
        // Slightly reduce credit rating (taking on debt)
        creditRating = Math.max(0, creditRating - 2.0);
        
        return true;
    }
    
    /**
     * Calculate loan interest rate based on credit rating and amount
     */
    private double calculateLoanInterestRate(double amount) {
        double baseRate = STARTER_LOAN_RATE;
        
        // Better credit = lower rate
        double creditAdjustment = (75.0 - creditRating) / 100.0; // 0 to 0.75
        
        // Larger loans = higher risk = higher rate
        double amountAdjustment = Math.min(0.05, amount / 100000.0); // Up to 5% extra
        
        return baseRate + creditAdjustment + amountAdjustment;
    }
    
    /**
     * Calculate loan term based on amount
     */
    private int calculateLoanTerm(double amount) {
        // Larger loans get longer terms
        return Math.max(180, Math.min(1095, (int)(amount / 100.0))); // 6 months to 3 years
    }
    
    /**
     * Check if resort can afford a purchase
     */
    public boolean canAfford(double cost) {
        return cost >= 0 && currentMoney >= cost;
    }
    
    /**
     * Make a purchase (building, equipment, etc.)
     */
    public boolean makePurchase(double cost, String description, ExpenseCategory category) {
        if (cost <= 0 || !canAfford(cost)) {
            return false;
        }
        
        addExpense(cost, category, description);
        return true;
    }
    
    /**
     * Add revenue and track it
     */
    private void addRevenue(double amount, RevenueSource source, String description) {
        currentMoney += amount;
        dailyRevenue += amount;
        totalRevenue += amount;
        
        revenueBySource.merge(source, amount, Double::sum);
        recordTransaction(TransactionType.REVENUE, amount, description, source);
    }
    
    /**
     * Add expense and track it
     */
    private void addExpense(double amount, ExpenseCategory category, String description) {
        currentMoney -= amount;
        dailyExpenses += amount;
        totalExpenses += amount;
        
        expensesByCategory.merge(category, amount, Double::sum);
        recordTransaction(TransactionType.EXPENSE, -amount, description, null);
    }
    
    /**
     * Record a transaction for history/analytics
     */
    private void recordTransaction(TransactionType type, double amount, String description, 
                                 RevenueSource revenueSource) {
        Transaction transaction = new Transaction(type, amount, description, 
            LocalDateTime.now(), revenueSource);
        transactionHistory.add(transaction);
        
        // Keep transaction history manageable
        if (transactionHistory.size() > 1000) {
            transactionHistory.remove(0);
        }
    }
    
    /**
     * Update credit rating based on financial performance
     */
    private void updateCreditRating(double netDaily) {
        if (netDaily > 0) {
            // Profitable day - slowly improve credit
            creditRating = Math.min(100, creditRating + 0.1);
        } else if (netDaily < -100) {
            // Bad loss - damage credit
            creditRating = Math.max(0, creditRating - 1.0);
        }
        
        // Debt ratio affects credit
        if (currentMoney > 0) {
            double debtRatio = totalDebt / currentMoney;
            if (debtRatio > 3.0) {
                creditRating = Math.max(0, creditRating - 0.5);
            } else if (debtRatio < 1.0) {
                creditRating = Math.min(100, creditRating + 0.1);
            }
        }
    }
    
    /**
     * Update demand multiplier based on guest satisfaction
     */
    public void updateDemandMultiplier(double averageGuestSatisfaction) {
        // High satisfaction = guests willing to pay more
        // Low satisfaction = need to reduce prices to attract guests
        demandMultiplier = 0.5 + (averageGuestSatisfaction / 100.0); // 0.5 to 1.5
    }
    
    // Getters for financial state
    public double getCurrentMoney() { return currentMoney; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getTotalExpenses() { return totalExpenses; }
    public double getDailyRevenue() { return dailyRevenue; }
    public double getDailyExpenses() { return dailyExpenses; }
    public double getNetWorth() { return currentMoney - totalDebt; }
    public double getTotalDebt() { return totalDebt; }
    public double getCreditRating() { return creditRating; }
    public double getCurrentLiftTicketPriceDisplay() { return getCurrentLiftTicketPrice(); }
    public boolean isBankrupt() { return currentMoney < BANKRUPTCY_THRESHOLD; }
    
    // Getters for analytics
    public Map<RevenueSource, Double> getRevenueBySource() { return new EnumMap<>(revenueBySource); }
    public Map<ExpenseCategory, Double> getExpensesByCategory() { return new EnumMap<>(expensesByCategory); }
    public List<Transaction> getRecentTransactions(int count) { 
        int start = Math.max(0, transactionHistory.size() - count);
        return new ArrayList<>(transactionHistory.subList(start, transactionHistory.size()));
    }
    public List<Loan> getActiveLoans() { return new ArrayList<>(activeLoans); }
    
    // Configuration setters (for future expansion)
    public void setSeasonalMultiplier(double multiplier) { this.seasonalMultiplier = multiplier; }
    public void setWeatherMultiplier(double multiplier) { this.weatherMultiplier = multiplier; }
    public void setCompetitionMultiplier(double multiplier) { this.competitionMultiplier = multiplier; }
    public void setDailyStaffCosts(double costs) { this.dailyStaffCosts = costs; }
    public void setDailyMarketingCosts(double costs) { this.dailyMarketingCosts = costs; }
    
    @Override
    public String toString() {
        return String.format("Finance{money=$%.0f, debt=$%.0f, netWorth=$%.0f, credit=%.0f}", 
            currentMoney, totalDebt, getNetWorth(), creditRating);
    }
} 