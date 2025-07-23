package com.skiresort.components.mountain.core;

import com.skiresort.shared.Position;
import java.util.UUID;

/**
 * Represents a ski lift for transporting guests up the mountain
 */
public class Lift {
    
    /**
     * Lift tiers for future unlock progression system
     */
    public enum LiftTier {
        STARTER("Starter", "Basic lifts available from the beginning"),
        INTERMEDIATE("Intermediate", "Requires moderate resort development"),
        ADVANCED("Advanced", "High-tech lifts for established resorts"),
        PREMIUM("Premium", "Elite lifts for major ski destinations");
        
        private final String displayName;
        private final String description;
        
        LiftTier(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    public enum LiftType {
        // Starter lifts - available from beginning
        MAGIC_CARPET("Magic Carpet", 20, 500, 10000, true, LiftTier.STARTER),
        ROPE_TOW("Rope Tow", 1, 600, 8000, true, LiftTier.STARTER),
        SMALL_DIESEL_LIFT("Small 2-Seat Diesel Lift", 2, 900, 15000, true, LiftTier.STARTER),
        
        // Advanced lifts - extensible for future unlock system
        CHAIRLIFT("4-Seat Chairlift", 4, 1200, 25000, true, LiftTier.INTERMEDIATE),
        HIGH_SPEED_CHAIRLIFT("4-Seat High-Speed Chairlift", 4, 400, 75000, true, LiftTier.ADVANCED), // 3x faster than regular chairlift
        GONDOLA("Gondola", 8, 2000, 80000, true, LiftTier.PREMIUM),
        
        // Legacy T-Bar kept for compatibility
        T_BAR("T-Bar", 1, 800, 15000, true, LiftTier.STARTER);
        
        private final String displayName;
        private final int capacity; // people per trip
        private final int rideTimeSeconds; // time to reach top
        private final double buildCost;
        private final boolean available; // Future: for unlock system
        private final LiftTier tier; // Future: for progression system
        
        LiftType(String displayName, int capacity, int rideTimeSeconds, double buildCost, boolean available, LiftTier tier) {
            this.displayName = displayName;
            this.capacity = capacity;
            this.rideTimeSeconds = rideTimeSeconds;
            this.buildCost = buildCost;
            this.available = available;
            this.tier = tier;
        }
        
        public String getDisplayName() { return displayName; }
        public int getCapacity() { return capacity; }
        public int getRideTimeSeconds() { return rideTimeSeconds; }
        public double getBuildCost() { return buildCost; }
        public boolean isAvailable() { return available; }
        public LiftTier getTier() { return tier; }
        
        /**
         * Calculate efficiency score (guests per minute)
         */
        public double getEfficiencyScore() {
            return (double) capacity / (rideTimeSeconds / 60.0);
        }
        
        /**
         * Check if this is a high-speed lift
         */
        public boolean isHighSpeed() {
            return this == HIGH_SPEED_CHAIRLIFT || this == GONDOLA;
        }
    }
    
    private final String id;
    private final String name;
    private final Position bottomPosition;
    private final Position topPosition;
    private final LiftType type;
    private final double buildCost;
    private final double maintenanceCost;
    private final double operationalCost; // Per hour
    private boolean isOperational;
    private int currentCapacity; // Current number of people using it
    
    // Enhanced operational mechanics (extensible for future features)
    private double efficiency; // 0.0 to 1.0 - affects actual vs theoretical capacity
    private int maintenanceHours; // Hours since last maintenance (future: affects reliability)
    private boolean weatherSafe; // Can this lift operate in bad weather?
    private int waitingGuests; // Number of guests in queue
    private double operationalCostMultiplier; // Future: staff, weather, efficiency effects
    
    public Lift(String name, Position bottomPosition, Position topPosition, LiftType type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.bottomPosition = bottomPosition;
        this.topPosition = topPosition;
        this.type = type;
        this.buildCost = type.getBuildCost();
        this.maintenanceCost = buildCost * 0.08; // 8% annually
        this.operationalCost = buildCost * 0.001; // Operating cost per hour
        this.isOperational = true;
        this.currentCapacity = 0;
        
        // Initialize enhanced operational mechanics
        this.efficiency = 0.85 + (type.isHighSpeed() ? 0.10 : 0.0); // High-speed lifts more efficient
        this.maintenanceHours = 0;
        this.weatherSafe = type != LiftType.ROPE_TOW && type != LiftType.T_BAR; // Surface lifts less weather-safe
        this.waitingGuests = 0;
        this.operationalCostMultiplier = 1.0;
    }
    
    public double getLength() {
        return bottomPosition.distanceTo(topPosition);
    }
    
    public int getElevationGain(Mountain mountain) {
        return mountain.getElevationAt(topPosition) - mountain.getElevationAt(bottomPosition);
    }
    
    /**
     * Calculate hourly throughput based on capacity and ride time
     */
    public int getHourlyThroughput() {
        double tripsPerHour = 3600.0 / type.getRideTimeSeconds();
        return (int) (tripsPerHour * type.getCapacity());
    }
    
    /**
     * Check if the lift can accommodate more guests
     */
    public boolean canAcceptGuests() {
        return isOperational && currentCapacity < getHourlyThroughput();
    }
    
    /**
     * Add guests to the lift (returns true if successful)
     */
    public boolean addGuests(int guestCount) {
        if (!canAcceptGuests()) {
            return false;
        }
        
        int availableCapacity = getHourlyThroughput() - currentCapacity;
        int guestsToAdd = Math.min(guestCount, availableCapacity);
        currentCapacity += guestsToAdd;
        
        return guestsToAdd == guestCount;
    }
    
    /**
     * Remove guests when they complete the ride
     */
    public void removeGuests(int guestCount) {
        currentCapacity = Math.max(0, currentCapacity - guestCount);
    }
    
    /**
     * Get effective capacity considering efficiency and operational status
     */
    public int getEffectiveCapacity() {
        if (!isOperational) return 0;
        return (int) (type.getCapacity() * efficiency);
    }
    
    /**
     * Get actual guests per hour throughput
     */
    public double getGuestsPerHour() {
        if (!isOperational) return 0;
        return (3600.0 / type.getRideTimeSeconds()) * getEffectiveCapacity();
    }
    
    /**
     * Add guests to waiting queue
     */
    public void addWaitingGuests(int count) {
        waitingGuests += count;
    }
    
    /**
     * Process guest queue - move waiting guests to lift if space available
     * Returns number of guests actually loaded
     */
    public int processGuestQueue() {
        if (!isOperational || waitingGuests <= 0) return 0;
        
        int availableCapacity = getEffectiveCapacity() - currentCapacity;
        int guestsToLoad = Math.min(waitingGuests, availableCapacity);
        
        if (guestsToLoad > 0) {
            currentCapacity += guestsToLoad;
            waitingGuests -= guestsToLoad;
        }
        
        return guestsToLoad;
    }
    
    /**
     * Calculate wait time in minutes for new guests
     */
    public double getWaitTimeMinutes() {
        if (!isOperational) return Double.MAX_VALUE;
        if (waitingGuests <= 0) return 0;
        
        double cycleTimeMinutes = type.getRideTimeSeconds() / 60.0;
        double cyclesNeeded = Math.ceil((double) waitingGuests / getEffectiveCapacity());
        
        return cyclesNeeded * cycleTimeMinutes;
    }
    
    /**
     * Perform maintenance (future: could be expanded with different maintenance types)
     */
    public void performMaintenance() {
        maintenanceHours = 0;
        efficiency = Math.min(0.95, efficiency + 0.05); // Maintenance improves efficiency
    }
    
    /**
     * Update operational status (called each game hour)
     */
    public void updateOperationalStatus() {
        maintenanceHours++;
        
        // Gradually decrease efficiency over time (future: could add breakdown chances)
        if (maintenanceHours > 100) {
            efficiency = Math.max(0.60, efficiency - 0.001);
        }
    }
    
    /**
     * Check if lift needs maintenance
     */
    public boolean needsMaintenance() {
        return efficiency < 0.70 || maintenanceHours > 200;
    }
    
    /**
     * Set weather operational status
     */
    public void setWeatherOperational(boolean canOperate) {
        if (!weatherSafe && !canOperate) {
            // Force shutdown in bad weather for unsafe lifts
            setOperational(false);
        }
    }
    
    /**
     * Get the appeal score for this lift based on efficiency and type
     */
    public int getAppealScore() {
        int baseScore = 50;
        
        // More modern lifts are more appealing
        switch (type) {
            case GONDOLA -> baseScore += 20;
            case CHAIRLIFT -> baseScore += 10;
            case MAGIC_CARPET -> baseScore += 5;
            case T_BAR -> baseScore -= 10;
        }
        
        // Operational lifts are more appealing
        if (!isOperational) {
            baseScore -= 40;
        }
        
        // Less crowded lifts are more appealing
        double crowdingFactor = (double) currentCapacity / getHourlyThroughput();
        baseScore -= (int) (crowdingFactor * 20);
        
        return Math.max(0, Math.min(100, baseScore));
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public Position getBottomPosition() { return bottomPosition; }
    public Position getTopPosition() { return topPosition; }
    public LiftType getType() { return type; }
    public double getBuildCost() { return buildCost; }
    public double getMaintenanceCost() { return maintenanceCost; }
    public double getOperationalCost() { return operationalCost; }
    public boolean isOperational() { return isOperational; }
    public void setOperational(boolean operational) { this.isOperational = operational; }
    public int getCurrentCapacity() { return currentCapacity; }
    public double getEfficiency() { return efficiency; }
    public int getMaintenanceHours() { return maintenanceHours; }
    public boolean isWeatherSafe() { return weatherSafe; }
    public int getWaitingGuests() { return waitingGuests; }
    public double getOperationalCostMultiplier() { return operationalCostMultiplier; }
    
    // Setters for operational control (extensible for management interface)
    public void setEfficiency(double efficiency) { 
        this.efficiency = Math.max(0.0, Math.min(1.0, efficiency)); 
    }
    public void setOperationalCostMultiplier(double multiplier) { 
        this.operationalCostMultiplier = Math.max(0.5, multiplier); 
    }
    
    @Override
    public String toString() {
        return String.format("Lift{name='%s', type=%s, efficiency=%.0f%%, queue=%d, throughput=%.0f/hr}", 
            name, type.getDisplayName(), efficiency * 100, waitingGuests, getGuestsPerHour());
    }
} 