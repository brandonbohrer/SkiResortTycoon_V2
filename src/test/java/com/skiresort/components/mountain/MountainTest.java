package com.skiresort.components.mountain;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.shared.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mountain Component Tests")
class MountainTest {
    
    private Mountain mountain;
    
    @BeforeEach
    void setUp() {
        mountain = new Mountain("Test Mountain", 50, 50);
    }
    
    @Test
    @DisplayName("Mountain should be created with basic properties")
    void testMountainCreation() {
        assertNotNull(mountain.getId());
        assertEquals("Test Mountain", mountain.getName());
        assertEquals(50, mountain.getWidth());
        assertEquals(50, mountain.getHeight());
        assertTrue(mountain.getSlopes().isEmpty());
        assertTrue(mountain.getLifts().isEmpty());
    }
    
    @Test
    @DisplayName("Mountain should generate elevation data")
    void testElevationGeneration() {
        // Test that elevation is generated and makes sense
        int centerX = mountain.getWidth() / 2;
        int centerY = 0; // Top of mountain
        int bottomY = mountain.getHeight() - 1;
        
        // Center top should have higher elevation than center bottom
        int topElevation = mountain.getElevationAt(centerX, centerY);
        int bottomElevation = mountain.getElevationAt(centerX, bottomY);
        
        assertTrue(topElevation > bottomElevation, 
            "Top of mountain should have higher elevation than bottom");
        
        // Elevation should be non-negative
        assertTrue(topElevation >= 0);
        assertTrue(bottomElevation >= 0);
    }
    
    @Test
    @DisplayName("Mountain should validate slope positions")
    void testSlopeValidation() {
        // Create a valid downhill slope
        // Since mountain elevation decreases with distance from center/top,
        // we need to go from high elevation to low elevation
        Position topPosition = new Position(25, 5);  // Near top - high elevation  
        Position bottomPosition = new Position(25, 45); // Near bottom - low elevation
        
        // Verify this creates a downhill slope
        int topElevation = mountain.getElevationAt(topPosition);
        int bottomElevation = mountain.getElevationAt(bottomPosition);
        
        assertTrue(topElevation > bottomElevation,
            "Test setup error: topPosition should have higher elevation than bottomPosition");
        
        Slope validSlope = new Slope("Test Slope", topPosition, bottomPosition, 
            Slope.Difficulty.INTERMEDIATE, 10000);
        
        assertTrue(mountain.addSlope(validSlope), 
            "Should be able to add valid downhill slope");
        assertEquals(1, mountain.getSlopes().size());
    }
    
    @Test
    @DisplayName("Mountain should reject invalid slope positions")
    void testInvalidSlopeRejection() {
        // Find two positions where we can create a true uphill slope
        // Since mountain elevation decreases with distance from center/top,
        // we need to go from a low elevation to a high elevation
        
        Position lowPosition = new Position(25, 45); // Near bottom - low elevation
        Position highPosition = new Position(25, 5);  // Near top - high elevation
        
        // Verify the elevation difference to ensure this is truly uphill
        int lowElevation = mountain.getElevationAt(lowPosition);
        int highElevation = mountain.getElevationAt(highPosition);
        
        assertTrue(lowElevation < highElevation, 
            "Test setup error: lowPosition should have lower elevation than highPosition");
        
        // Create uphill slope (from low to high elevation) - should be rejected
        Slope uphillSlope = new Slope("Invalid Slope", lowPosition, highPosition, 
            Slope.Difficulty.INTERMEDIATE, 10000);
        
        assertFalse(mountain.addSlope(uphillSlope), 
            "Should reject uphill slopes");
        assertEquals(0, mountain.getSlopes().size());
    }
    
    @Test
    @DisplayName("Mountain should validate lift positions")
    void testLiftValidation() {
        // Create a valid uphill lift (lifts go from low to high elevation)
        Position bottomPosition = new Position(25, 45); // Near bottom - low elevation
        Position topPosition = new Position(25, 5);     // Near top - high elevation
        
        // Verify this creates an uphill lift
        int bottomElevation = mountain.getElevationAt(bottomPosition);
        int topElevation = mountain.getElevationAt(topPosition);
        
        assertTrue(bottomElevation < topElevation,
            "Test setup error: bottomPosition should have lower elevation than topPosition");
        
        Lift validLift = new Lift("Test Lift", bottomPosition, topPosition, 
            Lift.LiftType.CHAIRLIFT);
        
        assertTrue(mountain.addLift(validLift), 
            "Should be able to add valid uphill lift");
        assertEquals(1, mountain.getLifts().size());
    }
    
    @Test
    @DisplayName("Mountain should reject invalid lift positions")
    void testInvalidLiftRejection() {
        // Try to create downhill lift (should be rejected)
        // Lifts should go uphill, so this reverses the positions
        Position topPosition = new Position(25, 5);     // Near top - high elevation
        Position bottomPosition = new Position(25, 45); // Near bottom - low elevation
        
        // Verify this would create a downhill lift
        int topElevation = mountain.getElevationAt(topPosition);
        int bottomElevation = mountain.getElevationAt(bottomPosition);
        
        assertTrue(topElevation > bottomElevation,
            "Test setup error: topPosition should have higher elevation than bottomPosition");
        
        Lift downhillLift = new Lift("Invalid Lift", topPosition, bottomPosition, 
            Lift.LiftType.CHAIRLIFT);
        
        assertFalse(mountain.addLift(downhillLift), 
            "Should reject downhill lifts");
        assertEquals(0, mountain.getLifts().size());
    }
    
    @Test
    @DisplayName("Mountain should reject out-of-bounds positions")
    void testOutOfBoundsValidation() {
        // Test out-of-bounds slope
        Position validPosition = new Position(25, 25);
        Position outOfBoundsPosition = new Position(100, 100);
        
        Slope outOfBoundsSlope = new Slope("OOB Slope", validPosition, outOfBoundsPosition, 
            Slope.Difficulty.BEGINNER, 5000);
        
        assertFalse(mountain.addSlope(outOfBoundsSlope), 
            "Should reject out-of-bounds slopes");
        
        // Test out-of-bounds lift
        Lift outOfBoundsLift = new Lift("OOB Lift", validPosition, outOfBoundsPosition, 
            Lift.LiftType.T_BAR);
        
        assertFalse(mountain.addLift(outOfBoundsLift), 
            "Should reject out-of-bounds lifts");
    }
    
    @Test
    @DisplayName("Mountain should handle elevation queries correctly")
    void testElevationQueries() {
        Position validPosition = new Position(10, 10);
        Position invalidPosition = new Position(100, 100);
        
        // Valid position should return elevation
        assertDoesNotThrow(() -> mountain.getElevationAt(validPosition));
        assertDoesNotThrow(() -> mountain.getElevationAt(10, 10));
        
        // Invalid position should throw exception
        assertThrows(IllegalArgumentException.class, 
            () -> mountain.getElevationAt(invalidPosition));
        assertThrows(IllegalArgumentException.class, 
            () -> mountain.getElevationAt(100, 100));
    }
    
    @Test
    @DisplayName("Slope should calculate properties correctly")
    void testSlopeProperties() {
        Position start = new Position(25, 10);
        Position end = new Position(25, 40);
        
        Slope slope = new Slope("Test Slope", start, end, 
            Slope.Difficulty.ADVANCED, 15000);
        
        // Test basic properties
        assertEquals("Test Slope", slope.getName());
        assertEquals(Slope.Difficulty.ADVANCED, slope.getDifficulty());
        assertEquals(15000, slope.getBuildCost());
        assertEquals(750, slope.getMaintenanceCost()); // 5% of build cost
        assertTrue(slope.isOpen());
        
        // Test calculated properties
        assertEquals(30.0, slope.getLength()); // Distance between (25,10) and (25,40)
        
        // Test with mountain context
        mountain.addSlope(slope);
        int elevationDrop = slope.getElevationDrop(mountain);
        assertTrue(elevationDrop > 0, "Slope should have positive elevation drop");
        
        double gradient = slope.getGradient(mountain);
        assertTrue(gradient > 0, "Slope should have positive gradient");
    }
    
    @Test
    @DisplayName("Lift should calculate properties correctly")
    void testLiftProperties() {
        Position bottom = new Position(25, 40);
        Position top = new Position(25, 10);
        
        Lift lift = new Lift("Test Lift", bottom, top, Lift.LiftType.GONDOLA);
        
        // Test basic properties
        assertEquals("Test Lift", lift.getName());
        assertEquals(Lift.LiftType.GONDOLA, lift.getType());
        assertEquals(Lift.LiftType.GONDOLA.getBuildCost(), lift.getBuildCost());
        assertTrue(lift.isOperational());
        assertEquals(0, lift.getCurrentCapacity());
        
        // Test calculated properties
        assertEquals(30.0, lift.getLength());
        assertTrue(lift.getHourlyThroughput() > 0);
        assertTrue(lift.canAcceptGuests());
        
        // Test guest management
        assertTrue(lift.addGuests(5));
        assertEquals(5, lift.getCurrentCapacity());
        
        lift.removeGuests(3);
        assertEquals(2, lift.getCurrentCapacity());
        
        // Test with mountain context
        mountain.addLift(lift);
        int elevationGain = lift.getElevationGain(mountain);
        assertTrue(elevationGain > 0, "Lift should have positive elevation gain");
    }
    
    @Test
    @DisplayName("Appeal scores should be calculated correctly")
    void testAppealScores() {
        Position start = new Position(25, 10);
        Position end = new Position(25, 40);
        
        // Test slope appeal
        Slope slope = new Slope("Test Slope", start, end, 
            Slope.Difficulty.INTERMEDIATE, 10000);
        
        int initialAppeal = slope.getAppealScore();
        assertTrue(initialAppeal >= 0 && initialAppeal <= 100);
        
        // Closing slope should reduce appeal
        slope.setOpen(false);
        int closedAppeal = slope.getAppealScore();
        assertTrue(closedAppeal < initialAppeal);
        
        // Test lift appeal
        Lift lift = new Lift("Test Lift", end, start, Lift.LiftType.CHAIRLIFT);
        
        int liftAppeal = lift.getAppealScore();
        assertTrue(liftAppeal >= 0 && liftAppeal <= 100);
        
        // Making lift non-operational should reduce appeal
        lift.setOperational(false);
        int nonOpAppeal = lift.getAppealScore();
        assertTrue(nonOpAppeal < liftAppeal);
    }
} 