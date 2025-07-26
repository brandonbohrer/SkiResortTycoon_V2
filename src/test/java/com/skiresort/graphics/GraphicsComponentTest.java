package com.skiresort.graphics;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.shared.Position;
import com.skiresort.graphics.IsometricMath.ScreenPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for core graphics components used by Swing GUI
 * Tests isometric math and camera system functionality
 */
@DisplayName("Graphics Component Tests")
class GraphicsComponentTest {
    
    private Camera2D camera;
    private Mountain mountain;
    
    @BeforeEach
    void setUp() {
        camera = new Camera2D(800, 600);
        mountain = new Mountain("Test Mountain", 20, 20);
    }
    
    @Test
    @DisplayName("IsometricMath should correctly convert world to screen coordinates")
    void testWorldToScreenConversion() {
        // Test origin
        ScreenPoint origin = IsometricMath.worldToScreen(0, 0, 0);
        assertEquals(0, origin.x);
        assertEquals(0, origin.y);
        
        // Test positive coordinates
        ScreenPoint positive = IsometricMath.worldToScreen(2, 2, 0);
        assertEquals(0, positive.x); // (2-2) * 32 = 0
        assertEquals(64, positive.y); // (2+2) * 16 = 64
        
        // Test with elevation
        ScreenPoint elevated = IsometricMath.worldToScreen(1, 1, 10);
        assertEquals(0, elevated.x); // (1-1) * 32 = 0  
        assertEquals(32 - 80, elevated.y); // (1+1) * 16 - (10 * 8) = 32 - 80 = -48
    }
    
    @Test
    @DisplayName("IsometricMath should correctly convert screen to world coordinates")
    void testScreenToWorldConversion() {
        // Test round trip conversion
        Position original = new Position(5, 3);
        ScreenPoint screen = IsometricMath.worldToScreen(original, 0);
        Position converted = IsometricMath.screenToWorld(screen.x, screen.y);
        
        assertEquals(original.getX(), converted.getX());
        assertEquals(original.getY(), converted.getY());
    }
    
    @Test
    @DisplayName("IsometricMath should calculate correct tile bounds")
    void testTileBounds() {
        ScreenPoint[] bounds = IsometricMath.getTileBounds(0, 0, 0);
        
        assertEquals(4, bounds.length);
        
        // Check diamond shape (top, right, bottom, left)
        assertEquals(0, bounds[0].x);    // Top center
        assertEquals(-16, bounds[0].y);  // Top y
        assertEquals(32, bounds[1].x);   // Right x
        assertEquals(0, bounds[1].y);    // Right center y
        assertEquals(0, bounds[2].x);    // Bottom center x
        assertEquals(16, bounds[2].y);   // Bottom y
        assertEquals(-32, bounds[3].x);  // Left x
        assertEquals(0, bounds[3].y);    // Left center y
    }
    
    @Test
    @DisplayName("IsometricMath should correctly detect point in tile")
    void testPointInTile() {
        // Point in center should be inside
        assertTrue(IsometricMath.isPointInTile(0, 0, 0, 0, 0));
        
        // Points at edges should be inside
        assertTrue(IsometricMath.isPointInTile(0, -15, 0, 0, 0)); // Near top
        assertTrue(IsometricMath.isPointInTile(30, 0, 0, 0, 0));  // Near right
        
        // Points far outside should be outside
        assertFalse(IsometricMath.isPointInTile(100, 100, 0, 0, 0));
        assertFalse(IsometricMath.isPointInTile(-100, -100, 0, 0, 0));
    }
    
    @Test
    @DisplayName("IsometricMath should calculate proper draw order")
    void testDrawOrder() {
        // Further back should have lower draw order (drawn first)
        int order1 = IsometricMath.calculateDrawOrder(0, 0, 0);
        int order2 = IsometricMath.calculateDrawOrder(0, 5, 0);
        assertTrue(order2 > order1); // Further back = higher priority
        
        // Higher elevation should have higher draw order (drawn later)
        int lowOrder = IsometricMath.calculateDrawOrder(0, 0, 0);
        int highOrder = IsometricMath.calculateDrawOrder(0, 0, 10);
        assertTrue(highOrder < lowOrder); // Higher elevation = lower priority
    }
    
    @Test
    @DisplayName("Camera2D should initialize with correct defaults")
    void testCameraInitialization() {
        assertEquals(0.0, camera.getWorldX(), 0.001);
        assertEquals(0.0, camera.getWorldY(), 0.001);
        assertEquals(1.0, camera.getZoom(), 0.001);
        assertEquals(800, camera.getViewportWidth());
        assertEquals(600, camera.getViewportHeight());
    }
    
    @Test
    @DisplayName("Camera2D should handle panning correctly")
    void testCameraPanning() {
        camera.panTo(10, 5);
        camera.update(); // Apply movement
        
        // Should move towards target
        assertTrue(camera.getWorldX() > 0);
        assertTrue(camera.getWorldY() > 0);
        
        // Test immediate positioning
        camera.setPosition(20, 15);
        assertEquals(20.0, camera.getWorldX(), 0.001);
        assertEquals(15.0, camera.getWorldY(), 0.001);
        
        // Test relative panning
        camera.panBy(5, -3);
        camera.update();
        assertTrue(camera.getWorldX() > 20);
        assertTrue(camera.getWorldY() < 15);
    }
    
    @Test
    @DisplayName("Camera2D should handle zooming correctly")
    void testCameraZooming() {
        camera.setZoom(2.0);
        assertEquals(2.0, camera.getZoom(), 0.001);
        
        // Test zoom limits
        camera.setZoom(10.0); // Above max
        assertEquals(camera.getMaxZoom(), camera.getZoom(), 0.001);
        
        camera.setZoom(0.1); // Below min
        assertEquals(camera.getMinZoom(), camera.getZoom(), 0.001);
        
        // Test zoom by factor
        camera.setZoom(1.0);
        camera.zoomBy(1.5);
        assertEquals(1.5, camera.getZoom(), 0.001);
    }
    
    @Test
    @DisplayName("Camera2D should convert coordinates correctly")
    void testCameraCoordinateConversion() {
        // Test with camera at origin
        ScreenPoint screen = camera.worldToScreen(5, 5, 0);
        Position world = camera.screenToWorld(screen.x, screen.y);
        
        assertEquals(5, world.getX());
        assertEquals(5, world.getY());
        
        // Test with camera moved
        camera.setPosition(10, 10);
        screen = camera.worldToScreen(15, 15, 0);
        world = camera.screenToWorld(screen.x, screen.y);
        
        assertEquals(15, world.getX());
        assertEquals(15, world.getY());
    }
    
    @Test
    @DisplayName("Camera2D should calculate visible bounds correctly")
    void testCameraVisibleBounds() {
        Camera2D.ViewBounds bounds = camera.getVisibleBounds();
        
        assertNotNull(bounds);
        assertTrue(bounds.maxX >= bounds.minX);
        assertTrue(bounds.maxY >= bounds.minY);
        
        // Bounds should include some padding
        assertTrue(bounds.maxX - bounds.minX > 10); // Should be reasonably wide
        assertTrue(bounds.maxY - bounds.minY > 10); // Should be reasonably tall
    }
    
    @Test
    @DisplayName("Camera2D should detect visibility correctly")
    void testCameraVisibility() {
        // Center position should be visible
        assertTrue(camera.isVisible(0, 0, 0));
        
        // Positions far outside should not be visible
        assertFalse(camera.isVisible(1000, 1000, 0));
        assertFalse(camera.isVisible(-1000, -1000, 0));
    }
    
    @Test
    @DisplayName("Camera2D should handle centering correctly")
    void testCameraCentering() {
        Position target = new Position(15, 20);
        camera.centerOn(target);
        camera.update();
        
        // Should move towards target
        assertTrue(camera.getWorldX() > 0);
        assertTrue(camera.getWorldY() > 0);
    }
    
    @Test
    @DisplayName("Camera2D should handle viewport resizing")
    void testCameraViewportResize() {
        camera.setViewportSize(1024, 768);
        
        assertEquals(1024, camera.getViewportWidth());
        assertEquals(768, camera.getViewportHeight());
    }
    
    @Test
    @DisplayName("ScreenPoint should have correct equality and hashing")
    void testScreenPointEquality() {
        ScreenPoint p1 = new ScreenPoint(10, 20);
        ScreenPoint p2 = new ScreenPoint(10, 20);
        ScreenPoint p3 = new ScreenPoint(15, 25);
        
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }
    
    @Test
    @DisplayName("ViewBounds should contain points correctly")
    void testViewBoundsContainment() {
        Camera2D.ViewBounds bounds = new Camera2D.ViewBounds(0, 0, 10, 10);
        
        assertTrue(bounds.contains(5, 5));
        assertTrue(bounds.contains(0, 0));
        assertTrue(bounds.contains(10, 10));
        assertFalse(bounds.contains(-1, 5));
        assertFalse(bounds.contains(5, 11));
    }
} 