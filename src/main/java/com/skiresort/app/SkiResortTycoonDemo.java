package com.skiresort.app;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.mountain.visual.MountainRenderer;
import com.skiresort.shared.Position;

/**
 * Simple demo application showcasing the Ski Resort Tycoon Mountain component
 */
public class SkiResortTycoonDemo {
    
    public static void main(String[] args) {
        System.out.println("🎿 Welcome to Ski Resort Tycoon! 🎿");
        System.out.println("=".repeat(50));
        
        // Create a mountain
        Mountain mountain = new Mountain("Alpine Paradise", 25, 25);
        
        System.out.println("\n📊 Initial Mountain Stats:");
        System.out.println(MountainRenderer.renderElevationStats(mountain));
        
        // Show the initial mountain view
        System.out.println("\n🏔️  Initial Mountain View:");
        System.out.println(MountainRenderer.renderMountainView(mountain));
        
        // Add some slopes
        System.out.println("\n🛷 Building Slopes...");
        
        // Build a beginner slope
        Position beginnerStart = new Position(12, 5);
        Position beginnerEnd = new Position(12, 20);
        Slope beginnerSlope = new Slope("Bunny Hill", beginnerStart, beginnerEnd, 
            Slope.Difficulty.BEGINNER, 5000);
        
        if (mountain.addSlope(beginnerSlope)) {
            System.out.println("✅ Successfully built: " + beginnerSlope.getName());
        } else {
            System.out.println("❌ Failed to build beginner slope");
        }
        
        // Build an intermediate slope
        Position intermediateStart = new Position(8, 8);
        Position intermediateEnd = new Position(8, 22);
        Slope intermediateSlope = new Slope("Blue Thunder", intermediateStart, intermediateEnd, 
            Slope.Difficulty.INTERMEDIATE, 12000);
        
        if (mountain.addSlope(intermediateSlope)) {
            System.out.println("✅ Successfully built: " + intermediateSlope.getName());
        } else {
            System.out.println("❌ Failed to build intermediate slope");
        }
        
        // Build an advanced slope
        Position advancedStart = new Position(16, 6);
        Position advancedEnd = new Position(16, 23);
        Slope advancedSlope = new Slope("Black Diamond Express", advancedStart, advancedEnd, 
            Slope.Difficulty.ADVANCED, 20000);
        
        if (mountain.addSlope(advancedSlope)) {
            System.out.println("✅ Successfully built: " + advancedSlope.getName());
        } else {
            System.out.println("❌ Failed to build advanced slope");
        }
        
        // Add some lifts
        System.out.println("\n🚠 Building Lifts...");
        
        // Build a magic carpet for beginners
        Position carpetBottom = new Position(12, 20);
        Position carpetTop = new Position(12, 15);
        Lift magicCarpet = new Lift("Magic Carpet", carpetBottom, carpetTop, 
            Lift.LiftType.MAGIC_CARPET);
        
        if (mountain.addLift(magicCarpet)) {
            System.out.println("✅ Successfully built: " + magicCarpet.getName());
        } else {
            System.out.println("❌ Failed to build magic carpet");
        }
        
        // Build a chairlift
        Position chairBottom = new Position(10, 22);
        Position chairTop = new Position(10, 8);
        Lift chairlift = new Lift("Summit Express", chairBottom, chairTop, 
            Lift.LiftType.CHAIRLIFT);
        
        if (mountain.addLift(chairlift)) {
            System.out.println("✅ Successfully built: " + chairlift.getName());
        } else {
            System.out.println("❌ Failed to build chairlift");
        }
        
        // Build a gondola
        Position gondolaBottom = new Position(18, 23);
        Position gondolaTop = new Position(18, 5);
        Lift gondola = new Lift("Sky Gondola", gondolaBottom, gondolaTop, 
            Lift.LiftType.GONDOLA);
        
        if (mountain.addLift(gondola)) {
            System.out.println("✅ Successfully built: " + gondola.getName());
        } else {
            System.out.println("❌ Failed to build gondola");
        }
        
        // Show the updated mountain view
        System.out.println("\n🏔️  Updated Mountain View with Infrastructure:");
        System.out.println(MountainRenderer.renderMountainView(mountain));
        
        // Show detailed information
        System.out.println("\n📋 Detailed Mountain Information:");
        System.out.println(MountainRenderer.renderMountainDetails(mountain));
        
        // Simulate some operations
        System.out.println("\n🎮 Simulation Results:");
        
        // Test lift capacity
        System.out.println("\n🚠 Testing Lift Operations:");
        if (chairlift.addGuests(8)) {
            System.out.println("✅ " + chairlift.getName() + " accepted 8 guests");
            System.out.println("   Current capacity: " + chairlift.getCurrentCapacity() + "/" + chairlift.getHourlyThroughput());
        }
        
        if (gondola.addGuests(15)) {
            System.out.println("✅ " + gondola.getName() + " accepted 15 guests");
            System.out.println("   Current capacity: " + gondola.getCurrentCapacity() + "/" + gondola.getHourlyThroughput());
        }
        
        // Show appeal scores
        System.out.println("\n🌟 Attraction Appeal Scores:");
        for (Slope slope : mountain.getSlopes()) {
            System.out.println("  " + slope.getName() + ": " + slope.getAppealScore() + "/100");
        }
        
        for (Lift lift : mountain.getLifts()) {
            System.out.println("  " + lift.getName() + ": " + lift.getAppealScore() + "/100");
        }
        
        // Calculate total build costs
        double totalSlopeCost = mountain.getSlopes().stream()
            .mapToDouble(Slope::getBuildCost)
            .sum();
        
        double totalLiftCost = mountain.getLifts().stream()
            .mapToDouble(Lift::getBuildCost)
            .sum();
        
        System.out.println("\n💰 Financial Summary:");
        System.out.println("  Total Slope Investment: $" + String.format("%.0f", totalSlopeCost));
        System.out.println("  Total Lift Investment: $" + String.format("%.0f", totalLiftCost));
        System.out.println("  Total Resort Investment: $" + String.format("%.0f", totalSlopeCost + totalLiftCost));
        
        // Show mountain legend
        System.out.println("\n🗺️  Mountain View Legend:");
        System.out.println("  ^ = Peak (elevation > 80)");
        System.out.println("  * = High elevation (60-80)");
        System.out.println("  + = Medium elevation (40-60)");
        System.out.println("  - = Low elevation (20-40)");
        System.out.println("  . = Very low/flat (< 20)");
        System.out.println("  S = Ski Slope");
        System.out.println("  L = Ski Lift");
        System.out.println("  X = Intersection (Slope & Lift)");
        
        System.out.println("\n🎉 Demo completed! The Mountain component is working perfectly!");
        System.out.println("Ready to add more game features like guests, finances, and weather!");
    }
} 