package com.skiresort.graphics;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.skiresort.shared.Position;
import com.skiresort.graphics.IsometricMath.ScreenPoint;

/**
 * Comprehensive tests to isolate camera system issues
 * Tests each component (pan, zoom, coordinate conversion) separately
 */
public class CameraSystemTest {
    
    private Camera2D camera;
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 600;
    private static final double EPSILON = 0.01;
    
    @BeforeEach
    public void setUp() {
        camera = new Camera2D(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }
    
    @Test
    public void testCameraInitialization() {
        assertEquals(0.0, camera.getWorldX(), EPSILON);
        assertEquals(0.0, camera.getWorldY(), EPSILON);
        assertEquals(1.0, camera.getZoom(), EPSILON);
        assertEquals(VIEWPORT_WIDTH, camera.getViewportWidth());
        assertEquals(VIEWPORT_HEIGHT, camera.getViewportHeight());
    }
    
    @Test
    public void testBasicCoordinateConversion_NoOffsets() {
        // At camera position (0,0) with zoom 1.0, world origin should map to screen center
        ScreenPoint screenOrigin = camera.worldToScreen(0, 0, 0);
        assertEquals(VIEWPORT_WIDTH / 2, screenOrigin.x, 1.0, 
            "World origin should map to screen center X");
        assertEquals(VIEWPORT_HEIGHT / 2, screenOrigin.y, 1.0, 
            "World origin should map to screen center Y");
    }
    
    @Test
    public void testRoundTripCoordinateConversion() {
        // Test that worldToScreen -> screenToWorld returns original coordinates
        Position originalWorld = new Position(10, 15);
        ScreenPoint screen = camera.worldToScreen(originalWorld, 0);
        Position convertedBack = camera.screenToWorld(screen.x, screen.y);
        
        assertEquals(originalWorld.getX(), convertedBack.getX(), 1.0,
            "Round trip conversion should preserve X coordinate");
        assertEquals(originalWorld.getY(), convertedBack.getY(), 1.0,
            "Round trip conversion should preserve Y coordinate");
    }
    
    @Test
    public void testCameraPanningEffect() {
        // When camera moves right, objects should appear to move left on screen
        Position worldPoint = new Position(10, 10);
        
        // Get screen position before panning
        ScreenPoint beforePan = camera.worldToScreen(worldPoint, 0);
        
        // Pan camera right by 5 units in world coordinates
        camera.setPosition(5, 0);
        camera.update(); // Make sure camera position is updated
        
        // Get screen position after panning
        ScreenPoint afterPan = camera.worldToScreen(worldPoint, 0);
        
        // Calculate expected movement using isometric math
        ScreenPoint cameraDelta = IsometricMath.worldToScreen(5, 0, 0);
        
        // Object should move by the negative of camera movement
        int expectedDeltaX = -cameraDelta.x;
        int expectedDeltaY = -cameraDelta.y;
        int actualDeltaX = afterPan.x - beforePan.x;
        int actualDeltaY = afterPan.y - beforePan.y;
        
        assertEquals(expectedDeltaX, actualDeltaX, 1.0,
            "Camera movement should cause isometric-correct object movement on screen");
        assertEquals(expectedDeltaY, actualDeltaY, 1.0,
            "Camera movement should cause isometric-correct object movement on screen");
    }
    
    @Test
    public void testMousePanningDirection() {
        // Simulate mouse dragging right - this should pan camera right
        int startMouseX = 400;
        int endMouseX = 450; // Mouse moved 50 pixels right
        
        // Get initial camera position
        double initialCameraX = camera.getWorldX();
        
        // Calculate world delta as done in the GUI
        double mouseDeltaX = endMouseX - startMouseX; // +50
        double worldDeltaX = -mouseDeltaX / (camera.getZoom() * IsometricMath.TILE_WIDTH/8);
        
        // Apply pan
        camera.panBy(worldDeltaX, 0);
        camera.update();
        
        // Camera should have moved in the expected direction
        double finalCameraX = camera.getWorldX();
        double actualDelta = finalCameraX - initialCameraX;
        
        // The expected delta should match what we calculated
        assertEquals(worldDeltaX, actualDelta, 0.1,
            String.format("Camera should move by calculated delta. Expected %.3f, got %.3f", worldDeltaX, actualDelta));
    }
    
    @Test
    public void testZoomingBasicBehavior() {
        Position worldPoint = new Position(20, 20);
        
        // Get screen position at normal zoom
        ScreenPoint normalZoom = camera.worldToScreen(worldPoint, 0);
        
        // Zoom in 2x
        camera.setZoom(2.0);
        ScreenPoint zoomedIn = camera.worldToScreen(worldPoint, 0);
        
        // At 2x zoom, objects further from center should appear further from center
        int screenCenterX = VIEWPORT_WIDTH / 2;
        int screenCenterY = VIEWPORT_HEIGHT / 2;
        
        int normalDistanceX = Math.abs(normalZoom.x - screenCenterX);
        int zoomedDistanceX = Math.abs(zoomedIn.x - screenCenterX);
        
        assertTrue(zoomedDistanceX > normalDistanceX,
            "Zooming in should make objects appear further from center");
    }
    
    @Test
    public void testZoomTowardsPoint() {
        // Place a point at screen position (600, 400)
        Position originalScreen = camera.screenToWorld(600, 400);
        
        // Zoom towards this screen point
        camera.zoomTowards(1.5, 600, 400);
        
        // The same screen position should still point to the same world position
        Position afterZoomScreen = camera.screenToWorld(600, 400);
        
        assertEquals(originalScreen.getX(), afterZoomScreen.getX(), 1.0,
            "Zoom towards point should keep same world position under cursor");
        assertEquals(originalScreen.getY(), afterZoomScreen.getY(), 1.0,
            "Zoom towards point should keep same world position under cursor");
    }
    
    @Test
    public void testSmoothCameraMovement() {
        // Set a target position
        camera.panTo(10, 10);
        
        // Camera should not immediately jump to target
        assertNotEquals(10.0, camera.getWorldX(), 0.1,
            "Camera should not immediately jump to target position");
        
        // After multiple updates, camera should approach target
        for (int i = 0; i < 20; i++) {
            camera.update();
        }
        
        assertEquals(10.0, camera.getWorldX(), 0.1,
            "Camera should reach target after multiple updates");
        assertEquals(10.0, camera.getWorldY(), 0.1,
            "Camera should reach target after multiple updates");
    }
    
    @Test
    public void testImmediateCameraMovement() {
        // Set position immediately
        camera.setPosition(15, 20);
        
        // Camera should immediately be at the target
        assertEquals(15.0, camera.getWorldX(), EPSILON,
            "setPosition should immediately move camera");
        assertEquals(20.0, camera.getWorldY(), EPSILON,
            "setPosition should immediately move camera");
    }
    
    @Test
    public void testVisibilityCalculation() {
        // Point at camera center should always be visible
        assertTrue(camera.isVisible(0, 0, 0),
            "Point at camera center should be visible");
        
        // Very far away point should not be visible
        assertFalse(camera.isVisible(1000, 1000, 0),
            "Very far away point should not be visible");
        
        // Pan camera and test visibility changes
        camera.setPosition(100, 100);
        assertTrue(camera.isVisible(100, 100, 0),
            "Point at new camera center should be visible");
        assertFalse(camera.isVisible(0, 0, 0),
            "Original camera center should no longer be visible");
    }
    
    @Test
    public void testZoomConstraints() {
        // Test minimum zoom constraint
        camera.setZoom(0.1); // Below minimum
        assertTrue(camera.getZoom() >= camera.getMinZoom(),
            "Zoom should not go below minimum");
        
        // Test maximum zoom constraint
        camera.setZoom(10.0); // Above maximum
        assertTrue(camera.getZoom() <= camera.getMaxZoom(),
            "Zoom should not go above maximum");
    }
    
    @Test
    public void testPanSpeedConfiguration() {
        // Test setting valid pan speed
        camera.setPanSpeed(0.5);
        // No direct getter, but we can test behavior
        
        camera.panTo(10, 10);
        camera.update();
        
        // Should have moved partway toward target
        assertTrue(camera.getWorldX() > 0 && camera.getWorldX() < 10,
            "Camera should move partway toward target with custom pan speed");
    }
    
    @Test
    public void testCameraBoundedMovement() {
        // This test verifies that the coordinate transformation is working correctly
        // in the isometric coordinate system
        
        Position fixedWorldPoint = new Position(0, 0);
        
        // Get screen position with camera at origin
        camera.setPosition(0, 0);
        ScreenPoint atOrigin = camera.worldToScreen(fixedWorldPoint, 0);
        
        // Move camera right and down in world coordinates
        camera.setPosition(10, 10);
        ScreenPoint afterMove = camera.worldToScreen(fixedWorldPoint, 0);
        
        // Calculate expected movement using isometric math
        ScreenPoint expectedCameraDelta = IsometricMath.worldToScreen(10, 10, 0);
        
        // The fixed world point should move by the negative of camera movement
        int expectedScreenDeltaX = -expectedCameraDelta.x;
        int expectedScreenDeltaY = -expectedCameraDelta.y;
        int actualScreenDeltaX = afterMove.x - atOrigin.x;
        int actualScreenDeltaY = afterMove.y - atOrigin.y;
        
        assertEquals(expectedScreenDeltaX, actualScreenDeltaX, 1.0,
            "Screen X movement should match isometric transformation");
        assertEquals(expectedScreenDeltaY, actualScreenDeltaY, 1.0,
            "Screen Y movement should match isometric transformation");
        
        System.out.println(String.format("Camera at origin: world(0,0) -> screen(%d,%d)", 
            atOrigin.x, atOrigin.y));
        System.out.println(String.format("Camera at (10,10): world(0,0) -> screen(%d,%d)", 
            afterMove.x, afterMove.y));
        System.out.println(String.format("Screen movement: (%d,%d)", 
            actualScreenDeltaX, actualScreenDeltaY));
        System.out.println(String.format("Expected isometric movement: (%d,%d)", 
            expectedScreenDeltaX, expectedScreenDeltaY));
    }
    
    @Test
    public void testConsistentClickConversion() {
        // Test that clicking the same screen position multiple times gives same world coordinates
        int screenX = 500;
        int screenY = 400;
        
        Position firstClick = camera.screenToWorld(screenX, screenY);
        Position secondClick = camera.screenToWorld(screenX, screenY);
        Position thirdClick = camera.screenToWorld(screenX, screenY);
        
        assertEquals(firstClick.getX(), secondClick.getX(), 
            "Multiple clicks at same screen position should give same world X");
        assertEquals(firstClick.getY(), secondClick.getY(), 
            "Multiple clicks at same screen position should give same world Y");
        assertEquals(secondClick.getX(), thirdClick.getX(), 
            "Multiple clicks at same screen position should give same world X");
        assertEquals(secondClick.getY(), thirdClick.getY(), 
            "Multiple clicks at same screen position should give same world Y");
        
        System.out.printf("Consistent clicks at screen(%d,%d) -> world(%d,%d)%n", 
            screenX, screenY, firstClick.getX(), firstClick.getY());
    }
    
    @Test 
    public void testConstructionScenario() {
        // Simulate the exact construction scenario: two clicks should both be accurate
        
        // First click (start point) - this works perfectly according to user
        int startScreenX = 400;
        int startScreenY = 300;
        Position startWorld = camera.screenToWorld(startScreenX, startScreenY);
        
        // Simulate some camera updates that might happen during construction
        camera.update();
        camera.update();
        
        // Second click (end point) - this is where the issue occurs
        int endScreenX = 500;
        int endScreenY = 350;
        Position endWorld = camera.screenToWorld(endScreenX, endScreenY);
        
        // Verify round-trip accuracy for both clicks
        ScreenPoint startBackToScreen = camera.worldToScreen(startWorld, 0);
        ScreenPoint endBackToScreen = camera.worldToScreen(endWorld, 0);
        
        assertEquals(startScreenX, startBackToScreen.x, 5.0, 
            "Start point round-trip should be accurate");
        assertEquals(startScreenY, startBackToScreen.y, 5.0, 
            "Start point round-trip should be accurate");
        assertEquals(endScreenX, endBackToScreen.x, 5.0, 
            "End point round-trip should be accurate");
        assertEquals(endScreenY, endBackToScreen.y, 5.0, 
            "End point round-trip should be accurate");
        
        System.out.printf("Construction simulation:%n");
        System.out.printf("  Start: screen(%d,%d) -> world(%d,%d) -> back to screen(%d,%d)%n",
            startScreenX, startScreenY, startWorld.getX(), startWorld.getY(), 
            startBackToScreen.x, startBackToScreen.y);
        System.out.printf("  End: screen(%d,%d) -> world(%d,%d) -> back to screen(%d,%d)%n",
            endScreenX, endScreenY, endWorld.getX(), endWorld.getY(), 
            endBackToScreen.x, endBackToScreen.y);
    }
} 