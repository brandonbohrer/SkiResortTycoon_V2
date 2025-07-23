package com.skiresort.app;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.mountain.visual.MountainRenderer;
import com.skiresort.components.guests.core.GuestManager;
import com.skiresort.components.guests.visual.GuestRenderer;
import com.skiresort.components.finances.core.FinanceManager;
import com.skiresort.components.finances.core.ExpenseCategory;
import com.skiresort.components.finances.visual.FinanceRenderer;
import com.skiresort.shared.Position;

/**
 * Finance Demo showcasing the challenging economic progression
 * Start poor, struggle with costs, and slowly build an empire!
 */
public class SkiResortTycoonFinanceDemo {
    
    public static void main(String[] args) {
        System.out.println("💰 Ski Resort Tycoon - Finance System Demo! 💰");
        System.out.println("⚠️  CHALLENGING MODE: You start with limited funds!");
        System.out.println("=".repeat(70));
        
        // Create core components
        Mountain mountain = new Mountain("Struggling Peaks Resort", 20, 20);
        GuestManager guestManager = new GuestManager();
        FinanceManager financeManager = new FinanceManager();
        
        // Configure for realistic but challenging gameplay
        guestManager.setMaxCapacity(15); // Small capacity initially
        guestManager.setBaseSpawnRate(3); // Low guest flow initially
        
        // Show initial dire financial situation
        System.out.println("\n📊 Starting Financial Position:");
        System.out.println(FinanceRenderer.renderFinancialDashboard(financeManager));
        System.out.println(FinanceRenderer.renderInvestmentRecommendations(financeManager));
        
        // The economic struggle begins - you can barely afford anything!
        System.out.println("\n💸 Welcome to Resort Management Reality!");
        System.out.println("You have $15,000 to build an entire ski resort.");
        System.out.println("A basic chairlift costs $25,000. A slope costs $5,000-$20,000.");
        System.out.println("You'll need a loan to get started... if you qualify!");
        
        // Attempt to build basic infrastructure - this will require loans!
        System.out.println("\n🏗️  Attempting to Build Basic Infrastructure...");
        
        // Try to build a basic slope first (cheapest option)
        Position beginnerStart = new Position(10, 5);
        Position beginnerEnd = new Position(10, 18);
        Slope beginnerSlope = new Slope("Bunny Hill", beginnerStart, beginnerEnd, 
            Slope.Difficulty.BEGINNER, 5000);
        
        if (financeManager.canAfford(beginnerSlope.getBuildCost())) {
            if (financeManager.makePurchase(beginnerSlope.getBuildCost(), 
                "Built " + beginnerSlope.getName(), ExpenseCategory.FACILITY_CONSTRUCTION)) {
                mountain.addSlope(beginnerSlope);
                System.out.println("✅ Built " + beginnerSlope.getName() + " for $" + 
                    String.format("%.0f", beginnerSlope.getBuildCost()));
            }
        } else {
            System.out.println("❌ Cannot afford " + beginnerSlope.getName() + 
                " ($" + String.format("%.0f", beginnerSlope.getBuildCost()) + ")");
        }
        
        // Try to build a lift - definitely need a loan for this
        Position liftBottom = new Position(10, 18);
        Position liftTop = new Position(10, 8);
        Lift beginnerLift = new Lift("Budget Express", liftBottom, liftTop, 
            Lift.LiftType.MAGIC_CARPET); // Cheapest option
        
        if (financeManager.canAfford(beginnerLift.getBuildCost())) {
            if (financeManager.makePurchase(beginnerLift.getBuildCost(), 
                "Built " + beginnerLift.getName(), ExpenseCategory.EQUIPMENT_PURCHASE)) {
                mountain.addLift(beginnerLift);
                System.out.println("✅ Built " + beginnerLift.getName() + " for $" + 
                    String.format("%.0f", beginnerLift.getBuildCost()));
            }
        } else {
            System.out.println("❌ Cannot afford " + beginnerLift.getName() + 
                " ($" + String.format("%.0f", beginnerLift.getBuildCost()) + ")");
            System.out.println("💳 Taking out a loan to survive...");
            
            // Take emergency loan
            if (financeManager.takeLoan(25000, "Emergency startup loan")) {
                System.out.println("✅ Loan approved! But at a high interest rate...");
                
                // Now try to build the lift
                if (financeManager.makePurchase(beginnerLift.getBuildCost(), 
                    "Built " + beginnerLift.getName(), ExpenseCategory.EQUIPMENT_PURCHASE)) {
                    mountain.addLift(beginnerLift);
                    System.out.println("✅ Built " + beginnerLift.getName() + " for $" + 
                        String.format("%.0f", beginnerLift.getBuildCost()));
                }
            } else {
                System.out.println("❌ Loan denied! Credit rating too poor or too much debt!");
            }
        }
        
        // Show post-construction financial state
        System.out.println("\n📊 Financial State After Initial Construction:");
        System.out.println(FinanceRenderer.renderFinancialDashboard(financeManager));
        System.out.println(FinanceRenderer.renderLoanReport(financeManager));
        
        // Show the bare-bones resort
        System.out.println("\n🏔️  Your Struggling Resort:");
        System.out.println(MountainRenderer.renderMountainView(mountain));
        
        // Simulate several days of operation to show the financial struggle
        System.out.println("\n📅 Simulating Resort Operations (Day by Day Survival)...");
        
        for (int day = 1; day <= 10; day++) {
            System.out.println("\n⏰ === Day " + day + " ===");
            
            // Update guest operations
            guestManager.update(mountain);
            
            // Update demand pricing based on guest satisfaction
            financeManager.updateDemandMultiplier(guestManager.getAverageSatisfaction());
            
            // Process daily finances (the moment of truth!)
            financeManager.processDailyFinances(mountain, guestManager);
            
            // Show daily results
            System.out.println("👥 Guests: " + guestManager.getActiveGuestCount() + 
                " (Satisfaction: " + String.format("%.1f", guestManager.getAverageSatisfaction()) + ")");
            System.out.println("💰 Money: $" + String.format("%.2f", financeManager.getCurrentMoney()));
            System.out.println("📈 Daily P&L: Revenue $" + String.format("%.2f", financeManager.getDailyRevenue()) + 
                " - Expenses $" + String.format("%.2f", financeManager.getDailyExpenses()) + 
                " = " + (financeManager.getDailyRevenue() - financeManager.getDailyExpenses() >= 0 ? "✅" : "❌") +
                " $" + String.format("%.2f", financeManager.getDailyRevenue() - financeManager.getDailyExpenses()));
            
            // Check financial health
            if (financeManager.isBankrupt()) {
                System.out.println("💀 BANKRUPTCY! Game Over on Day " + day + "!");
                break;
            } else if (financeManager.getCurrentMoney() < 1000) {
                System.out.println("🚨 CRITICAL: Very low funds! Bankruptcy imminent!");
            }
            
            // Show guest activity
            if (guestManager.getActiveGuestCount() > 0) {
                System.out.println("🎿 Resort Activity:");
                System.out.println(MountainRenderer.renderMountainView(mountain, guestManager.getActiveGuests()));
            }
            
            // Pause for dramatic effect
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Final financial report
        System.out.println("\n🎯 === Final Financial Results ===");
        System.out.println(FinanceRenderer.renderFinancialSummary(financeManager));
        
        // Economic analysis
        System.out.println("\n📊 Economic Analysis:");
        double totalProfit = financeManager.getTotalRevenue() - financeManager.getTotalExpenses();
        
        if (financeManager.isBankrupt()) {
            System.out.println("💀 FAILED: Resort went bankrupt!");
            System.out.println("   💡 Lessons: Start smaller, manage costs, ensure guest satisfaction");
        } else if (totalProfit > 0) {
            System.out.println("✅ SUCCESS: Profitable resort! Total profit: $" + 
                String.format("%.2f", totalProfit));
            System.out.println("   🎉 You've mastered the basics of resort economics!");
        } else {
            System.out.println("😰 SURVIVING: Still losing money but alive!");
            System.out.println("   💪 Keep optimizing - profitability is within reach!");
        }
        
        // Investment opportunities
        System.out.println(FinanceRenderer.renderInvestmentRecommendations(financeManager));
        
        // Show what could be next
        System.out.println("\n🚀 Next Steps for Resort Growth:");
        if (financeManager.getCurrentMoney() > 15000) {
            System.out.println("   💰 Add an intermediate slope ($12,000)");
            System.out.println("   🚠 Upgrade to a chairlift ($25,000)");
        } else if (financeManager.getCurrentMoney() > 5000) {
            System.out.println("   🛷 Focus on guest satisfaction to increase revenue");
            System.out.println("   📊 Optimize pricing and reduce costs");
        } else {
            System.out.println("   🚨 Focus on survival - cut costs and improve efficiency");
        }
        
        System.out.println("\n💰 Finance Demo Complete!");
        System.out.println("Experience the real challenge of ski resort economics!");
        System.out.println("Every dollar matters when you're starting from nothing!");
        
        // Show the economic progression possible
        System.out.println("\n🎯 Economic Progression Path:");
        System.out.println("Stage 1: Survival ($0-25K) - Basic slope + magic carpet");
        System.out.println("Stage 2: Growth ($25K-75K) - Add intermediate terrain + chairlift");
        System.out.println("Stage 3: Expansion ($75K+) - Expert slopes + gondola systems");
        System.out.println("Stage 4: Empire ($200K+) - Multiple mountains + luxury amenities");
        
        System.out.println("\nThe Finance component captures the real economics of resort management!");
    }
} 