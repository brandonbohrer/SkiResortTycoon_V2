package com.skiresort.graphics;

import com.skiresort.shared.Position;
import com.skiresort.graphics.IsometricMath.ScreenPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Focused tests for click coordinate accuracy
 * Tests the full pipeline: screen click -> world position -> construction
 */
@DisplayName("Click Coordinate Accuracy Tests")
class ClickCoordinateTest {
    
    private Camera2D camera;
    
    @BeforeEach
    void setUp() {
        // Use same dimensions as game
        camera = new Camera2D(1000, 600); // Game height minus status bar
    }
    
    @Test
    @DisplayName("Should accurately convert center screen click to world coordinates")
    void testCenterClickAccuracy() {
        // Test clicking the center of screen when camera is at origin
        camera.setPosition(0, 0);
        camera.setZoom(1.0);
        
        int centerX = 500; // Middle of 1000px width
        int centerY = 300; // Middle of 600px height
        
        Position worldPos = camera.screenToWorld(centerX, centerY);
        
        // Should be close to origin
        assertTrue(Math.abs(worldPos.getX()) <= 1, "Center click X should be near 0, got: " + worldPos.getX());
        assertTrue(Math.abs(worldPos.getY()) <= 1, "Center click Y should be near 0, got: " + worldPos.getY());
    }
    
    @Test
    @DisplayName("Should handle round-trip conversion accurately")
    void testRoundTripAccuracy() {
        camera.setPosition(0, 0);
        camera.setZoom(1.0);
        
        // Test several world positions
        Position[] testPositions = {
            new Position(0, 0),
            new Position(5, 5),
            new Position(10, 15),
            new Position(-5, 3),
            new Position(12, -8)
        };
        
        for (Position original : testPositions) {
            // Convert world -> screen -> world
            ScreenPoint screen = camera.worldToScreen(original, 0);
            Position converted = camera.screenToWorld(screen.x, screen.y);
            
            assertEquals(original.getX(), converted.getX(), 
                "Round trip failed for X: " + original + " -> " + screen + " -> " + converted);
            assertEquals(original.getY(), converted.getY(),
                "Round trip failed for Y: " + original + " -> " + screen + " -> " + converted);
        }
    }
    
    @Test
    @DisplayName("Should handle camera panning accurately")
    void testCameraPanningAccuracy() {
        // Pan camera to a specific position
        camera.setPosition(12, 8);
        camera.setZoom(1.0);
        
        // Center screen should now correspond to camera position
        int centerX = 500;
        int centerY = 300;
        
        Position worldPos = camera.screenToWorld(centerX, centerY);
        
        // Should be close to camera position
        assertEquals(12, worldPos.getX(), 1, "Panned camera center X");
        assertEquals(8, worldPos.getY(), 1, "Panned camera center Y");
    }
    
    @Test
    @DisplayName("Should handle zooming accurately")
    void testZoomAccuracy() {
        camera.setPosition(0, 0);
        
        // Test at different zoom levels
        double[] zoomLevels = {0.5, 1.0, 2.0};
        
        for (double zoom : zoomLevels) {
            camera.setZoom(zoom);
            
            // Center should still be origin
            Position centerWorld = camera.screenToWorld(500, 300);
            assertTrue(Math.abs(centerWorld.getX()) <= 1, 
                "Zoom " + zoom + " center X should be near 0, got: " + centerWorld.getX());
            assertTrue(Math.abs(centerWorld.getY()) <= 1,
                "Zoom " + zoom + " center Y should be near 0, got: " + centerWorld.getY());
        }
    }
    
    @Test
    @DisplayName("Should handle combined pan and zoom accurately")
    void testPanAndZoomAccuracy() {
        // Pan and zoom like in typical gameplay
        camera.setPosition(12, 8);
        camera.setZoom(0.68); // Like in the screenshot
        
        // Test that we can still do round-trip conversion
        Position testPos = new Position(15, 10);
        ScreenPoint screen = camera.worldToScreen(testPos, 0);
        Position converted = camera.screenToWorld(screen.x, screen.y);
        
        assertEquals(testPos.getX(), converted.getX(), 1,
            "Pan+Zoom round trip X failed");
        assertEquals(testPos.getY(), converted.getY(), 1,
            "Pan+Zoom round trip Y failed");
    }
    
    @Test
    @DisplayName("Should accurately detect clicks on mountain boundaries")
    void testMountainBoundaryClicks() {
        // Test clicks near mountain edges (25x25 mountain)
        camera.setPosition(12, 12); // Center on 25x25 mountain
        camera.setZoom(1.0);
        
        // Find screen positions for mountain corners
        ScreenPoint topLeft = camera.worldToScreen(0, 0, 0);
        ScreenPoint bottomRight = camera.worldToScreen(24, 24, 0);
        
        // Convert back to world
        Position topLeftWorld = camera.screenToWorld(topLeft.x, topLeft.y);
        Position bottomRightWorld = camera.screenToWorld(bottomRight.x, bottomRight.y);
        
        // Should be within reasonable bounds
        assertTrue(topLeftWorld.getX() >= -1 && topLeftWorld.getX() <= 1,
            "Top-left corner X accuracy: " + topLeftWorld.getX());
        assertTrue(topLeftWorld.getY() >= -1 && topLeftWorld.getY() <= 1,
            "Top-left corner Y accuracy: " + topLeftWorld.getY());
        
        assertTrue(bottomRightWorld.getX() >= 23 && bottomRightWorld.getX() <= 25,
            "Bottom-right corner X accuracy: " + bottomRightWorld.getX());
        assertTrue(bottomRightWorld.getY() >= 23 && bottomRightWorld.getY() <= 25,
            "Bottom-right corner Y accuracy: " + bottomRightWorld.getY());
    }
    
    @Test
    @DisplayName("Should handle negative world coordinates correctly")
    void testNegativeCoordinates() {
        camera.setPosition(0, 0);
        camera.setZoom(1.0);
        
        // Test negative world positions (outside mountain)
        Position negativePos = new Position(-5, -5);
        ScreenPoint screen = camera.worldToScreen(negativePos, 0);
        Position converted = camera.screenToWorld(screen.x, screen.y);
        
        assertEquals(negativePos.getX(), converted.getX(),
            "Negative X coordinate conversion");
        assertEquals(negativePos.getY(), converted.getY(),
            "Negative Y coordinate conversion");
    }
    
    @Test
    @DisplayName("Should match IsometricMath conversions exactly")
    void testIsometricMathConsistency() {
        // Verify camera conversions match IsometricMath when camera is at origin with no zoom
        camera.setPosition(0, 0);
        camera.setZoom(1.0);
        
        Position testPos = new Position(10, 5);
        
        // Direct IsometricMath conversion
        ScreenPoint isoScreen = IsometricMath.worldToScreen(testPos, 0);
        Position isoWorld = IsometricMath.screenToWorld(isoScreen.x, isoScreen.y);
        
        // Camera conversion (should account for viewport offset)
        ScreenPoint cameraScreen = camera.worldToScreen(testPos, 0);
        Position cameraWorld = camera.screenToWorld(cameraScreen.x, cameraScreen.y);
        
        // The world positions should match exactly
        assertEquals(isoWorld.getX(), cameraWorld.getX(),
            "Camera and IsometricMath world X should match");
        assertEquals(isoWorld.getY(), cameraWorld.getY(),
            "Camera and IsometricMath world Y should match");
    }
    
    @Test
    @DisplayName("Should accurately convert actual game scenario clicks")
    void testGameScenarioClicks() {
        // Simulate the exact camera state from the screenshots
        camera.setPosition(-2.6, -6.9); // From screenshot status bar
        camera.setZoom(0.68);
        
        // Test a click in the mountain area (approximate center of visible terrain)
        int clickX = 600; // Right side of visible mountain
        int clickY = 400; // Middle height
        
        Position worldPos = camera.screenToWorld(clickX, clickY);
        
        // Should be within reasonable mountain bounds (0-24 for 25x25 mountain)
        assertTrue(worldPos.getX() >= -30 && worldPos.getX() <= 30,
            "Game scenario click X should be reasonable: " + worldPos.getX());
        assertTrue(worldPos.getY() >= -30 && worldPos.getY() <= 30,
            "Game scenario click Y should be reasonable: " + worldPos.getY());
        
        // Log for debugging
        System.out.println("Game scenario click: Screen(" + clickX + "," + clickY + 
                          ") -> World(" + worldPos.getX() + "," + worldPos.getY() + ")");
    }
} 