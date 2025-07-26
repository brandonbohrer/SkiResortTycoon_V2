package com.skiresort.app;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.finances.core.FinanceManager;
import com.skiresort.shared.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for construction system functionality
 * Verifies that building slopes and lifts works with proper validation
 */
@DisplayName("Construction System Tests")
class ConstructionSystemTest {
    
    private Mountain mountain;
    private FinanceManager financeManager;
    
    @BeforeEach
    void setUp() {
        mountain = new Mountain("Test Resort", 25, 25);
        financeManager = new FinanceManager();
    }
    
    @Test
    @DisplayName("Should build valid downhill slope when affordable")
    void testBuildValidSlope() {
        // Find positions where slope goes downhill
        Position start = new Position(12, 5);  // Higher elevation (near top)
        Position end = new Position(12, 20);   // Lower elevation (near bottom)
        
        // Verify this is actually downhill
        double startElevation = mountain.getElevationAt(start);
        double endElevation = mountain.getElevationAt(end);
        assertTrue(startElevation > endElevation, "Test setup error: start should be higher than end");
        
        // Record initial state
        double initialMoney = financeManager.getCurrentMoney();
        int initialSlopes = mountain.getSlopes().size();
        
        // Build slope
        double slopeCost = 5000.0;
        assertTrue(financeManager.canAfford(slopeCost), "Should be able to afford slope");
        
        Slope newSlope = new Slope("Test Slope", start, end, Slope.Difficulty.BEGINNER, slopeCost);
        boolean slopeAdded = mountain.addSlope(newSlope);
        
        assertTrue(slopeAdded, "Mountain should accept valid downhill slope");
        
        if (slopeAdded) {
            boolean purchaseSuccessful = financeManager.makePurchase(slopeCost, "Built Test Slope", 
                com.skiresort.components.finances.core.ExpenseCategory.FACILITY_CONSTRUCTION);
            assertTrue(purchaseSuccessful, "Purchase should succeed when affordable");
            
            // Verify money was deducted
            assertEquals(initialMoney - slopeCost, financeManager.getCurrentMoney(), 0.01);
            
            // Verify slope was added
            assertEquals(initialSlopes + 1, mountain.getSlopes().size());
        }
    }
    
    @Test
    @DisplayName("Should reject uphill slope")
    void testRejectUphillSlope() {
        // Try to build uphill slope (invalid)
        Position start = new Position(12, 20);  // Lower elevation
        Position end = new Position(12, 5);     // Higher elevation
        
        // Verify this is uphill (invalid for slopes)
        double startElevation = mountain.getElevationAt(start);
        double endElevation = mountain.getElevationAt(end);
        assertTrue(startElevation < endElevation, "Test setup error: this should be uphill");
        
        Slope invalidSlope = new Slope("Invalid Slope", start, end, Slope.Difficulty.BEGINNER, 5000.0);
        boolean slopeAdded = mountain.addSlope(invalidSlope);
        
        assertFalse(slopeAdded, "Mountain should reject uphill slopes");
    }
    
    @Test
    @DisplayName("Should build valid uphill lift when affordable")
    void testBuildValidLift() {
        // Find positions where lift goes uphill
        Position bottom = new Position(12, 20);  // Lower elevation
        Position top = new Position(12, 5);      // Higher elevation
        
        // Verify this is uphill
        double bottomElevation = mountain.getElevationAt(bottom);
        double topElevation = mountain.getElevationAt(top);
        assertTrue(bottomElevation < topElevation, "Test setup error: bottom should be lower than top");
        
        // Record initial state
        double initialMoney = financeManager.getCurrentMoney();
        int initialLifts = mountain.getLifts().size();
        
        // Build lift
        double liftCost = 10000.0;
        assertTrue(financeManager.canAfford(liftCost), "Should be able to afford lift");
        
        Lift newLift = new Lift("Test Lift", bottom, top, Lift.LiftType.MAGIC_CARPET);
        boolean liftAdded = mountain.addLift(newLift);
        
        assertTrue(liftAdded, "Mountain should accept valid uphill lift");
        
        if (liftAdded) {
            boolean purchaseSuccessful = financeManager.makePurchase(liftCost, "Built Test Lift", 
                com.skiresort.components.finances.core.ExpenseCategory.EQUIPMENT_PURCHASE);
            assertTrue(purchaseSuccessful, "Purchase should succeed when affordable");
            
            // Verify money was deducted
            assertEquals(initialMoney - liftCost, financeManager.getCurrentMoney(), 0.01);
            
            // Verify lift was added
            assertEquals(initialLifts + 1, mountain.getLifts().size());
        }
    }
    
    @Test
    @DisplayName("Should reject downhill lift")
    void testRejectDownhillLift() {
        // Try to build downhill lift (invalid)
        Position bottom = new Position(12, 5);   // Higher elevation
        Position top = new Position(12, 20);     // Lower elevation
        
        // Verify this is downhill (invalid for lifts)
        double bottomElevation = mountain.getElevationAt(bottom);
        double topElevation = mountain.getElevationAt(top);
        assertTrue(bottomElevation > topElevation, "Test setup error: this should be downhill");
        
        Lift invalidLift = new Lift("Invalid Lift", bottom, top, Lift.LiftType.MAGIC_CARPET);
        boolean liftAdded = mountain.addLift(invalidLift);
        
        assertFalse(liftAdded, "Mountain should reject downhill lifts");
    }
    
    @Test
    @DisplayName("Should reject construction when not affordable")
    void testRejectUnaffordableConstruction() {
        // Spend most of the money
        double expensiveCost = financeManager.getCurrentMoney() - 1000; // Leave only $1000
        financeManager.makePurchase(expensiveCost, "Expensive purchase", 
            com.skiresort.components.finances.core.ExpenseCategory.OTHER);
        
        // Try to build something more expensive than remaining money
        double slopeCost = 5000.0;
        assertFalse(financeManager.canAfford(slopeCost), "Should not be able to afford slope");
        
        boolean purchaseSuccessful = financeManager.makePurchase(slopeCost, "Unaffordable slope", 
            com.skiresort.components.finances.core.ExpenseCategory.FACILITY_CONSTRUCTION);
        
        assertFalse(purchaseSuccessful, "Purchase should fail when unaffordable");
    }
    
    @Test
    @DisplayName("Should handle out of bounds construction")
    void testOutOfBoundsConstruction() {
        Position validPos = new Position(10, 10);
        Position invalidPos = new Position(100, 100); // Outside 25x25 mountain
        
        Slope outOfBoundsSlope = new Slope("OOB Slope", validPos, invalidPos, 
            Slope.Difficulty.BEGINNER, 5000.0);
        boolean slopeAdded = mountain.addSlope(outOfBoundsSlope);
        
        assertFalse(slopeAdded, "Mountain should reject out-of-bounds slopes");
        
        Lift outOfBoundsLift = new Lift("OOB Lift", validPos, invalidPos, Lift.LiftType.MAGIC_CARPET);
        boolean liftAdded = mountain.addLift(outOfBoundsLift);
        
        assertFalse(liftAdded, "Mountain should reject out-of-bounds lifts");
    }
} 