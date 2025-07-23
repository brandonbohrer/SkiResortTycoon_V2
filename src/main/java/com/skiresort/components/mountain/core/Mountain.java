package com.skiresort.components.mountain.core;

import com.skiresort.shared.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core Mountain entity representing the terrain and elevation of the ski resort
 */
public class Mountain {
    private final String id;
    private final String name;
    private final int width;
    private final int height;
    private final int[][] elevation; // Elevation at each point
    private final List<Slope> slopes;
    private final List<Lift> lifts;
    
    public Mountain(String name, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.width = width;
        this.height = height;
        this.elevation = new int[height][width];
        this.slopes = new ArrayList<>();
        this.lifts = new ArrayList<>();
        
        // Initialize with basic elevation (can be enhanced later)
        generateBasicElevation();
    }
    
    /**
     * Generate a basic mountain elevation profile
     * In the future, this could be more sophisticated with noise algorithms
     */
    private void generateBasicElevation() {
        int centerX = width / 2;
        int peakHeight = 100;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Simple cone-shaped mountain for now
                double distanceFromCenter = Math.sqrt(
                    Math.pow(x - centerX, 2) + Math.pow(y, 2)
                );
                double maxDistance = Math.sqrt(
                    Math.pow(centerX, 2) + Math.pow(height, 2)
                );
                
                // Higher elevation at the center/top, lower at edges/bottom
                int elevationValue = (int) (peakHeight * 
                    (1.0 - (distanceFromCenter / maxDistance)));
                elevation[y][x] = Math.max(0, elevationValue);
            }
        }
    }
    
    public boolean addSlope(Slope slope) {
        if (isValidSlopePosition(slope)) {
            slopes.add(slope);
            return true;
        }
        return false;
    }
    
    public boolean addLift(Lift lift) {
        if (isValidLiftPosition(lift)) {
            lifts.add(lift);
            return true;
        }
        return false;
    }
    
    private boolean isValidSlopePosition(Slope slope) {
        Position start = slope.getStartPosition();
        Position end = slope.getEndPosition();
        
        // Check bounds
        if (!start.isWithinBounds(width, height) || 
            !end.isWithinBounds(width, height)) {
            return false;
        }
        
        // Check that slope goes downhill (start elevation > end elevation)
        return getElevationAt(start) > getElevationAt(end);
    }
    
    private boolean isValidLiftPosition(Lift lift) {
        Position bottom = lift.getBottomPosition();
        Position top = lift.getTopPosition();
        
        // Check bounds
        if (!bottom.isWithinBounds(width, height) || 
            !top.isWithinBounds(width, height)) {
            return false;
        }
        
        // Check that lift goes uphill (bottom elevation < top elevation)
        return getElevationAt(bottom) < getElevationAt(top);
    }
    
    public int getElevationAt(Position position) {
        if (!position.isWithinBounds(width, height)) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return elevation[position.getY()][position.getX()];
    }
    
    public int getElevationAt(int x, int y) {
        return getElevationAt(new Position(x, y));
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Slope> getSlopes() { return new ArrayList<>(slopes); }
    public List<Lift> getLifts() { return new ArrayList<>(lifts); }
    
    @Override
    public String toString() {
        return String.format("Mountain{name='%s', size=%dx%d, slopes=%d, lifts=%d}", 
            name, width, height, slopes.size(), lifts.size());
    }
} 