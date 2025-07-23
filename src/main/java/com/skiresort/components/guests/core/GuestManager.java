package com.skiresort.components.guests.core;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.shared.Position;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages guest lifecycle including spawning, movement, and departure
 * Designed with extensibility for future features:
 * - Park capacity-based spawning rates
 * - Satisfaction-based arrival patterns  
 * - Dynamic pricing response
 * - Seasonal/weather variations
 */
public class GuestManager {
    
    // Current guest tracking
    private final Map<String, Guest> activeGuests;
    private final List<Guest> guestsToRemove;
    
    // Spawning configuration (extensible for future features)
    private int baseSpawnRate;              // Base guests per minute
    private int maxCapacity;                // Maximum guests in resort
    private double satisfactionMultiplier;  // Future: affects spawn rate
    private double seasonalMultiplier;      // Future: seasonal variations
    private double weatherMultiplier;       // Future: weather effects
    
    // Statistics tracking (for future analytics)
    private int totalGuestsSpawned;
    private int totalGuestsCompleted;
    private double averageStayDuration;
    private double averageSatisfaction;
    
    // Name generation for guests
    private final List<String> guestNames;
    private final Random random;
    
    public GuestManager() {
        this.activeGuests = new HashMap<>();
        this.guestsToRemove = new ArrayList<>();
        
        // Initialize reasonable defaults (extensible later)
        this.baseSpawnRate = 5;            // 5 guests per minute base rate
        this.maxCapacity = 100;            // Max 100 guests for now
        this.satisfactionMultiplier = 1.0; // Neutral satisfaction effect
        this.seasonalMultiplier = 1.0;     // No seasonal variation yet
        this.weatherMultiplier = 1.0;      // No weather effects yet
        
        this.totalGuestsSpawned = 0;
        this.totalGuestsCompleted = 0;
        this.averageStayDuration = 0.0;
        this.averageSatisfaction = 0.0;
        
        this.random = new Random();
        
        // Initialize guest names for variety
        this.guestNames = Arrays.asList(
            "Alex", "Sam", "Jordan", "Casey", "Riley", "Morgan", "Taylor", "Avery",
            "Quinn", "Blake", "Cameron", "Drew", "Emery", "Finley", "Hayden", "Jamie",
            "Kendall", "Logan", "Mason", "Parker", "Reese", "Sage", "Skyler", "Robin"
        );
    }
    
    /**
     * Update guest manager - spawn new guests and update existing ones
     * Called each game tick/update cycle
     */
    public void update(Mountain mountain) {
        // Update existing guests
        updateExistingGuests(mountain);
        
        // Remove guests who are leaving
        removeCompletedGuests();
        
        // Spawn new guests based on current conditions
        spawnNewGuests(mountain);
        
        // Update statistics
        updateStatistics();
    }
    
    /**
     * Update all existing guests' behavior and state
     */
    private void updateExistingGuests(Mountain mountain) {
        for (Guest guest : activeGuests.values()) {
            updateGuestBehavior(guest, mountain);
        }
    }
    
    /**
     * Update individual guest behavior based on their current state
     */
    private void updateGuestBehavior(Guest guest, Mountain mountain) {
        GuestActivity nextActivity = guest.chooseNextActivity();
        
        switch (nextActivity.getType()) {
            case FIND_LIFT:
                assignGuestToLift(guest, mountain);
                break;
                
            case FIND_SLOPE:
                assignGuestToSlope(guest, mountain);
                break;
                
            case LEAVE_RESORT:
                markGuestForRemoval(guest);
                break;
                
            case WAIT:
                // Guest is waiting, check if their satisfaction should decrease
                guest.updateSatisfaction(-1, "Waiting around");
                break;
        }
    }
    
    /**
     * Find and assign a suitable lift for the guest
     */
    private void assignGuestToLift(Guest guest, Mountain mountain) {
        List<Lift> availableLifts = mountain.getLifts().stream()
            .filter(lift -> guest.isWillingToUseLift(lift))
            .collect(Collectors.toList());
        
        if (!availableLifts.isEmpty()) {
            // Choose randomly for now (future: smart selection based on preferences)
            Lift chosenLift = availableLifts.get(random.nextInt(availableLifts.size()));
            
            if (chosenLift.addGuests(1)) {
                guest.setCurrentLift(chosenLift);
                guest.setCurrentPosition(chosenLift.getBottomPosition());
                guest.setState(Guest.GuestState.RIDING_LIFT);
                guest.updateSatisfaction(2, "Found available lift");
            } else {
                guest.setState(Guest.GuestState.WAITING_FOR_LIFT);
                guest.updateSatisfaction(-2, "Lift queue too long");
            }
        } else {
            guest.setState(Guest.GuestState.WAITING_FOR_LIFT);
            guest.updateSatisfaction(-3, "No suitable lifts available");
        }
    }
    
    /**
     * Find and assign a suitable slope for the guest
     */
    private void assignGuestToSlope(Guest guest, Mountain mountain) {
        List<Slope> availableSlopes = mountain.getSlopes().stream()
            .filter(slope -> guest.isWillingToUseSlope(slope))
            .collect(Collectors.toList());
        
        if (!availableSlopes.isEmpty()) {
            // Choose randomly for now (future: preference-based selection)
            Slope chosenSlope = availableSlopes.get(random.nextInt(availableSlopes.size()));
            
            guest.setCurrentSlope(chosenSlope);
            guest.setCurrentPosition(chosenSlope.getStartPosition());
            guest.setState(Guest.GuestState.SKIING_SLOPE);
            guest.updateSatisfaction(3, "Found suitable slope");
            
            // Remove from lift if they were on one
            if (guest.getCurrentLift() != null) {
                guest.getCurrentLift().removeGuests(1);
                guest.setCurrentLift(null);
            }
        } else {
            guest.updateSatisfaction(-4, "No suitable slopes available");
            // Guest might leave if no slopes match their skill level
            if (guest.getSatisfaction() < 30) {
                markGuestForRemoval(guest);
            }
        }
    }
    
    /**
     * Mark guest for removal and clean up their state
     */
    private void markGuestForRemoval(Guest guest) {
        guest.setState(Guest.GuestState.LEAVING_RESORT);
        guestsToRemove.add(guest);
        
        // Clean up lift capacity if guest was on a lift
        if (guest.getCurrentLift() != null) {
            guest.getCurrentLift().removeGuests(1);
        }
    }
    
    /**
     * Remove guests who have left the resort
     */
    private void removeCompletedGuests() {
        for (Guest guest : guestsToRemove) {
            activeGuests.remove(guest.getId());
            totalGuestsCompleted++;
        }
        guestsToRemove.clear();
    }
    
    /**
     * Spawn new guests based on current conditions
     * Extensible for capacity, satisfaction, and seasonal effects
     */
    private void spawnNewGuests(Mountain mountain) {
        // Check capacity limit
        if (activeGuests.size() >= maxCapacity) {
            return;
        }
        
        // Calculate current spawn rate
        double currentSpawnRate = calculateCurrentSpawnRate(mountain);
        
        // Spawn based on probability (simplified for demo)
        if (random.nextDouble() < currentSpawnRate / 60.0) { // Per second probability
            spawnSingleGuest(mountain);
        }
    }
    
    /**
     * Calculate current spawn rate based on various factors
     * Extensible for complex spawning algorithms
     */
    private double calculateCurrentSpawnRate(Mountain mountain) {
        double rate = baseSpawnRate;
        
        // Future extensibility points:
        // rate *= satisfactionMultiplier;  // Based on average guest satisfaction
        // rate *= seasonalMultiplier;      // Based on season/day of week  
        // rate *= weatherMultiplier;       // Based on weather conditions
        // rate *= mountain.getAppealScore() / 100.0; // Based on resort appeal
        
        return Math.max(0, rate);
    }
    
    /**
     * Spawn a single new guest
     */
    private void spawnSingleGuest(Mountain mountain) {
        // Generate random guest properties
        String name = guestNames.get(random.nextInt(guestNames.size()));
        Guest.SkillLevel skillLevel = getRandomSkillLevel();
        Position startPosition = getRandomStartPosition(mountain);
        
        Guest newGuest = new Guest(name, skillLevel, startPosition);
        activeGuests.put(newGuest.getId(), newGuest);
        totalGuestsSpawned++;
    }
    
    /**
     * Generate random skill level with realistic distribution
     */
    private Guest.SkillLevel getRandomSkillLevel() {
        double rand = random.nextDouble();
        
        // Realistic distribution: more beginners and intermediates than experts
        if (rand < 0.3) {
            return Guest.SkillLevel.BEGINNER;
        } else if (rand < 0.7) {
            return Guest.SkillLevel.INTERMEDIATE;
        } else if (rand < 0.95) {
            return Guest.SkillLevel.ADVANCED;
        } else {
            return Guest.SkillLevel.EXPERT;
        }
    }
    
    /**
     * Get random starting position for new guests
     * Future: Could be based on parking areas, lodge locations, etc.
     */
    private Position getRandomStartPosition(Mountain mountain) {
        // For now, start guests at the bottom of the mountain
        int x = random.nextInt(mountain.getWidth());
        int y = mountain.getHeight() - 1;
        return new Position(x, y);
    }
    
    /**
     * Update running statistics for analytics
     */
    private void updateStatistics() {
        if (activeGuests.isEmpty()) {
            return;
        }
        
        double totalSatisfaction = activeGuests.values().stream()
            .mapToInt(Guest::getSatisfaction)
            .average()
            .orElse(0.0);
        
        averageSatisfaction = totalSatisfaction;
    }
    
    // Configuration methods (for future extensibility)
    public void setBaseSpawnRate(int rate) { this.baseSpawnRate = rate; }
    public void setMaxCapacity(int capacity) { this.maxCapacity = capacity; }
    public void setSatisfactionMultiplier(double multiplier) { this.satisfactionMultiplier = multiplier; }
    public void setSeasonalMultiplier(double multiplier) { this.seasonalMultiplier = multiplier; }
    public void setWeatherMultiplier(double multiplier) { this.weatherMultiplier = multiplier; }
    
    // Getters for statistics and current state
    public Collection<Guest> getActiveGuests() { return new ArrayList<>(activeGuests.values()); }
    public int getActiveGuestCount() { return activeGuests.size(); }
    public int getTotalGuestsSpawned() { return totalGuestsSpawned; }
    public int getTotalGuestsCompleted() { return totalGuestsCompleted; }
    public double getAverageSatisfaction() { return averageSatisfaction; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getBaseSpawnRate() { return baseSpawnRate; }
    
    /**
     * Get guests by state for analytics
     */
    public Map<Guest.GuestState, Long> getGuestsByState() {
        return activeGuests.values().stream()
            .collect(Collectors.groupingBy(
                Guest::getState, 
                Collectors.counting()
            ));
    }
    
    /**
     * Get guests by skill level for analytics  
     */
    public Map<Guest.SkillLevel, Long> getGuestsBySkillLevel() {
        return activeGuests.values().stream()
            .collect(Collectors.groupingBy(
                Guest::getSkillLevel,
                Collectors.counting()
            ));
    }
    
    @Override
    public String toString() {
        return String.format("GuestManager{active=%d, spawned=%d, completed=%d, avgSatisfaction=%.1f}", 
            activeGuests.size(), totalGuestsSpawned, totalGuestsCompleted, averageSatisfaction);
    }
} 