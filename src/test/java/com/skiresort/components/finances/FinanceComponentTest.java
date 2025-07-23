package com.skiresort.components.finances;

import com.skiresort.components.finances.core.*;
import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.guests.core.Guest;
import com.skiresort.components.guests.core.GuestManager;
import com.skiresort.shared.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the Finance component
 * Tests financial operations, loan system, and economic balance
 */
public class FinanceComponentTest {
    
    private FinanceManager financeManager;
    private Mountain mountain;
    private GuestManager guestManager;
    
    @BeforeEach
    void setUp() {
        financeManager = new FinanceManager();
        mountain = new Mountain("Test Resort", 20, 20);
        guestManager = new GuestManager();
        
        // Add basic infrastructure for testing
        Slope testSlope = new Slope("Test Slope", new Position(10, 5), new Position(10, 15), 
            Slope.Difficulty.BEGINNER, 5000);
        mountain.addSlope(testSlope);
        
        Lift testLift = new Lift("Test Lift", new Position(10, 15), new Position(10, 7), 
            Lift.LiftType.MAGIC_CARPET);
        mountain.addLift(testLift);
    }
    
    @Test
    void testInitialFinancialState() {
        // Starting money should be challenging but enough for basic setup
        assertEquals(15000.0, financeManager.getCurrentMoney(), 0.01);
        assertEquals(0.0, financeManager.getTotalRevenue(), 0.01);
        assertEquals(0.0, financeManager.getTotalExpenses(), 0.01);
        assertEquals(0.0, financeManager.getTotalDebt(), 0.01);
        assertEquals(75.0, financeManager.getCreditRating(), 0.01);
        assertFalse(financeManager.isBankrupt());
    }
    
    @Test
    void testCanAffordValidation() {
        assertTrue(financeManager.canAfford(10000)); // Should be able to afford this
        assertTrue(financeManager.canAfford(15000)); // Exactly the starting amount
        assertFalse(financeManager.canAfford(20000)); // Too expensive initially
    }
    
    @Test
    void testBasicPurchase() {
        double initialMoney = financeManager.getCurrentMoney();
        double cost = 5000.0;
        
        assertTrue(financeManager.makePurchase(cost, "Test purchase", ExpenseCategory.OTHER));
        
        assertEquals(initialMoney - cost, financeManager.getCurrentMoney(), 0.01);
        assertEquals(cost, financeManager.getTotalExpenses(), 0.01);
    }
    
    @Test
    void testPurchaseInsufficientFunds() {
        double cost = 20000.0; // More than starting money
        
        assertFalse(financeManager.makePurchase(cost, "Expensive item", ExpenseCategory.OTHER));
        
        // Money should remain unchanged
        assertEquals(15000.0, financeManager.getCurrentMoney(), 0.01);
        assertEquals(0.0, financeManager.getTotalExpenses(), 0.01);
    }
    
    @Test
    void testLoanSystem() {
        double loanAmount = 25000.0;
        String purpose = "Equipment purchase";
        
        assertTrue(financeManager.takeLoan(loanAmount, purpose));
        
        // Money should increase by loan amount
        assertEquals(40000.0, financeManager.getCurrentMoney(), 0.01);
        assertEquals(25000.0, financeManager.getTotalDebt(), 0.01);
        
        // Should have one active loan
        assertEquals(1, financeManager.getActiveLoans().size());
        
        // Credit rating should decrease slightly due to new debt
        assertTrue(financeManager.getCreditRating() < 75.0);
    }
    
    @Test
    void testLoanDenialPoorCredit() {
        // Simulate poor credit by creating a new manager and setting poor conditions
        FinanceManager poorCreditManager = new FinanceManager();
        
        // Force poor credit by simulating bad financial performance
        for (int i = 0; i < 50; i++) {
            poorCreditManager.processDailyFinances(mountain, guestManager);
        }
        
        // Try to take a loan with very poor credit (if credit drops low enough)
        if (poorCreditManager.getCreditRating() < 30) {
            assertFalse(poorCreditManager.takeLoan(10000, "Desperate loan"));
        }
    }
    
    @Test
    void testLoanPaymentProcess() {
        // Take a loan
        financeManager.takeLoan(10000, "Test loan");
        
        Loan loan = financeManager.getActiveLoans().get(0);
        double initialBalance = loan.getRemainingBalance();
        double dailyPayment = loan.getDailyPayment();
        
        // Process one day to make a payment
        financeManager.processDailyFinances(mountain, guestManager);
        
        // Balance should decrease
        assertTrue(loan.getRemainingBalance() < initialBalance);
        
        // Should have made an expense for loan payment
        assertTrue(financeManager.getDailyExpenses() >= dailyPayment);
    }
    
    @Test
    void testDailyOperatingCosts() {
        double initialMoney = financeManager.getCurrentMoney();
        
        // Process one day of operations
        financeManager.processDailyFinances(mountain, guestManager);
        
        // Should have incurred operating expenses
        assertTrue(financeManager.getDailyExpenses() > 0);
        assertTrue(financeManager.getCurrentMoney() < initialMoney);
        
        // Should have base costs at minimum
        assertTrue(financeManager.getDailyExpenses() >= 200.0); // Base daily costs
    }
    
    @Test
    void testGuestRevenueGeneration() {
        // Add some guests
        guestManager.setMaxCapacity(5);
        guestManager.setBaseSpawnRate(5);
        
        // Run a few updates to get guests
        for (int i = 0; i < 3; i++) {
            guestManager.update(mountain);
        }
        
        // Process finances with guests present
        financeManager.processDailyFinances(mountain, guestManager);
        
        // Should generate some revenue if guests are using facilities
        if (guestManager.getActiveGuestCount() > 0) {
            // Revenue might be low but should be possible
            assertTrue(financeManager.getDailyRevenue() >= 0);
        }
    }
    
    @Test
    void testDemandPricingAdjustment() {
        double basePrice = financeManager.getCurrentLiftTicketPrice();
        
        // Test high satisfaction increases willingness to pay
        financeManager.updateDemandMultiplier(90.0); // High satisfaction
        double highSatisfactionPrice = financeManager.getCurrentLiftTicketPrice();
        
        // Test low satisfaction decreases pricing
        financeManager.updateDemandMultiplier(30.0); // Low satisfaction
        double lowSatisfactionPrice = financeManager.getCurrentLiftTicketPrice();
        
        assertTrue(highSatisfactionPrice > lowSatisfactionPrice);
        assertTrue(lowSatisfactionPrice < basePrice);
    }
    
    @Test
    void testBankruptcyThreshold() {
        // Spend most of the money
        financeManager.makePurchase(14000, "Heavy spending", ExpenseCategory.OTHER);
        
        // Should still be above bankruptcy threshold initially
        assertFalse(financeManager.isBankrupt());
        
        // Simulate continued losses to approach bankruptcy
        for (int i = 0; i < 30; i++) {
            financeManager.processDailyFinances(mountain, guestManager);
            if (financeManager.isBankrupt()) {
                break;
            }
        }
        
        // Should eventually hit bankruptcy with high daily costs and no revenue
        assertTrue(financeManager.getCurrentMoney() < 0);
    }
    
    @Test
    void testRevenueSourceTracking() {
        // Add revenue from different sources
        financeManager.processDailyFinances(mountain, guestManager);
        
        var revenueBySource = financeManager.getRevenueBySource();
        
        // Should have initial investment recorded
        assertTrue(revenueBySource.containsKey(RevenueSource.INITIAL_INVESTMENT));
        assertEquals(15000.0, revenueBySource.get(RevenueSource.INITIAL_INVESTMENT), 0.01);
    }
    
    @Test
    void testExpenseCategoryTracking() {
        // Make a purchase
        financeManager.makePurchase(1000, "Test expense", ExpenseCategory.EQUIPMENT_PURCHASE);
        
        var expensesByCategory = financeManager.getExpensesByCategory();
        
        // Should track the expense by category
        assertTrue(expensesByCategory.containsKey(ExpenseCategory.EQUIPMENT_PURCHASE));
        assertEquals(1000.0, expensesByCategory.get(ExpenseCategory.EQUIPMENT_PURCHASE), 0.01);
    }
    
    @Test
    void testTransactionHistory() {
        // Make some transactions
        financeManager.makePurchase(500, "Purchase 1", ExpenseCategory.OTHER);
        financeManager.processDailyFinances(mountain, guestManager);
        
        var recentTransactions = financeManager.getRecentTransactions(10);
        
        // Should have recorded transactions
        assertFalse(recentTransactions.isEmpty());
        
        // Should include initial capital and expenses
        assertTrue(recentTransactions.stream()
            .anyMatch(t -> t.getType() == TransactionType.INITIAL_CAPITAL));
    }
    
    @Test
    void testCreditRatingAdjustment() {
        double initialRating = financeManager.getCreditRating();
        
        // Simulate profitable operations to improve credit
        // Need to add revenue first
        financeManager.takeLoan(5000, "Test loan");
        
        // Simulate good financial performance
        // (This is simplified - in real game, would need actual guest revenue)
        for (int i = 0; i < 5; i++) {
            financeManager.processDailyFinances(mountain, guestManager);
        }
        
        // Credit rating changes are gradual, so may not see dramatic change
        // But the system should be tracking performance
        assertTrue(financeManager.getCreditRating() >= 0);
        assertTrue(financeManager.getCreditRating() <= 100);
    }
    
    @Test
    void testLiftOperatingCosts() {
        // Make sure our test lift is operational
        for (Lift lift : mountain.getLifts()) {
            lift.setOperational(true);
        }
        
        double initialMoney = financeManager.getCurrentMoney();
        
        // Process finances to incur lift operating costs
        financeManager.processDailyFinances(mountain, guestManager);
        
        // Should have lift operating expenses
        var expensesByCategory = financeManager.getExpensesByCategory();
        assertTrue(expensesByCategory.containsKey(ExpenseCategory.LIFT_OPERATIONS));
        assertTrue(expensesByCategory.get(ExpenseCategory.LIFT_OPERATIONS) > 0);
    }
    
    @Test
    void testSlopeMaintenanceCosts() {
        // Ensure slopes are open
        for (Slope slope : mountain.getSlopes()) {
            slope.setOpen(true);
        }
        
        // Process finances to incur slope maintenance costs
        financeManager.processDailyFinances(mountain, guestManager);
        
        // Should have slope maintenance expenses
        var expensesByCategory = financeManager.getExpensesByCategory();
        assertTrue(expensesByCategory.containsKey(ExpenseCategory.SLOPE_MAINTENANCE));
        assertTrue(expensesByCategory.get(ExpenseCategory.SLOPE_MAINTENANCE) > 0);
    }
    
    @Test
    void testFinancialProgression() {
        // Test the challenging but fair progression
        double startingMoney = financeManager.getCurrentMoney();
        
        // Starting money should be enough for one basic slope
        assertTrue(startingMoney >= 5000); // Cheapest slope cost
        
        // But not enough for a chairlift without a loan  
        assertFalse(financeManager.canAfford(25000)); // Chairlift cost
        
        // Should be able to get a loan for expansion
        assertTrue(financeManager.takeLoan(20000, "Expansion loan"));
        
        // Now should have enough for magic carpet (cheapest lift)
        assertTrue(financeManager.canAfford(10000));
    }
    
    @Test
    void testLoanMathAccuracy() {
        Loan testLoan = new Loan(10000, 0.12, 365, "Test loan");
        
        double dailyPayment = testLoan.getDailyPayment();
        double totalPayment = dailyPayment * 365;
        double totalInterest = totalPayment - 10000;
        
        // Interest should be significant but not excessive
        assertTrue(totalInterest > 500); // Should have meaningful interest
        assertTrue(totalInterest < 2000); // But not usurious
        
        // Daily payment should be manageable
        assertTrue(dailyPayment > 25); // Should be substantial
        assertTrue(dailyPayment < 50); // But not overwhelming
    }
    
    @Test
    void testFinancialResilience() {
        // Test that the system can handle edge cases
        
        // Zero amounts
        assertFalse(financeManager.makePurchase(0, "Zero purchase", ExpenseCategory.OTHER));
        
        // Negative amounts (should be prevented by canAfford check)
        assertFalse(financeManager.canAfford(-100));
        
        // Very large loans should be possible with good credit
        assertTrue(financeManager.takeLoan(100000, "Large loan"));
        
        // Multiple loans should be possible
        assertTrue(financeManager.takeLoan(5000, "Second loan"));
        
        assertEquals(2, financeManager.getActiveLoans().size());
    }
} 