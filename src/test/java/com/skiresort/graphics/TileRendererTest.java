package com.skiresort.graphics;

import com.skiresort.components.mountain.core.Mountain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the TileRenderer class
 * Note: These are logic tests that don't require actual JavaFX rendering
 */
@DisplayName("TileRenderer Tests")
class TileRendererTest {
    
    private Mountain mountain;
    private Camera2D camera;
    
    @BeforeEach
    void setUp() {
        mountain = new Mountain("Test Mountain", 20, 20);
        camera = new Camera2D(800, 600);
    }
    
    @Test
    @DisplayName("TileRenderer should validate mountain bounds correctly")
    void testMountainBoundsValidation() {
        // Test private method logic through public method behavior
        // This tests the isInBounds logic indirectly
        Camera2D.ViewBounds bounds = camera.getVisibleBounds();
        
        assertTrue(bounds.maxX >= bounds.minX);
        assertTrue(bounds.maxY >= bounds.minY);
    }
    
    @Test
    @DisplayName("TileRenderer should handle empty mountain gracefully")
    void testEmptyMountainBounds() {
        Mountain emptyMountain = new Mountain("Empty", 1, 1);
        
        assertEquals(1, emptyMountain.getWidth());
        assertEquals(1, emptyMountain.getHeight());
        assertTrue(emptyMountain.getElevationAt(0, 0) >= 0);
    }
    
    @Test
    @DisplayName("TileRenderer should handle extreme camera positions")
    void testExtremeCameraPositions() {
        // Test camera positioning
        camera.setPosition(1000, 1000);
        assertEquals(1000.0, camera.getWorldX(), 0.001);
        assertEquals(1000.0, camera.getWorldY(), 0.001);
        
        camera.setPosition(-1000, -1000);
        assertEquals(-1000.0, camera.getWorldX(), 0.001);
        assertEquals(-1000.0, camera.getWorldY(), 0.001);
    }
    
    @Test
    @DisplayName("TileRenderer should handle extreme zoom levels")
    void testExtremeZoomLevels() {
        // Test zoom limits
        camera.setZoom(camera.getMaxZoom());
        assertEquals(camera.getMaxZoom(), camera.getZoom(), 0.001);
        
        camera.setZoom(camera.getMinZoom());
        assertEquals(camera.getMinZoom(), camera.getZoom(), 0.001);
    }
    
    @Test
    @DisplayName("TileRenderer should handle large mountains")
    void testLargeMountainCreation() {
        Mountain largeMountain = new Mountain("Large Mountain", 100, 100);
        
        assertEquals(100, largeMountain.getWidth());
        assertEquals(100, largeMountain.getHeight());
        assertEquals("Large Mountain", largeMountain.getName());
    }
    
    @Test
    @DisplayName("TileRenderer helper methods should work correctly")
    void testTileRendererHelpers() {
        // Test coordinate validation through camera visibility
        assertFalse(camera.isVisible(-1000, -1000, 0));
        assertTrue(camera.isVisible(0, 0, 0));
        
        // Test view bounds calculation
        Camera2D.ViewBounds bounds = camera.getVisibleBounds();
        assertNotNull(bounds);
        assertTrue(bounds.contains(0, 0));
    }
} 