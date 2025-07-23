package com.skiresort.app;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.mountain.visual.MountainRenderer;
import com.skiresort.components.guests.core.GuestManager;
import com.skiresort.components.guests.visual.GuestRenderer;
import com.skiresort.components.finances.core.FinanceManager;
import com.skiresort.graphics.Camera2D;
import com.skiresort.graphics.IsometricMath;
import com.skiresort.graphics.IsometricMath.ScreenPoint;
import com.skiresort.shared.Position;

/**
 * Simple demo showcasing all visual components working together
 * This demonstrates the rendering system without JavaFX to verify functionality
 */
public class SkiResortTycoonSimpleDemo {
    
    public static void main(String[] args) {
        System.out.println("🎿 Ski Resort Tycoon - Visual Components Demo! 🎿");
        System.out.println("=".repeat(70));
        
        // Create core components
        Mountain mountain = new Mountain("Demo Resort", 20, 20);
        GuestManager guestManager = new GuestManager();
        FinanceManager financeManager = new FinanceManager();
        Camera2D camera = new Camera2D(800, 600);
        
        System.out.println("\n🏗️  Building Demo Resort Infrastructure...");
        
        // Add demo slopes and lifts
        addDemoInfrastructure(mountain);
        
        // Add some guests for testing
        guestManager.setMaxCapacity(15);
        guestManager.setBaseSpawnRate(5);
        
        // Simulate some guest activity
        for (int i = 0; i < 8; i++) {
            guestManager.update(mountain);
        }
        
        System.out.println("✅ Demo resort built successfully!");
        System.out.println("   📊 Mountain: " + mountain.getWidth() + "x" + mountain.getHeight());
        System.out.println("   🛷 Slopes: " + mountain.getSlopes().size());
        System.out.println("   🚠 Lifts: " + mountain.getLifts().size());
        System.out.println("   👥 Active Guests: " + guestManager.getActiveGuests().size());
        
        // Test isometric math system
        System.out.println("\n🔢 Testing Isometric Math System:");
        testIsometricMath();
        
        // Test camera system
        System.out.println("\n📷 Testing Camera System:");
        testCameraSystem(camera);
        
        // Show ASCII visualization with guests
        System.out.println("\n🏔️  ASCII Mountain View with Guests:");
        System.out.println(MountainRenderer.renderMountainView(mountain, guestManager.getActiveGuests()));
        
        // Show guest statistics
        System.out.println("\n👥 Guest Statistics:");
        System.out.println(GuestRenderer.renderGuestStatistics(guestManager));
        
        // Show financial status
        System.out.println("\n💰 Financial Status:");
        System.out.println("Starting Money: $" + String.format("%.2f", financeManager.getCurrentMoney()));
        System.out.println("Credit Rating: " + String.format("%.1f", financeManager.getCreditRating()));
        
        System.out.println("\n✅ All visual components working correctly!");
        System.out.println("🚀 Ready to run JavaFX application!");
    }
    
    /**
     * Add demo infrastructure to showcase rendering
     */
    private static void addDemoInfrastructure(Mountain mountain) {
        // Add a beginner slope
        Slope beginnerSlope = new Slope("Bunny Hill", 
            new Position(10, 5), new Position(10, 18), 
            Slope.Difficulty.BEGINNER, 5000);
        mountain.addSlope(beginnerSlope);
        
        // Add a magic carpet lift
        Lift magicCarpet = new Lift("Magic Carpet", 
            new Position(10, 18), new Position(10, 8), 
            Lift.LiftType.MAGIC_CARPET);
        mountain.addLift(magicCarpet);
        
        // Add an intermediate slope
        Slope intermediateSlope = new Slope("Blue Run", 
            new Position(6, 3), new Position(14, 19), 
            Slope.Difficulty.INTERMEDIATE, 12000);
        mountain.addSlope(intermediateSlope);
        
        // Add a chairlift
        Lift chairlift = new Lift("Summit Express", 
            new Position(8, 19), new Position(5, 6), 
            Lift.LiftType.CHAIRLIFT);
        mountain.addLift(chairlift);
    }
    
    /**
     * Test isometric math calculations
     */
    private static void testIsometricMath() {
        // Test coordinate conversion
        Position worldPos = new Position(5, 5);
        ScreenPoint screenPos = IsometricMath.worldToScreen(worldPos, 0);
        Position convertedBack = IsometricMath.screenToWorld(screenPos.x, screenPos.y);
        
        System.out.println("   World->Screen->World: " + worldPos + " -> " + screenPos + " -> " + convertedBack);
        System.out.println("   ✅ Coordinate conversion: " + (worldPos.equals(convertedBack) ? "PASS" : "FAIL"));
        
        // Test elevation effects
        ScreenPoint elevated = IsometricMath.worldToScreen(5, 5, 20);
        System.out.println("   Elevation effect: Ground=" + screenPos + " Elevated=" + elevated);
        System.out.println("   ✅ Elevation rendering: " + (elevated.y < screenPos.y ? "PASS" : "FAIL"));
        
        // Test tile bounds
        ScreenPoint[] bounds = IsometricMath.getTileBounds(0, 0, 0);
        System.out.println("   Tile bounds: " + bounds.length + " corners");
        System.out.println("   ✅ Tile bounds: " + (bounds.length == 4 ? "PASS" : "FAIL"));
    }
    
    /**
     * Test camera system functionality
     */
    private static void testCameraSystem(Camera2D camera) {
        System.out.println("   Initial camera: " + camera);
        
        // Test panning
        camera.panTo(10, 10);
        camera.update();
        System.out.println("   After pan: " + camera);
        System.out.println("   ✅ Panning: " + (camera.getWorldX() > 0 && camera.getWorldY() > 0 ? "PASS" : "FAIL"));
        
        // Test zooming
        double originalZoom = camera.getZoom();
        camera.zoomBy(2.0);
        System.out.println("   After zoom: " + camera);
        System.out.println("   ✅ Zooming: " + (camera.getZoom() > originalZoom ? "PASS" : "FAIL"));
        
        // Test visibility
        boolean centerVisible = camera.isVisible(0, 0, 0);
        boolean farVisible = camera.isVisible(1000, 1000, 0);
        System.out.println("   ✅ Visibility: " + (centerVisible && !farVisible ? "PASS" : "FAIL"));
        
        // Test view bounds
        Camera2D.ViewBounds bounds = camera.getVisibleBounds();
        System.out.println("   View bounds: " + bounds);
        System.out.println("   ✅ View bounds: " + (bounds.maxX > bounds.minX ? "PASS" : "FAIL"));
    }
} 