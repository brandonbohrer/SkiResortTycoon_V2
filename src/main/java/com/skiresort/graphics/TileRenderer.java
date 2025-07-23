package com.skiresort.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.graphics.IsometricMath.ScreenPoint;

/**
 * Renderer for terrain tiles in the isometric view
 * Handles elevation-based coloring and basic terrain visualization
 */
public class TileRenderer {
    
    // Terrain color palette based on elevation
    private static final Color[] ELEVATION_COLORS = {
        Color.LIGHTGREEN,      // 0-10: Low grassland
        Color.GREEN,           // 11-20: Medium grassland  
        Color.DARKGREEN,       // 21-30: High grassland
        Color.LIGHTGRAY,       // 31-40: Rocky areas
        Color.GRAY,            // 41-50: High rocky areas
        Color.DARKGRAY,        // 51+: Mountain peaks
    };
    
    /**
     * Render a single terrain tile
     */
    public static void renderTile(GraphicsContext gc, Mountain mountain, Camera2D camera, 
                                  int worldX, int worldY) {
        if (!isInBounds(mountain, worldX, worldY)) {
            return;
        }
        
        double elevation = mountain.getElevationAt(worldX, worldY);
        
        // Skip if not visible
        if (!camera.isVisible(worldX, worldY, elevation)) {
            return;
        }
        
        // Get tile corners in screen space
        ScreenPoint[] corners = getTileScreenCorners(camera, worldX, worldY, elevation);
        
        // Choose color based on elevation
        Color tileColor = getElevationColor(elevation);
        gc.setFill(tileColor);
        
        // Draw the diamond-shaped tile
        double[] xPoints = {corners[0].x, corners[1].x, corners[2].x, corners[3].x};
        double[] yPoints = {corners[0].y, corners[1].y, corners[2].y, corners[3].y};
        gc.fillPolygon(xPoints, yPoints, 4);
        
        // Draw tile outline for definition
        gc.setStroke(tileColor.darker());
        gc.setLineWidth(0.5 * camera.getZoom());
        gc.strokePolygon(xPoints, yPoints, 4);
    }
    
    /**
     * Get the screen corners of a tile transformed by the camera
     */
    private static ScreenPoint[] getTileScreenCorners(Camera2D camera, int worldX, int worldY, double elevation) {
        ScreenPoint[] isometricCorners = IsometricMath.getTileBounds(worldX, worldY, elevation);
        ScreenPoint[] screenCorners = new ScreenPoint[4];
        
        for (int i = 0; i < 4; i++) {
            ScreenPoint isoCorner = isometricCorners[i];
            // Apply camera transform
            int screenX = (int)((isoCorner.x - camera.getWorldX() * IsometricMath.TILE_WIDTH/2) * camera.getZoom() + camera.getViewportWidth()/2);
            int screenY = (int)((isoCorner.y - camera.getWorldY() * IsometricMath.TILE_HEIGHT/2) * camera.getZoom() + camera.getViewportHeight()/2);
            screenCorners[i] = new ScreenPoint(screenX, screenY);
        }
        
        return screenCorners;
    }
    
    /**
     * Get color for terrain based on elevation
     */
    private static Color getElevationColor(double elevation) {
        int colorIndex = Math.min((int)(elevation / 10), ELEVATION_COLORS.length - 1);
        return ELEVATION_COLORS[colorIndex];
    }
    
    /**
     * Check if coordinates are within mountain bounds
     */
    private static boolean isInBounds(Mountain mountain, int x, int y) {
        return x >= 0 && x < mountain.getWidth() && y >= 0 && y < mountain.getHeight();
    }
    
    /**
     * Render terrain in proper depth order
     */
    public static void renderTerrain(GraphicsContext gc, Mountain mountain, Camera2D camera) {
        Camera2D.ViewBounds bounds = camera.getVisibleBounds();
        
        // Render tiles in proper z-order (back to front for isometric)
        for (int y = bounds.maxY; y >= bounds.minY; y--) {
            for (int x = bounds.minX; x <= bounds.maxX; x++) {
                renderTile(gc, mountain, camera, x, y);
            }
        }
    }
} 