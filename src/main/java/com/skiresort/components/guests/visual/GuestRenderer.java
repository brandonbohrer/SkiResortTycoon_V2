package com.skiresort.components.guests.visual;

import com.skiresort.components.guests.core.Guest;
import com.skiresort.components.guests.core.GuestManager;
import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.shared.Position;
import java.util.Collection;
import java.util.Map;

/**
 * Visual renderer for guests in the ski resort
 * Handles both individual guest visualization and aggregate statistics
 */
public class GuestRenderer {
    
    private static final char GUEST_BEGINNER = 'B';
    private static final char GUEST_INTERMEDIATE = 'I'; 
    private static final char GUEST_ADVANCED = 'A';
    private static final char GUEST_EXPERT = 'E';
    private static final char GUEST_MULTIPLE = '@'; // When multiple guests at same position
    
    /**
     * Add guests to the mountain view grid
     */
    public static void addGuestsToGrid(char[][] grid, Collection<Guest> guests) {
        // First pass: mark all guest positions
        for (Guest guest : guests) {
            Position pos = guest.getCurrentPosition();
            if (pos != null && 
                pos.getX() >= 0 && pos.getX() < grid[0].length &&
                pos.getY() >= 0 && pos.getY() < grid.length) {
                
                char guestChar = getGuestCharacter(guest);
                char currentChar = grid[pos.getY()][pos.getX()];
                
                // Handle overlapping guests
                if (isGuestCharacter(currentChar)) {
                    grid[pos.getY()][pos.getX()] = GUEST_MULTIPLE;
                } else {
                    grid[pos.getY()][pos.getX()] = guestChar;
                }
            }
        }
    }
    
    /**
     * Get character representation for a guest based on skill level
     */
    private static char getGuestCharacter(Guest guest) {
        switch (guest.getSkillLevel()) {
            case BEGINNER: return GUEST_BEGINNER;
            case INTERMEDIATE: return GUEST_INTERMEDIATE;
            case ADVANCED: return GUEST_ADVANCED;
            case EXPERT: return GUEST_EXPERT;
            default: return '?';
        }
    }
    
    /**
     * Check if a character represents a guest
     */
    private static boolean isGuestCharacter(char c) {
        return c == GUEST_BEGINNER || c == GUEST_INTERMEDIATE || 
               c == GUEST_ADVANCED || c == GUEST_EXPERT || c == GUEST_MULTIPLE;
    }
    
    /**
     * Render detailed guest statistics
     */
    public static String renderGuestStatistics(GuestManager guestManager) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Guest Statistics ===\n");
        sb.append("Active Guests: ").append(guestManager.getActiveGuestCount())
          .append("/").append(guestManager.getMaxCapacity()).append("\n");
        sb.append("Total Spawned: ").append(guestManager.getTotalGuestsSpawned()).append("\n");
        sb.append("Total Completed: ").append(guestManager.getTotalGuestsCompleted()).append("\n");
        sb.append("Average Satisfaction: ").append(String.format("%.1f", guestManager.getAverageSatisfaction())).append("/100\n");
        
        // Breakdown by skill level
        Map<Guest.SkillLevel, Long> bySkill = guestManager.getGuestsBySkillLevel();
        sb.append("\nGuests by Skill Level:\n");
        for (Guest.SkillLevel skill : Guest.SkillLevel.values()) {
            long count = bySkill.getOrDefault(skill, 0L);
            sb.append("  ").append(skill.name()).append(": ").append(count).append("\n");
        }
        
        // Breakdown by current state
        Map<Guest.GuestState, Long> byState = guestManager.getGuestsByState();
        sb.append("\nGuests by Activity:\n");
        for (Guest.GuestState state : Guest.GuestState.values()) {
            long count = byState.getOrDefault(state, 0L);
            if (count > 0) {
                sb.append("  ").append(formatStateName(state)).append(": ").append(count).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Format guest state names for display
     */
    private static String formatStateName(Guest.GuestState state) {
        switch (state) {
            case ENTERING_RESORT: return "Entering Resort";
            case WAITING_FOR_LIFT: return "Waiting for Lift";
            case RIDING_LIFT: return "Riding Lift";
            case SKIING_SLOPE: return "Skiing";
            case RESTING: return "Resting";
            case LEAVING_RESORT: return "Leaving Resort";
            default: return state.name();
        }
    }
    
    /**
     * Render individual guest details
     */
    public static String renderIndividualGuests(Collection<Guest> guests) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Individual Guest Details ===\n");
        
        if (guests.isEmpty()) {
            sb.append("No guests currently in the resort.\n");
            return sb.toString();
        }
        
        // Group by state for better organization
        Map<Guest.GuestState, Long> stateGroups = guests.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Guest::getState,
                java.util.stream.Collectors.counting()
            ));
        
        for (Guest.GuestState state : Guest.GuestState.values()) {
            long countInState = stateGroups.getOrDefault(state, 0L);
            if (countInState == 0) continue;
            
            sb.append("\n").append(formatStateName(state)).append(" (").append(countInState).append("):\n");
            
            guests.stream()
                .filter(guest -> guest.getState() == state)
                .limit(5) // Show max 5 guests per state to avoid clutter
                .forEach(guest -> {
                    sb.append("  - ").append(guest.getName())
                      .append(" (").append(guest.getSkillLevel()).append(")")
                      .append(" - Satisfaction: ").append(guest.getSatisfaction()).append("/100")
                      .append(" - Runs: ").append(guest.getTotalRunsCompleted())
                      .append("\n");
                });
            
            if (countInState > 5) {
                sb.append("  ... and ").append(countInState - 5).append(" more\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Render guest satisfaction analysis
     */
    public static String renderSatisfactionAnalysis(Collection<Guest> guests) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Guest Satisfaction Analysis ===\n");
        
        if (guests.isEmpty()) {
            sb.append("No guests to analyze.\n");
            return sb.toString();
        }
        
        // Calculate satisfaction distribution
        int[] satisfactionBuckets = new int[5]; // 0-20, 21-40, 41-60, 61-80, 81-100
        double totalSatisfaction = 0;
        int veryHappy = 0;
        int veryUnhappy = 0;
        
        for (Guest guest : guests) {
            int satisfaction = guest.getSatisfaction();
            totalSatisfaction += satisfaction;
            
            if (satisfaction >= 80) veryHappy++;
            if (satisfaction <= 20) veryUnhappy++;
            
            int bucket = Math.min(4, satisfaction / 20);
            satisfactionBuckets[bucket]++;
        }
        
        double averageSatisfaction = totalSatisfaction / guests.size();
        
        sb.append("Average Satisfaction: ").append(String.format("%.1f", averageSatisfaction)).append("/100\n");
        sb.append("Very Happy Guests (80+): ").append(veryHappy).append("\n");
        sb.append("Very Unhappy Guests (20-): ").append(veryUnhappy).append("\n");
        
        sb.append("\nSatisfaction Distribution:\n");
        String[] bucketLabels = {"0-20", "21-40", "41-60", "61-80", "81-100"};
        for (int i = 0; i < satisfactionBuckets.length; i++) {
            sb.append("  ").append(bucketLabels[i]).append(": ")
              .append(satisfactionBuckets[i]).append(" guests\n");
        }
        
        // Satisfaction trends (basic analysis)
        if (averageSatisfaction >= 80) {
            sb.append("\n✅ Excellent guest satisfaction! Resort is performing very well.\n");
        } else if (averageSatisfaction >= 60) {
            sb.append("\n😊 Good guest satisfaction. Room for improvement.\n");
        } else if (averageSatisfaction >= 40) {
            sb.append("\n😐 Moderate satisfaction. Consider improvements to facilities.\n");
        } else {
            sb.append("\n😞 Low satisfaction. Urgent attention needed!\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Render legend for guest symbols
     */
    public static String renderGuestLegend() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Guest Legend ===\n");
        sb.append("B = Beginner Skier\n");
        sb.append("I = Intermediate Skier\n");
        sb.append("A = Advanced Skier\n");
        sb.append("E = Expert Skier\n");
        sb.append("@ = Multiple Guests\n");
        
        return sb.toString();
    }
} 