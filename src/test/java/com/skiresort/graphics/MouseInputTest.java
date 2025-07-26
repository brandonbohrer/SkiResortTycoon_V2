package com.skiresort.graphics;

import com.skiresort.shared.Position;
import com.skiresort.graphics.IsometricMath.ScreenPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for mouse input accuracy and camera panning behavior
 * Ensures clicks register exactly where they appear on screen
 */
@DisplayName("Mouse Input and Camera Tests")
class MouseInputTest {
    
    private Camera2D camera;
    
    @BeforeEach
    void setUp() {
        // Use game dimensions
        camera = new Camera2D(1000, 600);
    }
    
    @Test
    @DisplayName("Clicking on visible terrain should register at that world position")
    void testVisualClickAccuracy() {
        // Set camera to show specific world coordinates
        camera.setPosition(10, 10);
        camera.setZoom(1.0);
        
        // World position (15, 15) should be visible and clickable
        Position targetWorld = new Position(15, 15);
        ScreenPoint targetScreen = camera.worldToScreen(targetWorld, 0);
        
        // Click should be within viewport
        assertTrue(targetScreen.x >= 0 && targetScreen.x <= 1000, 
            "Target should be visible horizontally: " + targetScreen.x);
        assertTrue(targetScreen.y >= 0 && targetScreen.y <= 600,
            "Target should be visible vertically: " + targetScreen.y);
        
        // Click at that screen position should return the original world position
        Position clickResult = camera.screenToWorld(targetScreen.x, targetScreen.y);
        assertEquals(targetWorld.getX(), clickResult.getX(), 
            "Click X should match target position");
        assertEquals(targetWorld.getY(), clickResult.getY(),
            "Click Y should match target position");
    }
    
    @Test
    @DisplayName("Camera panning should move view in expected direction")
    void testCameraPanningDirection() {
        camera.setPosition(10, 10);
        camera.setZoom(1.0);
        
        // Get initial screen position of a world point
        Position testPoint = new Position(12, 12);
        ScreenPoint initialScreen = camera.worldToScreen(testPoint, 0);
        
        // Pan camera "right" (positive X) - should make world point appear left on screen
        camera.panBy(5, 0);
        camera.update(); // Apply the movement
        ScreenPoint afterPanRight = camera.worldToScreen(testPoint, 0);
        
        assertTrue(afterPanRight.x < initialScreen.x,
            "Panning camera right should move world point left on screen. Initial: " + 
            initialScreen.x + ", After: " + afterPanRight.x);
        
        // Reset camera
        camera.setPosition(10, 10);
        
        // Pan camera "down" (positive Y) - should make world point appear up on screen  
        camera.panBy(0, 5);
        camera.update(); // Apply the movement
        ScreenPoint afterPanDown = camera.worldToScreen(testPoint, 0);
        
        assertTrue(afterPanDown.y < initialScreen.y,
            "Panning camera down should move world point up on screen. Initial: " + 
            initialScreen.y + ", After: " + afterPanDown.y);
    }
    
    @Test
    @DisplayName("Mouse drag direction should match visual movement")
    void testMouseDragDirection() {
        camera.setPosition(10, 10);
        camera.setZoom(1.0);
        
        // Simulate mouse drag from left to right on screen
        // This should pan the camera to show terrain that was to the right
        int startScreenX = 400;
        int endScreenX = 600;  // Drag right 200 pixels
        int screenY = 300;     // Middle of screen
        
        // Get world positions under mouse before and after drag
        Position worldBefore = camera.screenToWorld(startScreenX, screenY);
        
        // Calculate expected camera movement for right drag
        // Right drag should move camera right (positive X) to reveal terrain on the right
        double mouseDeltaX = endScreenX - startScreenX; // +200 pixels
        double expectedCameraDeltaX = mouseDeltaX / (camera.getZoom() * IsometricMath.TILE_WIDTH/8);
        
        // Apply camera movement (this is what the game does)
        camera.panBy(expectedCameraDeltaX, 0);
        camera.update(); // Apply the movement
        
        Position worldAfter = camera.screenToWorld(startScreenX, screenY);
        
        // After panning right, the world position under the start mouse position
        // should be to the left of the original position
        assertTrue(worldAfter.getX() > worldBefore.getX(),
            "Dragging right should reveal terrain to the right. Before: " + worldBefore.getX() + 
            ", After: " + worldAfter.getX());
    }
    
    @Test
    @DisplayName("Click accuracy should be independent of camera position")
    void testClickAccuracyAtDifferentCameraPositions() {
        double[] cameraPositions = {0, 5, 10, 15, 20};
        double[] zoomLevels = {0.5, 1.0, 2.0};
        
        for (double camX : cameraPositions) {
            for (double camY : cameraPositions) {
                for (double zoom : zoomLevels) {
                    camera.setPosition(camX, camY);
                    camera.setZoom(zoom);
                    
                    // Test multiple world positions
                    Position[] testPositions = {
                        new Position((int)camX, (int)camY),         // Camera center
                        new Position((int)camX + 3, (int)camY + 2), // Offset from center
                        new Position((int)camX - 2, (int)camY + 1)  // Different offset
                    };
                    
                    for (Position worldPos : testPositions) {
                        ScreenPoint screen = camera.worldToScreen(worldPos, 0);
                        
                        // Only test if position is visible
                        if (screen.x >= 0 && screen.x <= 1000 && screen.y >= 0 && screen.y <= 600) {
                            Position clickResult = camera.screenToWorld(screen.x, screen.y);
                            
                            assertEquals(worldPos.getX(), clickResult.getX(),
                                String.format("Click accuracy failed at cam(%.1f,%.1f) zoom %.1f for world %s",
                                    camX, camY, zoom, worldPos));
                            assertEquals(worldPos.getY(), clickResult.getY(),
                                String.format("Click accuracy failed at cam(%.1f,%.1f) zoom %.1f for world %s", 
                                    camX, camY, zoom, worldPos));
                        }
                    }
                }
            }
        }
    }
    
    @Test
    @DisplayName("Screen center should always correspond to camera world position")
    void testScreenCenterAccuracy() {
        double[] cameraPositions = {0, 5, 10, 15, 20};
        double[] zoomLevels = {0.5, 1.0, 2.0};
        
        int centerX = 500; // Center of 1000px width
        int centerY = 300; // Center of 600px height
        
        for (double camX : cameraPositions) {
            for (double camY : cameraPositions) {
                for (double zoom : zoomLevels) {
                    camera.setPosition(camX, camY);
                    camera.setZoom(zoom);
                    
                    Position centerWorld = camera.screenToWorld(centerX, centerY);
                    
                    // Screen center should be very close to camera position
                    assertEquals(camX, centerWorld.getX(), 1.0,
                        String.format("Screen center X should match camera at pos(%.1f,%.1f) zoom %.1f. Got: %d",
                            camX, camY, zoom, centerWorld.getX()));
                    assertEquals(camY, centerWorld.getY(), 1.0,
                        String.format("Screen center Y should match camera at pos(%.1f,%.1f) zoom %.1f. Got: %d",
                            camX, camY, zoom, centerWorld.getY()));
                }
            }
        }
    }
    
    @Test
    @DisplayName("Isometric coordinate system should be consistent")
    void testIsometricConsistency() {
        // Test the fundamental isometric math independently
        Position[] testPositions = {
            new Position(0, 0),
            new Position(5, 0),
            new Position(0, 5),
            new Position(5, 5),
            new Position(10, 8),
            new Position(-3, 7)
        };
        
        for (Position pos : testPositions) {
            // Direct IsometricMath conversion
            ScreenPoint isoScreen = IsometricMath.worldToScreen(pos, 0);
            Position isoBack = IsometricMath.screenToWorld(isoScreen.x, isoScreen.y);
            
            assertEquals(pos.getX(), isoBack.getX(),
                "IsometricMath round-trip failed for X: " + pos);
            assertEquals(pos.getY(), isoBack.getY(),
                "IsometricMath round-trip failed for Y: " + pos);
        }
    }
} 