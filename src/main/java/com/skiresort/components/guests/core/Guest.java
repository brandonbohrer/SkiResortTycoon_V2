package com.skiresort.components.guests.core;

import com.skiresort.shared.Position;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import java.util.UUID;

/**
 * Represents an individual guest (skier/snowboarder) visiting the resort
 * Designed with extensibility in mind for future features like:
 * - Satisfaction-based spawning rates
 * - Park capacity limits
 * - Dynamic pricing response
 * - Weather preferences
 */
public class Guest {
    
    public enum SkillLevel {
        BEGINNER(1, "Learning the basics"),
        INTERMEDIATE(2, "Comfortable on most slopes"), 
        ADVANCED(3, "Seeks challenging terrain"),
        EXPERT(4, "Master of the mountain");
        
        private final int level;
        private final String description;
        
        SkillLevel(int level, String description) {
            this.level = level;
            this.description = description;
        }
        
        public int getLevel() { return level; }
        public String getDescription() { return description; }
    }
    
    public enum GuestState {
        ENTERING_RESORT,    // Just arrived, choosing first activity
        WAITING_FOR_LIFT,   // In lift queue
        RIDING_LIFT,        // Currently on a lift
        SKIING_SLOPE,       // Currently skiing down a slope
        RESTING,           // Taking a break (future: eating, shopping)
        LEAVING_RESORT     // Satisfaction/time-based departure
    }
    
    // Core identity
    private final String id;
    private final String name;
    private final SkillLevel skillLevel;
    
    // Current state
    private GuestState state;
    private Position currentPosition;
    private Slope currentSlope;
    private Lift currentLift;
    
    // Satisfaction system (extensible for future features)
    private int satisfaction;           // Current satisfaction (0-100)
    private int baseSatisfaction;      // Starting satisfaction level
    private int totalRunsCompleted;   // Tracking for experience
    
    // Time tracking (for future capacity/pricing features)
    private long arrivalTime;         // When guest entered resort
    private long currentActivityStartTime; // When current activity started
    
    // Financial tracking (for future revenue features)
    private double totalSpent;        // Money spent at resort
    private boolean hasDayPass;       // Lift ticket status
    
    // Future extensibility placeholders
    private int weatherTolerance;     // For weather system
    private int crowdTolerance;       // For capacity management
    private double pricesensitivity; // For dynamic pricing
    
    public Guest(String name, SkillLevel skillLevel, Position startPosition) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.skillLevel = skillLevel;
        this.currentPosition = startPosition;
        this.state = GuestState.ENTERING_RESORT;
        
        // Initialize satisfaction based on skill level and random factors
        this.baseSatisfaction = 50 + (int)(Math.random() * 30); // 50-80 base
        this.satisfaction = baseSatisfaction;
        
        // Initialize time tracking
        this.arrivalTime = System.currentTimeMillis();
        this.currentActivityStartTime = arrivalTime;
        
        // Initialize financial tracking
        this.totalSpent = 0.0;
        this.hasDayPass = true; // Start with day pass for simplicity
        
        // Initialize future feature placeholders with reasonable defaults
        this.weatherTolerance = 50 + (int)(Math.random() * 40); // 50-90
        this.crowdTolerance = 30 + (int)(Math.random() * 50);   // 30-80
        this.pricesensitivity = 0.3 + (Math.random() * 0.7);   // 0.3-1.0
        
        this.totalRunsCompleted = 0;
    }
    
    /**
     * Update guest satisfaction based on current conditions
     * Designed to be extended for complex satisfaction calculations
     */
    public void updateSatisfaction(int change, String reason) {
        int oldSatisfaction = satisfaction;
        satisfaction = Math.max(0, Math.min(100, satisfaction + change));
        
        // Future: Log satisfaction changes for analytics
        // Logger could track: guest_id, change, reason, new_value, timestamp
    }
    
    /**
     * Check if guest is willing to use a specific slope based on skill and preferences
     * Extensible for future features like crowd aversion, weather conditions
     */
    public boolean isWillingToUseSlope(Slope slope) {
        if (!slope.isOpen()) {
            return false;
        }
        
        // Basic skill level matching
        int slopeDifficulty = slope.getDifficulty().getDifficultyLevel();
        int guestSkill = skillLevel.getLevel();
        
        // Guests generally prefer slopes at or slightly below their skill level
        if (slopeDifficulty > guestSkill + 1) {
            return false; // Too difficult
        }
        
        if (slopeDifficulty < guestSkill - 2) {
            return false; // Too easy (advanced skiers won't use beginner slopes)
        }
        
        // Future extensibility points:
        // - Check crowd levels: if (slope.getCurrentCrowdLevel() > crowdTolerance) return false;
        // - Check weather: if (slope.getSnowQuality() < weatherTolerance) return false;
        // - Check wait times: if (slope.getAverageWaitTime() > maxWaitTime) return false;
        
        return true;
    }
    
    /**
     * Check if guest is willing to use a specific lift
     * Extensible for queue length, crowd tolerance, etc.
     */
    public boolean isWillingToUseLift(Lift lift) {
        if (!lift.isOperational()) {
            return false;
        }
        
        // Basic capacity check
        if (!lift.canAcceptGuests()) {
            return false;
        }
        
        // Future extensibility points:
        // - Check queue length: if (lift.getQueueLength() > crowdTolerance) return false;
        // - Check lift type preference: some guests prefer gondolas over chairlifts
        // - Check weather conditions: some guests won't ride open lifts in bad weather
        
        return true;
    }
    
    /**
     * Calculate how much this guest would be willing to pay for services
     * Foundation for future dynamic pricing system
     */
    public double getWillingnessToPayMultiplier() {
        double baseMultiplier = 1.0;
        
        // Higher satisfaction = willing to pay more
        baseMultiplier += (satisfaction - 50) * 0.01; // +/- 50% based on satisfaction
        
        // Skill level affects spending (advanced skiers often spend more)
        baseMultiplier += (skillLevel.getLevel() - 2) * 0.1; // +/- 20% based on skill
        
        // Price sensitivity factor
        baseMultiplier *= (2.0 - pricesensitivity);
        
        return Math.max(0.5, Math.min(2.0, baseMultiplier)); // Clamp between 50% and 200%
    }
    
    /**
     * Simulate guest decision making for next activity
     * Extensible for complex AI behaviors
     */
    public GuestActivity chooseNextActivity() {
        // Simple decision tree that can be extended
        switch (state) {
            case ENTERING_RESORT:
                return new GuestActivity(GuestActivity.Type.FIND_LIFT, null);
                
            case RIDING_LIFT:
                return new GuestActivity(GuestActivity.Type.FIND_SLOPE, null);
                
            case SKIING_SLOPE:
                // Decision based on satisfaction and fatigue
                if (satisfaction < 30 || totalRunsCompleted > 10) {
                    return new GuestActivity(GuestActivity.Type.LEAVE_RESORT, null);
                } else {
                    return new GuestActivity(GuestActivity.Type.FIND_LIFT, null);
                }
                
            default:
                return new GuestActivity(GuestActivity.Type.WAIT, null);
        }
    }
    
    // State management
    public void setState(GuestState newState) {
        this.state = newState;
        this.currentActivityStartTime = System.currentTimeMillis();
    }
    
    public void setCurrentSlope(Slope slope) {
        this.currentSlope = slope;
        if (slope != null) {
            setState(GuestState.SKIING_SLOPE);
        }
    }
    
    public void setCurrentLift(Lift lift) {
        this.currentLift = lift;
        if (lift != null) {
            setState(GuestState.RIDING_LIFT);
        }
    }
    
    public void completeRun() {
        totalRunsCompleted++;
        // Future: Award satisfaction bonus for completed runs
        updateSatisfaction(5, "Completed ski run");
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public SkillLevel getSkillLevel() { return skillLevel; }
    public GuestState getState() { return state; }
    public Position getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Position position) { this.currentPosition = position; }
    public Slope getCurrentSlope() { return currentSlope; }
    public Lift getCurrentLift() { return currentLift; }
    public int getSatisfaction() { return satisfaction; }
    public int getTotalRunsCompleted() { return totalRunsCompleted; }
    public long getArrivalTime() { return arrivalTime; }
    public double getTotalSpent() { return totalSpent; }
    public void addSpending(double amount) { this.totalSpent += amount; }
    
    // Future extensibility getters
    public int getWeatherTolerance() { return weatherTolerance; }
    public int getCrowdTolerance() { return crowdTolerance; }
    public double getPriceSensitivity() { return pricesensitivity; }
    
    @Override
    public String toString() {
        return String.format("Guest{name='%s', skill=%s, satisfaction=%d, runs=%d, state=%s}", 
            name, skillLevel, satisfaction, totalRunsCompleted, state);
    }
} 