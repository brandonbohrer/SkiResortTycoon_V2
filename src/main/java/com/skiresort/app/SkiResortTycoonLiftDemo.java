package com.skiresort.app;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.mountain.core.Lift.LiftType;
import com.skiresort.components.mountain.visual.MountainRenderer;
import com.skiresort.components.finances.core.FinanceManager;
import com.skiresort.components.finances.core.ExpenseCategory;
import com.skiresort.shared.Position;

/**
 * Enhanced Lift System Demo showcasing new lift types, operational mechanics, and economic progression
 */
public class SkiResortTycoonLiftDemo {
    
    public static void main(String[] args) {
        System.out.println("🚠 Ski Resort Tycoon - Enhanced Lift System Demo! 🚠");
        System.out.println("=".repeat(70));
        
        // Create core components
        Mountain mountain = new Mountain("Alpine Excellence Resort", 25, 25);
        FinanceManager financeManager = new FinanceManager();
        
        System.out.println("\n💰 Starting with $20,000 budget");
        System.out.println("Let's explore the lift progression and economics!\n");
        
        // Showcase all lift types with detailed analysis
        System.out.println("🎯 === LIFT PROGRESSION ANALYSIS ===\n");
        
        System.out.println("📊 STARTER LIFTS (Available from Day 1):");
        analyzeLift(LiftType.ROPE_TOW);
        analyzeLift(LiftType.MAGIC_CARPET);
        analyzeLift(LiftType.SMALL_DIESEL_LIFT);
        
        System.out.println("\n📊 INTERMEDIATE LIFTS (Future: Unlock at 50+ daily guests):");
        analyzeLift(LiftType.CHAIRLIFT);
        
        System.out.println("\n📊 ADVANCED LIFTS (Future: Unlock at 100+ daily guests):");
        analyzeLift(LiftType.HIGH_SPEED_CHAIRLIFT);
        
        System.out.println("\n📊 PREMIUM LIFTS (Future: Unlock at 200+ daily guests):");
        analyzeLift(LiftType.GONDOLA);
        
        // Build a practical lift progression
        System.out.println("\n🏗️  === BUILDING OPTIMAL LIFT PROGRESSION ===\n");
        
        // Stage 1: Start with Magic Carpet (safe choice)
        Position pos1Bottom = new Position(12, 20);
        Position pos1Top = new Position(12, 10);
        Lift magicCarpet = new Lift("Beginner Express", pos1Bottom, pos1Top, LiftType.MAGIC_CARPET);
        
        if (financeManager.makePurchase(magicCarpet.getBuildCost(), 
            "Built " + magicCarpet.getName(), ExpenseCategory.EQUIPMENT_PURCHASE)) {
            mountain.addLift(magicCarpet);
            System.out.println("✅ Stage 1: Built " + magicCarpet.getName() + 
                " ($" + String.format("%.0f", magicCarpet.getBuildCost()) + ")");
            System.out.println("   💡 Safe beginner lift with good capacity");
            System.out.println("   📈 Throughput: " + String.format("%.0f", magicCarpet.getGuestsPerHour()) + " guests/hour");
        }
        
        // Stage 2: Add High-Speed Chairlift if we can afford it
        Position pos2Bottom = new Position(8, 22);
        Position pos2Top = new Position(8, 5);
        Lift highSpeedChair = new Lift("Mountain Express", pos2Bottom, pos2Top, LiftType.HIGH_SPEED_CHAIRLIFT);
        
        System.out.println("\n💭 Considering High-Speed Chairlift upgrade...");
        System.out.println("   💰 Cost: $" + String.format("%.0f", highSpeedChair.getBuildCost()));
        System.out.println("   💵 Remaining budget: $" + String.format("%.0f", financeManager.getCurrentMoney()));
        
        if (financeManager.canAfford(highSpeedChair.getBuildCost())) {
            System.out.println("   ✅ Can afford it! But should we?");
            System.out.println("   🤔 Let's compare the economics...\n");
            
            compareLifts(magicCarpet, highSpeedChair);
            
            // Take a loan to demonstrate the investment decision
            if (financeManager.takeLoan(60000, "High-speed lift investment")) {
                System.out.println("\n💳 Took investment loan of $60,000");
                
                if (financeManager.makePurchase(highSpeedChair.getBuildCost(), 
                    "Built " + highSpeedChair.getName(), ExpenseCategory.EQUIPMENT_PURCHASE)) {
                    mountain.addLift(highSpeedChair);
                    System.out.println("✅ Stage 2: Built " + highSpeedChair.getName() + 
                        " ($" + String.format("%.0f", highSpeedChair.getBuildCost()) + ")");
                    System.out.println("   🚀 Premium high-capacity lift for serious skiers");
                    System.out.println("   📈 Throughput: " + String.format("%.0f", highSpeedChair.getGuestsPerHour()) + " guests/hour");
                }
            }
        } else {
            System.out.println("   ❌ Cannot afford yet - need to build revenue first!");
        }
        
        // Demonstrate operational mechanics
        System.out.println("\n⚙️  === OPERATIONAL MECHANICS DEMO ===\n");
        
        // Simulate guest queuing and processing
        System.out.println("🎿 Simulating peak hour operations...");
        
        for (Lift lift : mountain.getLifts()) {
            System.out.println("\n🚠 " + lift.getName() + " (" + lift.getType().getDisplayName() + "):");
            
            // Add some waiting guests
            lift.addWaitingGuests(15);
            System.out.println("   👥 " + lift.getWaitingGuests() + " guests join the queue");
            
            // Process the queue
            int loaded = lift.processGuestQueue();
            System.out.println("   🚶 " + loaded + " guests loaded onto lift");
            System.out.println("   ⏱️  Wait time for next guests: " + 
                String.format("%.1f", lift.getWaitTimeMinutes()) + " minutes");
            System.out.println("   📊 Current efficiency: " + 
                String.format("%.0f%%", lift.getEfficiency() * 100));
            
            // Show maintenance status
            if (lift.needsMaintenance()) {
                System.out.println("   🔧 ⚠️  NEEDS MAINTENANCE");
            } else {
                System.out.println("   ✅ Good mechanical condition");
            }
        }
        
        // Show the resort layout
        System.out.println("\n🏔️  === RESORT LAYOUT ===\n");
        System.out.println(MountainRenderer.renderMountainView(mountain));
        
        // Economic summary
        System.out.println("\n💰 === FINANCIAL IMPACT ===");
        System.out.println("💸 Total Investment: $" + String.format("%.0f", financeManager.getTotalExpenses()));
        System.out.println("💳 Current Debt: $" + String.format("%.0f", financeManager.getTotalDebt()));
        System.out.println("💰 Remaining Cash: $" + String.format("%.0f", financeManager.getCurrentMoney()));
        System.out.println("📊 Net Worth: $" + String.format("%.0f", financeManager.getNetWorth()));
        
        // Strategic recommendations
        System.out.println("\n🎯 === STRATEGIC INSIGHTS ===");
        
        double totalThroughput = mountain.getLifts().stream()
            .mapToDouble(Lift::getGuestsPerHour)
            .sum();
        
        System.out.println("📈 Total Resort Throughput: " + String.format("%.0f", totalThroughput) + " guests/hour");
        
        if (totalThroughput > 500) {
            System.out.println("🚀 EXCELLENT: High-capacity resort ready for major guest volumes!");
        } else if (totalThroughput > 200) {
            System.out.println("✅ GOOD: Solid capacity for growing resort");
        } else {
            System.out.println("⚠️  LIMITED: Need more lift capacity for expansion");
        }
        
        System.out.println("\n💡 Next Steps:");
        if (financeManager.getCurrentMoney() > 50000) {
            System.out.println("   🎯 Ready for gondola system ($80K)");
        } else if (financeManager.getCurrentMoney() > 20000) {
            System.out.println("   🎯 Focus on guest satisfaction and revenue");
        } else {
            System.out.println("   🎯 Build guest base with current lifts");
        }
        
        System.out.println("\n🚠 Enhanced Lift System Demo Complete!");
        System.out.println("Strategic lift investments drive resort success! 🎿");
    }
    
    /**
     * Analyze a lift type's characteristics and economics
     */
    private static void analyzeLift(LiftType liftType) {
        System.out.println("🚠 " + liftType.getDisplayName() + " (" + liftType.getTier().getDisplayName() + ")");
        System.out.println("   💰 Build Cost: $" + String.format("%.0f", liftType.getBuildCost()));
        System.out.println("   👥 Capacity: " + liftType.getCapacity() + " guests per trip");
        System.out.println("   ⏱️  Ride Time: " + String.format("%.1f", liftType.getRideTimeSeconds() / 60.0) + " minutes");
        System.out.println("   📈 Efficiency: " + String.format("%.1f", liftType.getEfficiencyScore()) + " guests/minute");
        System.out.println("   💵 Cost per guest/min: $" + 
            String.format("%.0f", liftType.getBuildCost() / liftType.getEfficiencyScore()));
        
        if (liftType.isHighSpeed()) {
            System.out.println("   ⚡ HIGH-SPEED: Premium guest experience");
        }
        
        System.out.println();
    }
    
    /**
     * Compare two lifts economically
     */
    private static void compareLifts(Lift lift1, Lift lift2) {
        System.out.println("⚖️  LIFT COMPARISON:");
        System.out.println("┌─────────────────────┬─────────────────┬─────────────────┐");
        System.out.println("│ Metric              │ " + String.format("%-15s", lift1.getName()) + " │ " + String.format("%-15s", lift2.getName()) + " │");
        System.out.println("├─────────────────────┼─────────────────┼─────────────────┤");
        System.out.println("│ Build Cost          │ $" + String.format("%,-14.0f", lift1.getBuildCost()) + " │ $" + String.format("%,-14.0f", lift2.getBuildCost()) + " │");
        System.out.println("│ Guests/Hour         │ " + String.format("%,15.0f", lift1.getGuestsPerHour()) + " │ " + String.format("%,15.0f", lift2.getGuestsPerHour()) + " │");
        System.out.println("│ Cost per Guest/Hr   │ $" + String.format("%,14.0f", lift1.getBuildCost() / lift1.getGuestsPerHour()) + " │ $" + String.format("%,14.0f", lift2.getBuildCost() / lift2.getGuestsPerHour()) + " │");
        System.out.println("│ Efficiency          │ " + String.format("%14.0f%%", lift1.getEfficiency() * 100) + " │ " + String.format("%14.0f%%", lift2.getEfficiency() * 100) + " │");
        System.out.println("└─────────────────────┴─────────────────┴─────────────────┘");
        
        double costEfficiencyRatio = (lift2.getBuildCost() / lift2.getGuestsPerHour()) / 
                                   (lift1.getBuildCost() / lift1.getGuestsPerHour());
        
        if (costEfficiencyRatio < 1.0) {
            System.out.println("💡 " + lift2.getName() + " is " + String.format("%.1fx", 1/costEfficiencyRatio) + " more cost-efficient!");
        } else {
            System.out.println("💡 " + lift1.getName() + " is " + String.format("%.1fx", costEfficiencyRatio) + " more cost-efficient!");
        }
    }
} 