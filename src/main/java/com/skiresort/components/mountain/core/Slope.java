package com.skiresort.components.mountain.core;

import com.skiresort.shared.Position;
import java.util.UUID;

/**
 * Represents a ski slope with difficulty, position, and characteristics
 */
public class Slope {
    
    public enum Difficulty {
        BEGINNER("Green Circle", 1),
        INTERMEDIATE("Blue Square", 2),
        ADVANCED("Black Diamond", 3),
        EXPERT("Double Black Diamond", 4);
        
        private final String displayName;
        private final int difficultyLevel;
        
        Difficulty(String displayName, int difficultyLevel) {
            this.displayName = displayName;
            this.difficultyLevel = difficultyLevel;
        }
        
        public String getDisplayName() { return displayName; }
        public int getDifficultyLevel() { return difficultyLevel; }
    }
    
    private final String id;
    private final String name;
    private final Position startPosition;
    private final Position endPosition;
    private final Difficulty difficulty;
    private final double buildCost;
    private final double maintenanceCost;
    private boolean isOpen;
    private int snowQuality; // 0-100
    
    public Slope(String name, Position startPosition, Position endPosition, 
                 Difficulty difficulty, double buildCost) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.difficulty = difficulty;
        this.buildCost = buildCost;
        this.maintenanceCost = buildCost * 0.05; // 5% of build cost annually
        this.isOpen = true;
        this.snowQuality = 75; // Default decent snow
    }
    
    public double getLength() {
        return startPosition.distanceTo(endPosition);
    }
    
    public int getElevationDrop(Mountain mountain) {
        return mountain.getElevationAt(startPosition) - mountain.getElevationAt(endPosition);
    }
    
    public double getGradient(Mountain mountain) {
        double elevationDrop = getElevationDrop(mountain);
        double horizontalDistance = getLength();
        return horizontalDistance > 0 ? (elevationDrop / horizontalDistance) : 0;
    }
    
    /**
     * Calculate the appeal of this slope to guests based on difficulty and conditions
     */
    public int getAppealScore() {
        int baseScore = 50;
        
        // Snow quality affects appeal
        baseScore += (snowQuality - 50) / 2;
        
        // Open slopes are more appealing
        if (!isOpen) {
            baseScore -= 30;
        }
        
        return Math.max(0, Math.min(100, baseScore));
    }
    
    public void updateSnowQuality(int weatherSnowQuality) {
        // Snow quality is influenced by weather and maintenance
        this.snowQuality = Math.max(0, Math.min(100, 
            (int) (snowQuality * 0.9 + weatherSnowQuality * 0.1)));
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public Position getStartPosition() { return startPosition; }
    public Position getEndPosition() { return endPosition; }
    public Difficulty getDifficulty() { return difficulty; }
    public double getBuildCost() { return buildCost; }
    public double getMaintenanceCost() { return maintenanceCost; }
    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { this.isOpen = open; }
    public int getSnowQuality() { return snowQuality; }
    public void setSnowQuality(int snowQuality) { 
        this.snowQuality = Math.max(0, Math.min(100, snowQuality)); 
    }
    
    @Override
    public String toString() {
        return String.format("Slope{name='%s', difficulty=%s, length=%.1f, open=%s}", 
            name, difficulty.getDisplayName(), getLength(), isOpen);
    }
} 