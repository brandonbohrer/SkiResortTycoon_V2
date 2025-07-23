package com.skiresort.app;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.mountain.visual.MountainRenderer;
import com.skiresort.components.guests.core.GuestManager;
import com.skiresort.components.guests.visual.GuestRenderer;
import com.skiresort.shared.Position;

/**
 * Enhanced demo showcasing the Guest system with live simulation
 * Shows guests moving through the resort in real-time
 */
public class SkiResortTycoonGuestDemo {
    
    public static void main(String[] args) {
        System.out.println("🎿 Ski Resort Tycoon - Guest System Demo! 🎿");
        System.out.println("=".repeat(60));
        
        // Create a mountain
        Mountain mountain = new Mountain("Alpine Paradise", 20, 20);
        
        // Build some infrastructure for guests to use
        buildResortInfrastructure(mountain);
        
        // Create guest manager
        GuestManager guestManager = new GuestManager();
        guestManager.setMaxCapacity(25);  // Moderate capacity for demo
        guestManager.setBaseSpawnRate(8); // Reasonable spawn rate
        
        System.out.println("\n🏗️  Resort Infrastructure Built!");
        System.out.println("📊 Mountain Stats:");
        System.out.println("   • Slopes: " + mountain.getSlopes().size());
        System.out.println("   • Lifts: " + mountain.getLifts().size());
        System.out.println("   • Max Guests: " + guestManager.getMaxCapacity());
        
        // Show initial mountain
        System.out.println("\n🏔️  Initial Mountain View:");
        System.out.println(MountainRenderer.renderMountainView(mountain));
        
        // Run simulation for several iterations
        System.out.println("🎮 Starting Guest Simulation...\n");
        
        for (int cycle = 1; cycle <= 15; cycle++) {
            System.out.println("⏰ === Simulation Cycle " + cycle + " ===");
            
            // Update guest manager (spawn and update guests)
            guestManager.update(mountain);
            
            // Show current statistics
            System.out.println(GuestRenderer.renderGuestStatistics(guestManager));
            
            // Show mountain with guests every few cycles
            if (cycle % 3 == 0) {
                System.out.println("\n🏔️  Mountain View with Active Guests:");
                System.out.println(MountainRenderer.renderMountainView(mountain, guestManager.getActiveGuests()));
                
                if (guestManager.getActiveGuestCount() > 0) {
                    System.out.println(GuestRenderer.renderIndividualGuests(guestManager.getActiveGuests()));
                    System.out.println(GuestRenderer.renderSatisfactionAnalysis(guestManager.getActiveGuests()));
                }
            }
            
            // Brief pause for readability (in real game this would be natural timing)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            System.out.println("-".repeat(50));
        }
        
        // Final results
        System.out.println("\n🎯 === Final Simulation Results ===");
        System.out.println(GuestRenderer.renderGuestStatistics(guestManager));
        
        if (guestManager.getActiveGuestCount() > 0) {
            System.out.println("\n🏔️  Final Mountain State:");
            System.out.println(MountainRenderer.renderMountainView(mountain, guestManager.getActiveGuests()));
            
            System.out.println(GuestRenderer.renderSatisfactionAnalysis(guestManager.getActiveGuests()));
        }
        
        // Show legend
        System.out.println(GuestRenderer.renderGuestLegend());
        
        // Performance summary
        double averageSatisfaction = guestManager.getAverageSatisfaction();
        int totalGuests = guestManager.getTotalGuestsSpawned();
        int activeGuests = guestManager.getActiveGuestCount();
        
        System.out.println("\n📈 Performance Summary:");
        System.out.println("   • Total Guests Visited: " + totalGuests);
        System.out.println("   • Currently Active: " + activeGuests);
        System.out.println("   • Average Satisfaction: " + String.format("%.1f/100", averageSatisfaction));
        
        if (averageSatisfaction >= 80) {
            System.out.println("   🌟 Excellent! Your resort is a huge success!");
        } else if (averageSatisfaction >= 60) {
            System.out.println("   😊 Good job! Guests are generally happy.");
        } else if (averageSatisfaction >= 40) {
            System.out.println("   😐 Room for improvement. Consider adding more facilities.");
        } else {
            System.out.println("   😞 Guests are unhappy. Major improvements needed!");
        }
        
        System.out.println("\n🎉 Guest System Demo Complete!");
        System.out.println("The Guest component is working perfectly and ready for integration!");
        
        // Show what's possible for future expansion
        System.out.println("\n🚀 Ready for Future Expansion:");
        System.out.println("   • Satisfaction-based spawning rates");
        System.out.println("   • Dynamic pricing based on guest willingness to pay");
        System.out.println("   • Weather effects on guest behavior");
        System.out.println("   • Seasonal crowd variations");
        System.out.println("   • Guest spending and revenue tracking");
        System.out.println("   • Advanced AI behaviors and preferences");
    }
    
    /**
     * Build a sample resort with slopes and lifts for guests to use
     */
    private static void buildResortInfrastructure(Mountain mountain) {
        // Build beginner area
        Position beginnerStart = new Position(5, 3);
        Position beginnerEnd = new Position(5, 17);
        Slope beginnerSlope = new Slope("Learning Hill", beginnerStart, beginnerEnd, 
            Slope.Difficulty.BEGINNER, 5000);
        mountain.addSlope(beginnerSlope);
        
        Position beginnerLiftBottom = new Position(5, 17);
        Position beginnerLiftTop = new Position(5, 8);
        Lift beginnerLift = new Lift("Magic Carpet", beginnerLiftBottom, beginnerLiftTop, 
            Lift.LiftType.MAGIC_CARPET);
        mountain.addLift(beginnerLift);
        
        // Build intermediate area
        Position intStart = new Position(10, 4);
        Position intEnd = new Position(10, 16);
        Slope intSlope = new Slope("Blue Ridge", intStart, intEnd, 
            Slope.Difficulty.INTERMEDIATE, 10000);
        mountain.addSlope(intSlope);
        
        Position intLiftBottom = new Position(10, 16);
        Position intLiftTop = new Position(10, 6);
        Lift intLift = new Lift("Express Chair", intLiftBottom, intLiftTop, 
            Lift.LiftType.CHAIRLIFT);
        mountain.addLift(intLift);
        
        // Build advanced area
        Position advStart = new Position(15, 3);
        Position advEnd = new Position(15, 18);
        Slope advSlope = new Slope("Black Diamond", advStart, advEnd, 
            Slope.Difficulty.ADVANCED, 18000);
        mountain.addSlope(advSlope);
        
        Position advLiftBottom = new Position(15, 18);
        Position advLiftTop = new Position(15, 4);
        Lift advLift = new Lift("Summit Gondola", advLiftBottom, advLiftTop, 
            Lift.LiftType.GONDOLA);
        mountain.addLift(advLift);
    }
} 