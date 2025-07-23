package com.skiresort.shared;

/**
 * Game-wide constants and configuration values
 */
public final class GameConstants {
    
    // Game Loop
    public static final int TICKS_PER_SECOND = 30;
    public static final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    
    // Financial Constants
    public static final double STARTING_MONEY = 50000.0;
    public static final double LOAN_INTEREST_RATE = 0.05; // 5% annual
    
    // Guest Satisfaction
    public static final int MAX_SATISFACTION = 100;
    public static final int MIN_SATISFACTION = 0;
    public static final int DEFAULT_SATISFACTION = 50;
    
    // Mountain/Resort Limits
    public static final int MAX_MOUNTAIN_WIDTH = 200;
    public static final int MAX_MOUNTAIN_HEIGHT = 200;
    public static final int MIN_SLOPE_LENGTH = 10;
    public static final int MAX_SLOPE_LENGTH = 100;
    
    // Weather
    public static final int SNOW_QUALITY_PERFECT = 100;
    public static final int SNOW_QUALITY_POOR = 20;
    
    private GameConstants() {
        // Utility class - prevent instantiation
    }
} 