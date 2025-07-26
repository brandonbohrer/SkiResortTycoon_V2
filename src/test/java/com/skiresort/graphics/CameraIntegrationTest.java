package com.skiresort.graphics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.skiresort.shared.Position;
import com.skiresort.graphics.IsometricMath.ScreenPoint;

/**
 * Integration tests for camera system functionality including panning, zooming, and scrolling
 */
public class CameraIntegrationTest {
    
    private Camera2D camera;
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 600;
    
    @BeforeEach
    public void setUp() {
        camera = new Camera2D(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.setPosition(0, 0); // Start at origin
    }
    
    @Test
    public void testBasicPanningBehavior() {
        // Test immediate panning with setPosition
        camera.setPosition(10, 20);
        assertEquals(10.0, camera.getWorldX(), 0.01);
        assertEquals(20.0, camera.getWorldY(), 0.01);
        
        // Test smooth panning with panBy
        camera.panBy(5, -5);
        
        // Should not move immediately (uses smooth interpolation)
        assertEquals(10.0, camera.getWorldX(), 0.01);
        assertEquals(20.0, camera.getWorldY(), 0.01);
        
        // After update, should move towards target
        camera.update();
        assertTrue(camera.getWorldX() > 10.0, "Camera X should start moving towards target");
        assertTrue(camera.getWorldY() < 20.0, "Camera Y should start moving towards target");
        
        // After multiple updates, should reach target
        for (int i = 0; i < 100; i++) {
            camera.update();
        }
        assertEquals(15.0, camera.getWorldX(), 0.1, "Should reach target X");
        assertEquals(15.0, camera.getWorldY(), 0.1, "Should reach target Y");
    }
    
    @Test
    public void testZoomBehavior() {
        // Test basic zoom
        camera.setZoom(2.0);
        assertEquals(2.0, camera.getZoom(), 0.01);
        
        // Test zoom bounds
        camera.setZoom(0.05); // Below minimum
        assertEquals(0.1, camera.getZoom(), 0.01, "Should respect minimum zoom");
        
        camera.setZoom(15.0); // Above maximum
        assertEquals(10.0, camera.getZoom(), 0.01, "Should respect maximum zoom");
    }
    
    @Test
    public void testZoomTowardsPoint() {
        // Set initial zoom and position
        camera.setZoom(1.0);
        camera.setPosition(0, 0);
        
        // Get world position under a specific screen point
        int screenX = 600;
        int screenY = 400;
        Position originalWorldPos = camera.screenToWorld(screenX, screenY);
        
        // Zoom towards that point
        camera.zoomTowards(2.0, screenX, screenY);
        
        // The same screen position should still map to the same world position
        Position newWorldPos = camera.screenToWorld(screenX, screenY);
        
        assertEquals(originalWorldPos.getX(), newWorldPos.getX(), 1.0,
            "World X under cursor should remain the same after zoom");
        assertEquals(originalWorldPos.getY(), newWorldPos.getY(), 1.0,
            "World Y under cursor should remain the same after zoom");
    }
    
    @Test
    public void testCoordinateConsistency() {
        // Test round-trip coordinate conversion
        camera.setPosition(5, 10);
        camera.setZoom(1.5);
        
        Position originalWorld = new Position(20, 30);
        ScreenPoint screen = camera.worldToScreen(originalWorld, 0);
        Position backToWorld = camera.screenToWorld(screen.x, screen.y);
        
        assertEquals(originalWorld.getX(), backToWorld.getX(), 1.0,
            "Round-trip X conversion should be consistent");
        assertEquals(originalWorld.getY(), backToWorld.getY(), 1.0,
            "Round-trip Y conversion should be consistent");
    }
    
    @Test
    public void testPanningDirectionCorrectness() {
        // When we pan the camera right (+X), world objects should appear to move left
        camera.setPosition(0, 0);
        ScreenPoint originalScreen = camera.worldToScreen(10, 10, 0);
        
        camera.setPosition(5, 0); // Pan camera right
        ScreenPoint afterPanScreen = camera.worldToScreen(10, 10, 0);
        
        assertTrue(afterPanScreen.x < originalScreen.x,
            "Panning camera right should make world objects appear to move left on screen");
    }
    
    @Test
    public void testScrollWheelZoomSimulation() {
        // Simulate mouse wheel zoom at screen center
        int centerX = VIEWPORT_WIDTH / 2;
        int centerY = VIEWPORT_HEIGHT / 2;
        
        // Initial state
        camera.setPosition(0, 0);
        camera.setZoom(1.0);
        Position centerWorldBefore = camera.screenToWorld(centerX, centerY);
        
        // Simulate zoom in (like scroll wheel up)
        camera.zoomTowards(1.2, centerX, centerY);
        
        // Center should still map to same world position
        Position centerWorldAfter = camera.screenToWorld(centerX, centerY);
        assertEquals(centerWorldBefore.getX(), centerWorldAfter.getX(), 0.5,
            "Screen center should map to same world position after zoom");
        assertEquals(centerWorldBefore.getY(), centerWorldAfter.getY(), 0.5,
            "Screen center should map to same world position after zoom");
    }
    
    @Test
    public void testMouseDragPanningSimulation() {
        // Simulate mouse drag panning
        camera.setPosition(0, 0);
        camera.setZoom(1.0);
        
        // Get initial world position under mouse
        int mouseX = 500;
        int mouseY = 300;
        Position initialWorldUnderMouse = camera.screenToWorld(mouseX, mouseY);
        
        // Simulate dragging mouse 50 pixels to the right and 30 pixels down
        int dragDeltaX = 50;
        int dragDeltaY = 30;
        
        // Calculate world delta for mouse movement
        Position newMouseWorldPos = camera.screenToWorld(mouseX + dragDeltaX, mouseY + dragDeltaY);
        double worldDeltaX = initialWorldUnderMouse.getX() - newMouseWorldPos.getX();
        double worldDeltaY = initialWorldUnderMouse.getY() - newMouseWorldPos.getY();
        
        // Apply pan (camera should move opposite to mouse drag)
        camera.setPosition(worldDeltaX, worldDeltaY);
        
        // The original mouse position should now point to the world position where we "grabbed"
        Position finalWorldUnderMouse = camera.screenToWorld(mouseX, mouseY);
        assertEquals(initialWorldUnderMouse.getX(), finalWorldUnderMouse.getX(), 1.0,
            "Original mouse position should map to original world position after pan");
    }
    
    @Test
    public void testViewportBoundsCalculation() {
        camera.setPosition(10, 20);
        camera.setZoom(1.0);
        
        Camera2D.ViewBounds bounds = camera.getVisibleBounds();
        
        // Bounds should be reasonable for the viewport size
        assertTrue(bounds.minX < bounds.maxX, "Min X should be less than max X");
        assertTrue(bounds.minY < bounds.maxY, "Min Y should be less than max Y");
        
        // Bounds should encompass the camera position area
        assertTrue(bounds.minX <= 15 && bounds.maxX >= 5, "Bounds should include camera area");
        assertTrue(bounds.minY <= 25 && bounds.maxY >= 15, "Bounds should include camera area");
    }
} 