package com.skiresort.graphics;

import com.skiresort.shared.Position;
import com.skiresort.graphics.IsometricMath.ScreenPoint;

/**
 * 2D Camera system for the isometric view
 * Handles panning, zooming, and viewport management
 * Fixed angle (no rotation) like classic tycoon games
 */
public class Camera2D {
    
    // Camera position in world coordinates
    private double worldX;
    private double worldY;
    
    // Camera zoom level
    private double zoom;
    private static final double MIN_ZOOM = 0.01; // Extremely zoomed out (almost no limit)
    private static final double MAX_ZOOM = 100.0;  // Extremely zoomed in (almost no limit)
    private static final double DEFAULT_ZOOM = 1.0;
    
    // Viewport dimensions (screen size)
    private int viewportWidth;
    private int viewportHeight;
    
    // Viewport offset (for centering camera)
    private int offsetX;
    private int offsetY;
    
    // Pan smoothing (for future smooth camera movement)
    private double targetWorldX;
    private double targetWorldY;
    private double panSpeed = 0.3; // How fast camera catches up to target (increased for responsiveness)
    
    public Camera2D(int viewportWidth, int viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        
        // Start centered at origin
        this.worldX = 0;
        this.worldY = 0;
        this.targetWorldX = 0;
        this.targetWorldY = 0;
        this.zoom = DEFAULT_ZOOM;
        
        // Center the viewport
        this.offsetX = viewportWidth / 2;
        this.offsetY = viewportHeight / 2;
    }
    
    /**
     * Convert world coordinates to screen coordinates relative to camera
     */
    public ScreenPoint worldToScreen(int worldX, int worldY, double elevation) {
        // Get base isometric coordinates
        ScreenPoint isoPoint = IsometricMath.worldToScreen(worldX, worldY, elevation);
        
        // Simple camera transform: subtract camera position, apply zoom, add screen center
        // This follows the standard pattern: screen = (world - camera) * zoom + offset
        ScreenPoint cameraIsoPoint = IsometricMath.worldToScreen((int)this.worldX, (int)this.worldY, 0);
        
        int screenX = (int)((isoPoint.x - cameraIsoPoint.x) * zoom + offsetX);
        int screenY = (int)((isoPoint.y - cameraIsoPoint.y) * zoom + offsetY);
        
        return new ScreenPoint(screenX, screenY);
    }
    
    /**
     * Convert world Position to screen coordinates relative to camera
     */
    public ScreenPoint worldToScreen(Position worldPos, double elevation) {
        return worldToScreen(worldPos.getX(), worldPos.getY(), elevation);
    }
    
    /**
     * Convert screen coordinates to world coordinates considering camera
     */
    public Position screenToWorld(int screenX, int screenY) {
        // Reverse the camera transform: remove screen center, reverse zoom, add camera position
        // This is the inverse of worldToScreen: world = (screen - offset) / zoom + camera
        ScreenPoint cameraIsoPoint = IsometricMath.worldToScreen((int)this.worldX, (int)this.worldY, 0);
        
        double isoX = (screenX - offsetX) / zoom + cameraIsoPoint.x;
        double isoY = (screenY - offsetY) / zoom + cameraIsoPoint.y;
        
        // Convert from isometric to world coordinates
        return IsometricMath.screenToWorld((int)isoX, (int)isoY);
    }
    
    /**
     * Pan the camera to a new world position
     */
    public void panTo(double worldX, double worldY) {
        this.targetWorldX = worldX;
        this.targetWorldY = worldY;
    }
    
    /**
     * Pan the camera by a delta amount
     */
    public void panBy(double deltaX, double deltaY) {
        this.targetWorldX += deltaX; // Use += instead of setting to worldX + deltaX
        this.targetWorldY += deltaY;
    }
    
    /**
     * Immediately set camera position (no smoothing)
     */
    public void setPosition(double worldX, double worldY) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.targetWorldX = worldX;
        this.targetWorldY = worldY;
    }
    
    /**
     * Set zoom level
     */
    public void setZoom(double zoom) {
        this.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
    }
    
    /**
     * Zoom by a factor (e.g., 1.1 for 10% zoom in, 0.9 for 10% zoom out)
     */
    public void zoomBy(double factor) {
        setZoom(this.zoom * factor);
    }
    
    /**
     * Zoom towards a specific screen point (like mouse position)
     */
    public void zoomTowards(double newZoom, int screenX, int screenY) {
        // Get world position under mouse before zoom
        Position worldBeforeZoom = screenToWorld(screenX, screenY);
        
        // Apply zoom
        setZoom(newZoom);
        
        // Get world position under mouse after zoom
        Position worldAfterZoom = screenToWorld(screenX, screenY);
        
        // Adjust camera to keep the same world point under the mouse
        double deltaX = worldBeforeZoom.getX() - worldAfterZoom.getX();
        double deltaY = worldBeforeZoom.getY() - worldAfterZoom.getY();
        
        // Immediately move camera (no smooth interpolation for zoom)
        this.worldX += deltaX;
        this.worldY += deltaY;
        this.targetWorldX = this.worldX;
        this.targetWorldY = this.worldY;
    }
    
    /**
     * Update camera (for smooth movement and other animations)
     */
    public void update() {
        // Smooth camera movement towards target
        if (Math.abs(worldX - targetWorldX) > 0.01 || Math.abs(worldY - targetWorldY) > 0.01) {
            worldX += (targetWorldX - worldX) * panSpeed;
            worldY += (targetWorldY - worldY) * panSpeed;
        } else {
            // Snap to target if very close (prevents infinite interpolation)
            worldX = targetWorldX;
            worldY = targetWorldY;
        }
    }
    
    /**
     * Update viewport size (when window is resized)
     */
    public void setViewportSize(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.offsetX = width / 2;
        this.offsetY = height / 2;
    }
    
    /**
     * Check if a world position is visible in the current viewport
     */
    public boolean isVisible(int worldX, int worldY, double elevation) {
        ScreenPoint screen = worldToScreen(worldX, worldY, elevation);
        
        // Add some margin for tiles that are partially visible
        int margin = IsometricMath.TILE_WIDTH;
        
        return screen.x >= -margin && screen.x <= viewportWidth + margin &&
               screen.y >= -margin && screen.y <= viewportHeight + margin;
    }
    
    /**
     * Get the bounds of the visible world area
     * Returns the min/max world coordinates currently visible
     */
    public ViewBounds getVisibleBounds() {
        // Get corner screen positions
        Position topLeft = screenToWorld(0, 0);
        Position topRight = screenToWorld(viewportWidth, 0);
        Position bottomLeft = screenToWorld(0, viewportHeight);
        Position bottomRight = screenToWorld(viewportWidth, viewportHeight);
        
        // Find min/max world coordinates
        int minX = Math.min(Math.min(topLeft.getX(), topRight.getX()), 
                           Math.min(bottomLeft.getX(), bottomRight.getX()));
        int maxX = Math.max(Math.max(topLeft.getX(), topRight.getX()), 
                           Math.max(bottomLeft.getX(), bottomRight.getX()));
        int minY = Math.min(Math.min(topLeft.getY(), topRight.getY()), 
                           Math.min(bottomLeft.getY(), bottomRight.getY()));
        int maxY = Math.max(Math.max(topLeft.getY(), topRight.getY()), 
                           Math.max(bottomLeft.getY(), bottomRight.getY()));
        
        // Add padding for partially visible tiles
        int padding = 2;
        return new ViewBounds(minX - padding, minY - padding, maxX + padding, maxY + padding);
    }
    
    /**
     * Center camera on a specific world position
     */
    public void centerOn(Position worldPos) {
        panTo(worldPos.getX(), worldPos.getY());
    }
    
    /**
     * Center camera on a world area (fits the area in view)
     */
    public void centerOn(int minX, int minY, int maxX, int maxY) {
        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        panTo(centerX, centerY);
        
        // Optionally adjust zoom to fit the area
        // This would require calculating the appropriate zoom level
    }
    
    // Getters
    public double getWorldX() { return worldX; }
    public double getWorldY() { return worldY; }
    public double getZoom() { return zoom; }
    public int getViewportWidth() { return viewportWidth; }
    public int getViewportHeight() { return viewportHeight; }
    public double getMinZoom() { return MIN_ZOOM; }
    public double getMaxZoom() { return MAX_ZOOM; }
    
    // Setters for configuration
    public void setPanSpeed(double panSpeed) {
        this.panSpeed = Math.max(0.01, Math.min(1.0, panSpeed));
    }
    
    @Override
    public String toString() {
        return String.format("Camera2D{pos=(%.1f,%.1f), zoom=%.2f, viewport=%dx%d}",
            worldX, worldY, zoom, viewportWidth, viewportHeight);
    }
    
    /**
     * Represents the bounds of the visible world area
     */
    public static class ViewBounds {
        public final int minX, minY, maxX, maxY;
        
        public ViewBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
        
        public boolean contains(int x, int y) {
            return x >= minX && x <= maxX && y >= minY && y <= maxY;
        }
        
        @Override
        public String toString() {
            return String.format("ViewBounds{x=%d-%d, y=%d-%d}", minX, maxX, minY, maxY);
        }
    }
} 