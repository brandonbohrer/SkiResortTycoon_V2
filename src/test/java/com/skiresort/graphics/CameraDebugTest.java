package com.skiresort.graphics;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.skiresort.shared.Position;
import com.skiresort.graphics.IsometricMath.ScreenPoint;

/**
 * Simple debug test to see actual coordinate values
 */
public class CameraDebugTest {
    
    private Camera2D camera;
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 600;
    
    @BeforeEach
    public void setUp() {
        camera = new Camera2D(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }
    
    @Test
    public void debugCoordinateValues() {
        // Test the isometric transformation directly first
        System.out.println("=== Isometric Math Debug ===");
        ScreenPoint iso_0_0 = IsometricMath.worldToScreen(0, 0, 0);
        ScreenPoint iso_10_0 = IsometricMath.worldToScreen(10, 0, 0);
        ScreenPoint iso_0_10 = IsometricMath.worldToScreen(0, 10, 0);
        ScreenPoint iso_10_10 = IsometricMath.worldToScreen(10, 10, 0);
        
        System.out.printf("World (0,0) -> Iso (%d,%d)%n", iso_0_0.x, iso_0_0.y);
        System.out.printf("World (10,0) -> Iso (%d,%d) [deltaX=%d, deltaY=%d]%n", 
            iso_10_0.x, iso_10_0.y, iso_10_0.x - iso_0_0.x, iso_10_0.y - iso_0_0.y);
        System.out.printf("World (0,10) -> Iso (%d,%d) [deltaX=%d, deltaY=%d]%n", 
            iso_0_10.x, iso_0_10.y, iso_0_10.x - iso_0_0.x, iso_0_10.y - iso_0_0.y);
        System.out.printf("World (10,10) -> Iso (%d,%d) [deltaX=%d, deltaY=%d]%n", 
            iso_10_10.x, iso_10_10.y, iso_10_10.x - iso_0_0.x, iso_10_10.y - iso_0_0.y);
        
        System.out.println("\n=== Camera Transform Debug ===");
        // Fixed world point
        Position worldPoint = new Position(0, 0);
        
        // Camera at origin
        camera.setPosition(0, 0);
        ScreenPoint atOrigin = camera.worldToScreen(worldPoint, 0);
        System.out.printf("Camera at (0,0): world(0,0) -> screen(%d,%d)%n", atOrigin.x, atOrigin.y);
        
        // Move camera right and down
        camera.setPosition(10, 10);
        ScreenPoint afterMove = camera.worldToScreen(worldPoint, 0);
        System.out.printf("Camera at (10,10): world(0,0) -> screen(%d,%d)%n", afterMove.x, afterMove.y);
        
        // Calculate movement
        int deltaX = afterMove.x - atOrigin.x;
        int deltaY = afterMove.y - atOrigin.y;
        System.out.printf("Screen movement: (%d,%d)%n", deltaX, deltaY);
        
        // Show expected vs actual
        System.out.printf("Expected: negative X (left), negative Y (up)%n");
        System.out.printf("Actual: deltaX=%d (%s), deltaY=%d (%s)%n", 
            deltaX, deltaX < 0 ? "left" : "right",
            deltaY, deltaY < 0 ? "up" : "down");
        
        // Test what SHOULD happen according to isometric math
        System.out.println("\n=== Analysis ===");
        System.out.printf("When camera moves (10,10), it should subtract (%d,%d) from screen coordinates%n",
            iso_10_10.x, iso_10_10.y);
        System.out.printf("Actual screen movement was (%d,%d)%n", deltaX, deltaY);
        System.out.printf("Match? X: %s, Y: %s%n", 
            deltaX == -iso_10_10.x ? "YES" : "NO",
            deltaY == -iso_10_10.y ? "YES" : "NO");
        
        // Force pass so we can see output
        assertTrue(true);
    }

    @Test
    public void debugPrecisionLoss() {
        System.out.println("=== Detailed Precision Analysis ===");
        
        // Test the problematic coordinate conversion
        int screenX = 500;
        int screenY = 350;
        
        System.out.printf("Original screen: (%d, %d)%n", screenX, screenY);
        
        // Manual calculation to see intermediate values
        double halfTileWidth = 64 / 2.0; // TILE_WIDTH / 2
        double halfTileHeight = 32 / 2.0; // TILE_HEIGHT / 2
        
        double worldX = (screenX / halfTileWidth + screenY / halfTileHeight) / 2.0;
        double worldY = (screenY / halfTileHeight - screenX / halfTileWidth) / 2.0;
        
        System.out.printf("Calculated world (double): (%.6f, %.6f)%n", worldX, worldY);
        
        // Test different rounding methods
        int roundedX = (int)Math.round(worldX);
        int roundedY = (int)Math.round(worldY);
        System.out.printf("Math.round: (%d, %d)%n", roundedX, roundedY);
        
        int floorX = (int)Math.floor(worldX);
        int floorY = (int)Math.floor(worldY);
        System.out.printf("Math.floor: (%d, %d)%n", floorX, floorY);
        
        int ceilX = (int)Math.ceil(worldX);
        int ceilY = (int)Math.ceil(worldY);
        System.out.printf("Math.ceil: (%d, %d)%n", ceilX, ceilY);
        
        // Test reverse conversion for each rounding method
        System.out.println("\nReverse conversion tests:");
        
        // Test Math.round result
        ScreenPoint roundBack = IsometricMath.worldToScreen(roundedX, roundedY, 0);
        double roundError = Math.sqrt((roundBack.x - screenX)*(roundBack.x - screenX) + (roundBack.y - screenY)*(roundBack.y - screenY));
        System.out.printf("Round -> (%d,%d) -> (%d,%d) error=%.2f%n", roundedX, roundedY, roundBack.x, roundBack.y, roundError);
        
        // Test Math.floor result  
        ScreenPoint floorBack = IsometricMath.worldToScreen(floorX, floorY, 0);
        double floorError = Math.sqrt((floorBack.x - screenX)*(floorBack.x - screenX) + (floorBack.y - screenY)*(floorBack.y - screenY));
        System.out.printf("Floor -> (%d,%d) -> (%d,%d) error=%.2f%n", floorX, floorY, floorBack.x, floorBack.y, floorError);
        
        // Test Math.ceil result
        ScreenPoint ceilBack = IsometricMath.worldToScreen(ceilX, ceilY, 0);
        double ceilError = Math.sqrt((ceilBack.x - screenX)*(ceilBack.x - screenX) + (ceilBack.y - screenY)*(ceilBack.y - screenY));
        System.out.printf("Ceil -> (%d,%d) -> (%d,%d) error=%.2f%n", ceilX, ceilY, ceilBack.x, ceilBack.y, ceilError);
        
        // Test what IsometricMath.screenToWorld actually returns
        Position actual = IsometricMath.screenToWorld(screenX, screenY);
        ScreenPoint actualBack = IsometricMath.worldToScreen(actual, 0);
        double actualError = Math.sqrt((actualBack.x - screenX)*(actualBack.x - screenX) + (actualBack.y - screenY)*(actualBack.y - screenY));
        System.out.printf("Actual method -> (%d,%d) -> (%d,%d) error=%.2f%n", actual.getX(), actual.getY(), actualBack.x, actualBack.y, actualError);
        
        assertTrue(true); // Always pass, just want to see debug output
    }

    @Test
    public void compareNewVsOldAlgorithm() {
        System.out.println("=== New vs Old Algorithm Comparison ===");
        
        int screenX = 500;
        int screenY = 350;
        
        // Test the new algorithm (current implementation)
        Position newResult = IsometricMath.screenToWorld(screenX, screenY);
        ScreenPoint newBackToScreen = IsometricMath.worldToScreen(newResult, 0);
        double newError = Math.sqrt(
            (newBackToScreen.x - screenX) * (newBackToScreen.x - screenX) + 
            (newBackToScreen.y - screenY) * (newBackToScreen.y - screenY)
        );
        
        System.out.printf("New algorithm: screen(%d,%d) -> world(%d,%d) -> screen(%d,%d) error=%.2f%n",
            screenX, screenY, newResult.getX(), newResult.getY(), 
            newBackToScreen.x, newBackToScreen.y, newError);
        
        // Test the old simple algorithm manually
        double halfTileWidth = 64 / 2.0;
        double halfTileHeight = 32 / 2.0;
        double worldX = (screenX / halfTileWidth + screenY / halfTileHeight) / 2.0;
        double worldY = (screenY / halfTileHeight - screenX / halfTileWidth) / 2.0;
        int oldX = (int)Math.round(worldX);
        int oldY = (int)Math.round(worldY);
        
        ScreenPoint oldBackToScreen = IsometricMath.worldToScreen(oldX, oldY, 0);
        double oldError = Math.sqrt(
            (oldBackToScreen.x - screenX) * (oldBackToScreen.x - screenX) + 
            (oldBackToScreen.y - screenY) * (oldBackToScreen.y - screenY)
        );
        
        System.out.printf("Old algorithm: screen(%d,%d) -> world(%d,%d) -> screen(%d,%d) error=%.2f%n",
            screenX, screenY, oldX, oldY, 
            oldBackToScreen.x, oldBackToScreen.y, oldError);
        
        System.out.printf("Improvement: %.2f -> %.2f (%.1f%% %s)%n", 
            oldError, newError, 
            Math.abs(newError - oldError) / oldError * 100,
            newError < oldError ? "better" : "worse");
        
        assertTrue(true);
    }

    @Test
    public void debugScreenCenter() {
        System.out.println("=== Screen Center Debug ===");
        
        // What world coordinate is at the screen center when camera is at (0,0)?
        camera.setPosition(0, 0);
        
        int screenCenterX = VIEWPORT_WIDTH / 2;  // 400
        int screenCenterY = VIEWPORT_HEIGHT / 2; // 300
        
        Position centerWorld = camera.screenToWorld(screenCenterX, screenCenterY);
        System.out.printf("Screen center (%d,%d) -> World(%d,%d)%n", 
            screenCenterX, screenCenterY, centerWorld.getX(), centerWorld.getY());
        
        // This should be (0,0) or close to it for a camera at origin!
        // If it's not, then the camera transform is broken
        
        // Test direct isometric conversion (no camera)
        Position directWorld = IsometricMath.screenToWorld(screenCenterX, screenCenterY);
        System.out.printf("Direct conversion (%d,%d) -> World(%d,%d)%n", 
            screenCenterX, screenCenterY, directWorld.getX(), directWorld.getY());
        
        // Test what (0,0) world maps to on screen
        ScreenPoint originScreen = camera.worldToScreen(0, 0, 0);
        System.out.printf("World(0,0) -> Screen(%d,%d)%n", originScreen.x, originScreen.y);
        
        assertTrue(true);
    }
} 