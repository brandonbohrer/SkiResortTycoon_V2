package com.skiresort.graphics;

import com.skiresort.shared.Position;

/**
 * Utility class for isometric coordinate conversions and calculations
 * Handles the math for converting between world coordinates and screen coordinates
 * with a fixed isometric perspective (like RollerCoaster Tycoon)
 */
public class IsometricMath {
    
    // Isometric tile dimensions (in screen pixels)
    public static final int TILE_WIDTH = 64;   // Width of a tile in pixels
    public static final int TILE_HEIGHT = 32;  // Height of a tile in pixels (half width for isometric)
    
    // Isometric angles (standard 2:1 ratio for classic look)
    private static final double COS_ANGLE = Math.sqrt(3) / 2.0; // ~0.866
    private static final double SIN_ANGLE = 0.5;
    
    /**
     * Convert world coordinates to isometric screen coordinates
     * World coordinates: (x, y) where y increases going "north", x increases going "east"
     * Screen coordinates: (screenX, screenY) where (0,0) is top-left
     * 
     * @param worldX World X coordinate 
     * @param worldY World Y coordinate
     * @param elevation Height/elevation at this position (for 3D effect)
     * @return ScreenPoint containing screen coordinates
     */
    public static ScreenPoint worldToScreen(int worldX, int worldY, double elevation) {
        // Standard isometric transformation
        int screenX = (worldX - worldY) * (TILE_WIDTH / 2);
        int screenY = (worldX + worldY) * (TILE_HEIGHT / 2);
        
        // Subtract elevation to create height effect (higher = appears higher on screen)
        screenY -= (int)(elevation * TILE_HEIGHT / 4); // Scale elevation effect
        
        return new ScreenPoint(screenX, screenY);
    }
    
    /**
     * Convert world coordinates to screen coordinates (elevation = 0)
     */
    public static ScreenPoint worldToScreen(int worldX, int worldY) {
        return worldToScreen(worldX, worldY, 0.0);
    }
    
    /**
     * Convert world Position to screen coordinates
     */
    public static ScreenPoint worldToScreen(Position worldPos, double elevation) {
        return worldToScreen(worldPos.getX(), worldPos.getY(), elevation);
    }
    
    /**
     * Convert screen coordinates back to world coordinates
     * Useful for mouse click detection and tile selection
     * 
     * @param screenX Screen X coordinate
     * @param screenY Screen Y coordinate  
     * @return Position containing world coordinates
     */
    public static Position screenToWorld(int screenX, int screenY) {
        // Reverse the isometric transformation
        // This gives us the tile coordinates without considering elevation
        double worldX = (screenX / (TILE_WIDTH / 2.0) + screenY / (TILE_HEIGHT / 2.0)) / 2.0;
        double worldY = (screenY / (TILE_HEIGHT / 2.0) - screenX / (TILE_WIDTH / 2.0)) / 2.0;
        
        return new Position((int)Math.round(worldX), (int)Math.round(worldY));
    }
    
    /**
     * Get the bounds of a tile in screen coordinates
     * Returns the four corner points of an isometric tile
     */
    public static ScreenPoint[] getTileBounds(int worldX, int worldY, double elevation) {
        ScreenPoint center = worldToScreen(worldX, worldY, elevation);
        
        return new ScreenPoint[] {
            new ScreenPoint(center.x, center.y - TILE_HEIGHT/2),           // Top
            new ScreenPoint(center.x + TILE_WIDTH/2, center.y),           // Right  
            new ScreenPoint(center.x, center.y + TILE_HEIGHT/2),          // Bottom
            new ScreenPoint(center.x - TILE_WIDTH/2, center.y)            // Left
        };
    }
    
    /**
     * Calculate distance between two world points
     */
    public static double worldDistance(Position p1, Position p2) {
        return p1.distanceTo(p2);
    }
    
    /**
     * Check if a screen point is inside an isometric tile
     * Useful for mouse hit detection
     */
    public static boolean isPointInTile(int screenX, int screenY, int worldTileX, int worldTileY, double elevation) {
        ScreenPoint[] bounds = getTileBounds(worldTileX, worldTileY, elevation);
        
        // Use point-in-diamond algorithm for isometric tile
        ScreenPoint center = worldToScreen(worldTileX, worldTileY, elevation);
        
        int relX = Math.abs(screenX - center.x);
        int relY = Math.abs(screenY - center.y);
        
        // Diamond shape check: |x|/width + |y|/height <= 1
        return (relX / (TILE_WIDTH / 2.0)) + (relY / (TILE_HEIGHT / 2.0)) <= 1.0;
    }
    
    /**
     * Calculate the draw order (z-order) for a tile
     * Tiles further back and higher up should be drawn first
     */
    public static int calculateDrawOrder(int worldX, int worldY, double elevation) {
        // Further back (higher Y) and lower elevation = draw first
        // This ensures proper depth sorting for the isometric view
        return (worldX + worldY) * 1000 - (int)(elevation * 100);
    }
    
    /**
     * Represents a point in screen coordinates
     */
    public static class ScreenPoint {
        public final int x;
        public final int y;
        
        public ScreenPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return String.format("ScreenPoint(%d, %d)", x, y);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ScreenPoint that = (ScreenPoint) obj;
            return x == that.x && y == that.y;
        }
        
        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
} 