package com.skiresort.components.mountain.visual;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.guests.core.Guest;
import com.skiresort.components.guests.visual.GuestRenderer;
import com.skiresort.shared.Position;
import java.util.Collection;

/**
 * Simple console-based renderer for the Mountain component
 * In the future, this could be replaced with a proper graphics renderer
 */
public class MountainRenderer {
    
    private static final char EMPTY_SPACE = '.';
    private static final char SLOPE_CHAR = 'S';
    private static final char LIFT_CHAR = 'L';
    private static final char INTERSECTION = 'X';
    
    /**
     * Render the mountain as ASCII art showing elevations, slopes, lifts, and guests
     */
    public static String renderMountainView(Mountain mountain) {
        return renderMountainView(mountain, null);
    }
    
    /**
     * Render the mountain with guests
     */
    public static String renderMountainView(Mountain mountain, Collection<Guest> guests) {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("=== ").append(mountain.getName()).append(" ===\n");
        sb.append("Size: ").append(mountain.getWidth()).append("x").append(mountain.getHeight());
        sb.append(" | Slopes: ").append(mountain.getSlopes().size());
        sb.append(" | Lifts: ").append(mountain.getLifts().size());
        
        if (guests != null) {
            sb.append(" | Guests: ").append(guests.size());
        }
        sb.append("\n\n");
        
        // Create a grid to show mountain features
        char[][] grid = createMountainGrid(mountain);
        
        // Add slopes and lifts to grid
        markSlopesOnGrid(grid, mountain);
        markLiftsOnGrid(grid, mountain);
        
        // Add guests if provided
        if (guests != null && !guests.isEmpty()) {
            GuestRenderer.addGuestsToGrid(grid, guests);
        }
        
        // Render the grid
        for (int y = 0; y < mountain.getHeight(); y++) {
            for (int x = 0; x < mountain.getWidth(); x++) {
                sb.append(grid[y][x]);
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Create a basic grid showing elevation with simple characters
     */
    private static char[][] createMountainGrid(Mountain mountain) {
        char[][] grid = new char[mountain.getHeight()][mountain.getWidth()];
        
        for (int y = 0; y < mountain.getHeight(); y++) {
            for (int x = 0; x < mountain.getWidth(); x++) {
                int elevation = mountain.getElevationAt(x, y);
                
                // Simple elevation representation
                if (elevation > 80) {
                    grid[y][x] = '^'; // Peak
                } else if (elevation > 60) {
                    grid[y][x] = '*'; // High elevation
                } else if (elevation > 40) {
                    grid[y][x] = '+'; // Medium elevation
                } else if (elevation > 20) {
                    grid[y][x] = '-'; // Low elevation
                } else {
                    grid[y][x] = EMPTY_SPACE; // Very low/flat
                }
            }
        }
        
        return grid;
    }
    
    /**
     * Mark slopes on the grid
     */
    private static void markSlopesOnGrid(char[][] grid, Mountain mountain) {
        for (Slope slope : mountain.getSlopes()) {
            Position start = slope.getStartPosition();
            Position end = slope.getEndPosition();
            
            // Draw a simple line for the slope
            drawLine(grid, start, end, SLOPE_CHAR);
        }
    }
    
    /**
     * Mark lifts on the grid
     */
    private static void markLiftsOnGrid(char[][] grid, Mountain mountain) {
        for (Lift lift : mountain.getLifts()) {
            Position bottom = lift.getBottomPosition();
            Position top = lift.getTopPosition();
            
            // Draw a simple line for the lift
            drawLine(grid, bottom, top, LIFT_CHAR);
        }
    }
    
    /**
     * Draw a simple line between two points on the grid
     */
    private static void drawLine(char[][] grid, Position start, Position end, char lineChar) {
        int x1 = start.getX();
        int y1 = start.getY();
        int x2 = end.getX();
        int y2 = end.getY();
        
        // Simple line drawing using Bresenham-like algorithm
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int x = x1;
        int y = y1;
        int n = 1 + dx + dy;
        int x_inc = (x2 > x1) ? 1 : -1;
        int y_inc = (y2 > y1) ? 1 : -1;
        int error = dx - dy;
        
        dx *= 2;
        dy *= 2;
        
        for (; n > 0; --n) {
            if (x >= 0 && x < grid[0].length && y >= 0 && y < grid.length) {
                // If there's already something at this position, mark as intersection
                if (grid[y][x] != EMPTY_SPACE && grid[y][x] != '^' && 
                    grid[y][x] != '*' && grid[y][x] != '+' && grid[y][x] != '-') {
                    grid[y][x] = INTERSECTION;
                } else {
                    grid[y][x] = lineChar;
                }
            }
            
            if (error > 0) {
                x += x_inc;
                error -= dy;
            } else {
                y += y_inc;
                error += dx;
            }
        }
    }
    
    /**
     * Render detailed information about the mountain
     */
    public static String renderMountainDetails(Mountain mountain) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Mountain Details ===\n");
        sb.append("Name: ").append(mountain.getName()).append("\n");
        sb.append("ID: ").append(mountain.getId()).append("\n");
        sb.append("Dimensions: ").append(mountain.getWidth()).append(" x ").append(mountain.getHeight()).append("\n");
        
        // Slopes information
        sb.append("\nSlopes (").append(mountain.getSlopes().size()).append("):\n");
        for (Slope slope : mountain.getSlopes()) {
            sb.append("  - ").append(slope.getName())
              .append(" (").append(slope.getDifficulty().getDisplayName()).append(")")
              .append(" - Length: ").append(String.format("%.1f", slope.getLength()))
              .append(" - Cost: $").append(String.format("%.0f", slope.getBuildCost()))
              .append(" - Open: ").append(slope.isOpen())
              .append(" - Appeal: ").append(slope.getAppealScore()).append("/100")
              .append("\n");
        }
        
        // Lifts information
        sb.append("\nLifts (").append(mountain.getLifts().size()).append("):\n");
        for (Lift lift : mountain.getLifts()) {
            sb.append("  - ").append(lift.getName())
              .append(" (").append(lift.getType().getDisplayName()).append(")")
              .append(" - Length: ").append(String.format("%.1f", lift.getLength()))
              .append(" - Cost: $").append(String.format("%.0f", lift.getBuildCost()))
              .append(" - Operational: ").append(lift.isOperational())
              .append(" - Capacity: ").append(lift.getCurrentCapacity()).append("/").append(lift.getHourlyThroughput())
              .append(" - Appeal: ").append(lift.getAppealScore()).append("/100")
              .append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Render elevation statistics
     */
    public static String renderElevationStats(Mountain mountain) {
        StringBuilder sb = new StringBuilder();
        
        int minElevation = Integer.MAX_VALUE;
        int maxElevation = Integer.MIN_VALUE;
        int totalElevation = 0;
        int pointCount = mountain.getWidth() * mountain.getHeight();
        
        for (int y = 0; y < mountain.getHeight(); y++) {
            for (int x = 0; x < mountain.getWidth(); x++) {
                int elevation = mountain.getElevationAt(x, y);
                minElevation = Math.min(minElevation, elevation);
                maxElevation = Math.max(maxElevation, elevation);
                totalElevation += elevation;
            }
        }
        
        double averageElevation = (double) totalElevation / pointCount;
        
        sb.append("=== Elevation Statistics ===\n");
        sb.append("Minimum Elevation: ").append(minElevation).append("\n");
        sb.append("Maximum Elevation: ").append(maxElevation).append("\n");
        sb.append("Average Elevation: ").append(String.format("%.1f", averageElevation)).append("\n");
        sb.append("Total Vertical: ").append(maxElevation - minElevation).append("\n");
        
        return sb.toString();
    }
} 