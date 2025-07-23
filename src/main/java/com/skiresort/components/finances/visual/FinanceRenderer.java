package com.skiresort.components.finances.visual;

import com.skiresort.components.finances.core.*;
import java.util.Map;
import java.util.List;

/**
 * Visual renderer for financial information and reports
 * Provides comprehensive financial analysis and warnings
 */
public class FinanceRenderer {
    
    /**
     * Render main financial dashboard
     */
    public static String renderFinancialDashboard(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Financial Dashboard ===\n");
        
        // Current financial state
        double currentMoney = financeManager.getCurrentMoney();
        double netWorth = financeManager.getNetWorth();
        double totalDebt = financeManager.getTotalDebt();
        
        sb.append("💰 Current Money: $").append(String.format("%.2f", currentMoney)).append("\n");
        sb.append("📊 Net Worth: $").append(String.format("%.2f", netWorth)).append("\n");
        sb.append("💳 Total Debt: $").append(String.format("%.2f", totalDebt)).append("\n");
        sb.append("⭐ Credit Rating: ").append(String.format("%.0f", financeManager.getCreditRating())).append("/100\n");
        
        // Daily performance
        double dailyRevenue = financeManager.getDailyRevenue();
        double dailyExpenses = financeManager.getDailyExpenses();
        double dailyNet = dailyRevenue - dailyExpenses;
        
        sb.append("\n📈 Today's Performance:\n");
        sb.append("  Revenue: $").append(String.format("%.2f", dailyRevenue)).append("\n");
        sb.append("  Expenses: $").append(String.format("%.2f", dailyExpenses)).append("\n");
        sb.append("  Net: ");
        
        if (dailyNet >= 0) {
            sb.append("✅ +$").append(String.format("%.2f", dailyNet));
        } else {
            sb.append("❌ -$").append(String.format("%.2f", Math.abs(dailyNet)));
        }
        sb.append("\n");
        
        // Financial health warnings
        sb.append(renderFinancialWarnings(financeManager));
        
        return sb.toString();
    }
    
    /**
     * Render financial health warnings and alerts
     */
    public static String renderFinancialWarnings(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n🚨 Financial Health Check:\n");
        
        double currentMoney = financeManager.getCurrentMoney();
        double totalDebt = financeManager.getTotalDebt();
        double creditRating = financeManager.getCreditRating();
        
        // Bankruptcy risk
        if (financeManager.isBankrupt()) {
            sb.append("💀 BANKRUPT! Game Over!\n");
        } else if (currentMoney < 1000) {
            sb.append("🚨 CRITICAL: Very low funds! Risk of bankruptcy!\n");
        } else if (currentMoney < 5000) {
            sb.append("⚠️  WARNING: Low funds. Be careful with expenses.\n");
        } else if (currentMoney > 50000) {
            sb.append("💰 EXCELLENT: Strong financial position.\n");
        } else {
            sb.append("✅ STABLE: Adequate financial reserves.\n");
        }
        
        // Debt warnings
        if (totalDebt > 0) {
            double debtRatio = currentMoney > 0 ? totalDebt / currentMoney : Double.MAX_VALUE;
            if (debtRatio > 5.0) {
                sb.append("🚨 CRITICAL: Debt is extremely high relative to cash!\n");
            } else if (debtRatio > 2.0) {
                sb.append("⚠️  WARNING: High debt burden.\n");
            } else {
                sb.append("📊 INFO: Manageable debt levels.\n");
            }
        } else {
            sb.append("✅ DEBT-FREE: No outstanding loans.\n");
        }
        
        // Credit rating warnings
        if (creditRating < 30) {
            sb.append("🚨 CRITICAL: Very poor credit rating! Loans may be unavailable.\n");
        } else if (creditRating < 50) {
            sb.append("⚠️  WARNING: Poor credit rating will increase loan costs.\n");
        } else if (creditRating > 90) {
            sb.append("⭐ EXCELLENT: Outstanding credit rating!\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Render revenue breakdown report
     */
    public static String renderRevenueReport(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Revenue Report ===\n");
        sb.append("Total Revenue: $").append(String.format("%.2f", financeManager.getTotalRevenue())).append("\n");
        sb.append("Today's Revenue: $").append(String.format("%.2f", financeManager.getDailyRevenue())).append("\n");
        
        sb.append("\nRevenue by Source:\n");
        Map<RevenueSource, Double> revenueBySource = financeManager.getRevenueBySource();
        
        if (revenueBySource.isEmpty()) {
            sb.append("  No revenue recorded yet.\n");
        } else {
            revenueBySource.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // Sort by amount descending
                .forEach(entry -> {
                    sb.append("  ").append(entry.getKey().getDisplayName())
                      .append(": $").append(String.format("%.2f", entry.getValue()))
                      .append("\n");
                });
        }
        
        return sb.toString();
    }
    
    /**
     * Render expense breakdown report
     */
    public static String renderExpenseReport(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Expense Report ===\n");
        sb.append("Total Expenses: $").append(String.format("%.2f", financeManager.getTotalExpenses())).append("\n");
        sb.append("Today's Expenses: $").append(String.format("%.2f", financeManager.getDailyExpenses())).append("\n");
        
        sb.append("\nExpenses by Category:\n");
        Map<ExpenseCategory, Double> expensesByCategory = financeManager.getExpensesByCategory();
        
        if (expensesByCategory.isEmpty()) {
            sb.append("  No expenses recorded yet.\n");
        } else {
            expensesByCategory.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // Sort by amount descending
                .forEach(entry -> {
                    sb.append("  ").append(entry.getKey().getDisplayName())
                      .append(": $").append(String.format("%.2f", entry.getValue()))
                      .append("\n");
                });
        }
        
        return sb.toString();
    }
    
    /**
     * Render loan information
     */
    public static String renderLoanReport(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Loan Report ===\n");
        sb.append("Total Debt: $").append(String.format("%.2f", financeManager.getTotalDebt())).append("\n");
        sb.append("Credit Rating: ").append(String.format("%.0f", financeManager.getCreditRating())).append("/100\n");
        
        List<Loan> activeLoans = financeManager.getActiveLoans();
        
        if (activeLoans.isEmpty()) {
            sb.append("\n✅ No active loans - debt free!\n");
        } else {
            sb.append("\nActive Loans (").append(activeLoans.size()).append("):\n");
            
            for (Loan loan : activeLoans) {
                sb.append("  📋 ").append(loan.getPurpose()).append("\n");
                sb.append("    Balance: $").append(String.format("%.2f", loan.getRemainingBalance())).append("\n");
                sb.append("    Rate: ").append(String.format("%.1f%%", loan.getAnnualInterestRate() * 100)).append(" annually\n");
                sb.append("    Daily Payment: $").append(String.format("%.2f", loan.getDailyPayment())).append("\n");
                sb.append("    Status: ").append(loan.getStatusSummary()).append("\n");
                sb.append("    Interest Paid: $").append(String.format("%.2f", loan.getTotalInterestPaid())).append("\n\n");
            }
            
            // Total daily loan payments
            double totalDailyPayments = activeLoans.stream()
                .mapToDouble(Loan::getDailyPayment)
                .sum();
            sb.append("💳 Total Daily Loan Payments: $").append(String.format("%.2f", totalDailyPayments)).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Render recent transaction history
     */
    public static String renderTransactionHistory(FinanceManager financeManager, int transactionCount) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Recent Transactions ===\n");
        
        List<Transaction> recentTransactions = financeManager.getRecentTransactions(transactionCount);
        
        if (recentTransactions.isEmpty()) {
            sb.append("No transactions recorded.\n");
        } else {
            sb.append("Last ").append(Math.min(transactionCount, recentTransactions.size())).append(" transactions:\n\n");
            
            for (Transaction transaction : recentTransactions) {
                String prefix = transaction.isRevenue() ? "💰" : "💸";
                sb.append(prefix).append(" ").append(transaction.toString()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Render comprehensive financial summary
     */
    public static String renderFinancialSummary(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Complete Financial Summary ===\n\n");
        
        // Dashboard
        sb.append(renderFinancialDashboard(financeManager));
        sb.append("\n");
        
        // Revenue and expenses
        sb.append(renderRevenueReport(financeManager));
        sb.append("\n");
        sb.append(renderExpenseReport(financeManager));
        sb.append("\n");
        
        // Loans
        sb.append(renderLoanReport(financeManager));
        sb.append("\n");
        
        // Recent activity
        sb.append(renderTransactionHistory(financeManager, 10));
        
        return sb.toString();
    }
    
    /**
     * Render pricing information
     */
    public static String renderPricingInfo(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Pricing Information ===\n");
        sb.append("Current Lift Ticket Price: $").append(String.format("%.2f", financeManager.getCurrentLiftTicketPriceDisplay())).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Render investment recommendations based on current financial state
     */
    public static String renderInvestmentRecommendations(FinanceManager financeManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Investment Recommendations ===\n");
        
        double currentMoney = financeManager.getCurrentMoney();
        double creditRating = financeManager.getCreditRating();
        
        if (financeManager.isBankrupt()) {
            sb.append("💀 Focus on survival! No investments recommended.\n");
        } else if (currentMoney < 10000) {
            sb.append("🚨 Build cash reserves before investing.\n");
            sb.append("   Consider taking a loan if credit allows.\n");
        } else if (currentMoney < 25000) {
            sb.append("⚠️  Limited investment capacity.\n");
            sb.append("   Focus on high-ROI improvements like basic slopes.\n");
        } else if (currentMoney < 50000) {
            sb.append("📊 Moderate investment capacity.\n");
            sb.append("   Consider additional lifts or intermediate slopes.\n");
        } else {
            sb.append("💰 Strong investment position!\n");
            sb.append("   Ready for major expansions like gondolas or expert terrain.\n");
        }
        
        // Loan recommendations
        if (creditRating >= 70 && financeManager.getTotalDebt() < currentMoney * 2) {
            sb.append("💳 Good credit - loans available for expansion.\n");
        } else if (creditRating < 50) {
            sb.append("⚠️  Poor credit - improve finances before borrowing.\n");
        }
        
        return sb.toString();
    }
} 