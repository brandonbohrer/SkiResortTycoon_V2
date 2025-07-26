package com.skiresort.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Path2D; // Added for smooth mountain rendering
import java.util.ArrayList;
import java.util.List;

import com.skiresort.components.mountain.core.Mountain;
import com.skiresort.components.mountain.core.Slope;
import com.skiresort.components.mountain.core.Lift;
import com.skiresort.components.guests.core.GuestManager;
import com.skiresort.components.finances.core.FinanceManager;
import com.skiresort.graphics.Camera2D;
import com.skiresort.graphics.IsometricMath;
import com.skiresort.graphics.IsometricMath.ScreenPoint;
import com.skiresort.shared.Position;

/**
 * Main GUI for Ski Resort Tycoon
 * Uses Java Swing for maximum stability across all platforms
 * Provides isometric 3D-style graphics and full game functionality
 */
public class SkiResortTycoonGUI extends JFrame {
    
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    
    // Core game components
    private Mountain mountain;
    private GuestManager guestManager;
    private FinanceManager financeManager;
    
    // Graphics components
    private GamePanel gamePanel;
    private Camera2D camera;
    private Timer gameTimer;
    
    // Status components
    private JLabel statusLabel;
    
    // Construction system
    private enum ConstructionMode {
        NONE,           // Normal viewing mode
        BUILD_SLOPE,    // Building a slope
        BUILD_LIFT      // Building a lift
    }
    
    private ConstructionMode constructionMode = ConstructionMode.NONE;
    private Position constructionStart = null;  // First click position
    private Position constructionEnd = null;    // Second click position (for slopes/lifts)
    
    public SkiResortTycoonGUI() {
        super("🎿 Ski Resort Tycoon - Swing Edition");
        initializeGame();
        initializeUI();
        startGameLoop();
        
        System.out.println("✅ Swing GUI started successfully!");
    }
    
    /**
     * Initialize core game systems
     */
    private void initializeGame() {
        mountain = new Mountain("Swing Resort", 25, 25);
        guestManager = new GuestManager();
        financeManager = new FinanceManager();
        camera = new Camera2D(WINDOW_WIDTH, WINDOW_HEIGHT - 100); // Leave room for status
        
        // Start with empty mountain - player builds everything!
        // No demo infrastructure - true tycoon experience
        
        // Position camera so mountain is visible in the center of the screen
        // Instead of centering ON the mountain, position camera so mountain appears centered
        camera.setPosition(-6, -6); // Move camera up and left to bring mountain into view
        
        // DEBUG: Show where mountain center appears on screen
        Position mountainCenter = new Position(mountain.getWidth()/2, mountain.getHeight()/2);
        ScreenPoint centerScreen = camera.worldToScreen(mountainCenter, 0);
        System.out.printf("🏔️ Mountain center (%d,%d) appears at screen (%d,%d)%n", 
            mountainCenter.getX(), mountainCenter.getY(), centerScreen.x, centerScreen.y);
        System.out.printf("📏 Mountain bounds: (0,0) to (%d,%d)%n", mountain.getWidth()-1, mountain.getHeight()-1);
        System.out.printf("🎯 Click near screen center (%d,%d) for mountain coordinates%n", 
            WINDOW_WIDTH/2, (WINDOW_HEIGHT-100)/2);
        
        System.out.println("Game initialized: " + mountain.getName());
        System.out.println("Fresh mountain ready for development!");
    }
    

    
    /**
     * Initialize Swing UI
     */
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false); // Fixed size for stability
        setLocationRelativeTo(null); // Center on screen
        
        // Create main panel
        setLayout(new BorderLayout());
        
        // Status panel at top
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("🎿 Ski Resort Tycoon - Swing Edition | Loading...");
        statusPanel.add(statusLabel);
        statusPanel.setBackground(Color.LIGHT_GRAY);
        add(statusPanel, BorderLayout.NORTH);
        
        // Game panel in center
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);
        
        // Control panel at bottom
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.DARK_GRAY);
        
        // Construction buttons on the left
        JPanel constructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        constructionPanel.setBackground(Color.DARK_GRAY);
        
        JButton buildSlopeButton = new JButton("🛷 Build Slope ($5000)");
        buildSlopeButton.setForeground(Color.WHITE);
        buildSlopeButton.setBackground(Color.DARK_GRAY);
        buildSlopeButton.addActionListener(e -> toggleConstructionMode(ConstructionMode.BUILD_SLOPE));
        constructionPanel.add(buildSlopeButton);
        
        JButton buildLiftButton = new JButton("🚠 Build Lift ($10000)");
        buildLiftButton.setForeground(Color.WHITE);  
        buildLiftButton.setBackground(Color.DARK_GRAY);
        buildLiftButton.addActionListener(e -> toggleConstructionMode(ConstructionMode.BUILD_LIFT));
        constructionPanel.add(buildLiftButton);
        
        JButton cancelButton = new JButton("❌ Cancel");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(Color.DARK_GRAY);
        cancelButton.addActionListener(e -> cancelConstruction());
        constructionPanel.add(cancelButton);
        
        controlPanel.add(constructionPanel, BorderLayout.WEST);
        
        // Controls info on the right
        JLabel controlsLabel = new JLabel("🎮 Controls: Drag to pan | Mouse wheel to zoom | Click to build");
        controlsLabel.setForeground(Color.WHITE);
        controlPanel.add(controlsLabel, BorderLayout.EAST);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        System.out.println("Swing UI initialized");
    }
    
    /**
     * Start the game loop using Swing Timer (thread-safe)
     */
    private void startGameLoop() {
        gameTimer = new Timer(33, e -> { // ~30 FPS
            update();
            gamePanel.repaint();
        });
        gameTimer.start();
        
        System.out.println("Game loop started (30 FPS)");
    }
    
    /**
     * Update game state
     */
    private void update() {
        camera.update();
        
        // Update guests occasionally
        if (System.currentTimeMillis() % 2000 < 33) {
            guestManager.update(mountain);
        }
        
        // Update status
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(String.format(
                "🎿 Ski Resort Tycoon | Guests: %d | Money: $%.0f | Camera: (%.1f, %.1f) Zoom: %.2f",
                guestManager.getActiveGuests().size(),
                financeManager.getCurrentMoney(),
                camera.getWorldX(), camera.getWorldY(), camera.getZoom()
            ));
        });
    }
    
    /**
     * Toggle construction mode
     */
    private void toggleConstructionMode(ConstructionMode mode) {
        if (constructionMode == mode) {
            // Toggle off if already in this mode
            cancelConstruction();
        } else {
            constructionMode = mode;
            constructionStart = null;
            constructionEnd = null;
            System.out.println("Construction mode: " + mode);
        }
    }
    
    /**
     * Cancel current construction
     */
    private void cancelConstruction() {
        constructionMode = ConstructionMode.NONE;
        constructionStart = null;
        constructionEnd = null;
        System.out.println("Construction cancelled");
    }
    
    /**
     * Handle construction clicks
     */
    private void handleConstructionClick(Position worldPos) {
        switch (constructionMode) {
            case BUILD_SLOPE:
                handleSlopeConstruction(worldPos);
                break;
            case BUILD_LIFT:
                handleLiftConstruction(worldPos);
                break;
            default:
                break;
        }
    }
    
    /**
     * Handle slope construction (two clicks: start and end)
     */
    private void handleSlopeConstruction(Position worldPos) {
        if (constructionStart == null) {
            // First click - set start position
            constructionStart = worldPos;
            System.out.println("Slope start set at: " + worldPos);
        } else {
            // Second click - build the slope
            constructionEnd = worldPos;
            buildSlope(constructionStart, constructionEnd);
            
            // Reset for next slope
            constructionStart = null;
            constructionEnd = null;
        }
    }
    
    /**
     * Handle lift construction (two clicks: bottom and top)
     */
    private void handleLiftConstruction(Position worldPos) {
        if (constructionStart == null) {
            // First click - set bottom position
            constructionStart = worldPos;
            System.out.println("Lift bottom set at: " + worldPos);
        } else {
            // Second click - build the lift
            constructionEnd = worldPos;
            buildLift(constructionStart, constructionEnd);
            
            // Reset for next lift
            constructionStart = null;
            constructionEnd = null;
        }
    }
    
    /**
     * Build a slope with cost validation
     */
    private void buildSlope(Position start, Position end) {
        double cost = 5000.0; // Basic slope cost
        
        if (!financeManager.canAfford(cost)) {
            System.out.println("❌ Cannot afford slope! Need $" + String.format("%.0f", cost));
            return;
        }
        
        Slope newSlope = new Slope("Slope " + (mountain.getSlopes().size() + 1), 
            start, end, Slope.Difficulty.BEGINNER, cost);
        
        if (mountain.addSlope(newSlope)) {
            financeManager.makePurchase(cost, "Built " + newSlope.getName(), 
                com.skiresort.components.finances.core.ExpenseCategory.FACILITY_CONSTRUCTION);
            System.out.println("✅ Built " + newSlope.getName() + " for $" + String.format("%.0f", cost));
        } else {
            System.out.println("❌ Invalid slope location (must go downhill)");
        }
    }
    
    /**
     * Build a lift with cost validation
     */
    private void buildLift(Position bottom, Position top) {
        double cost = 10000.0; // Basic lift cost
        
        if (!financeManager.canAfford(cost)) {
            System.out.println("❌ Cannot afford lift! Need $" + String.format("%.0f", cost));
            return;
        }
        
        Lift newLift = new Lift("Lift " + (mountain.getLifts().size() + 1), 
            bottom, top, Lift.LiftType.MAGIC_CARPET);
        
        if (mountain.addLift(newLift)) {
            financeManager.makePurchase(cost, "Built " + newLift.getName(), 
                com.skiresort.components.finances.core.ExpenseCategory.EQUIPMENT_PURCHASE);
            System.out.println("✅ Built " + newLift.getName() + " for $" + String.format("%.0f", cost));
        } else {
            System.out.println("❌ Invalid lift location (must go uphill)");
        }
    }
    
    /**
     * Custom JPanel for game rendering
     */
    private class GamePanel extends JPanel {
        private BufferedImage offscreenBuffer;
        private Graphics2D offscreenGraphics;
        
        // Mouse interaction
        private int lastMouseX, lastMouseY;
        private int pressedMouseX, pressedMouseY;  // Track where mouse was pressed
        private boolean isDragging = false;
        private static final int CLICK_TOLERANCE = 5; // Maximum pixels to still count as click
        
        public GamePanel() {
            setBackground(new Color(173, 216, 230)); // Light blue
            setupMouseControls();
        }
        
        /**
         * Setup mouse controls for camera
         */
        private void setupMouseControls() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    pressedMouseX = e.getX();
                    pressedMouseY = e.getY();
                    isDragging = false; // Reset dragging - will be set to true if mouse moves
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    // Check if this was a click (small movement) or a drag
                    int deltaX = Math.abs(e.getX() - pressedMouseX);
                    int deltaY = Math.abs(e.getY() - pressedMouseY);
                    
                    if (deltaX <= CLICK_TOLERANCE && deltaY <= CLICK_TOLERANCE) {
                        // This was a click, not a drag
                        handleMouseClick(e);
                    }
                    
                    isDragging = false;
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // Check if we've moved enough to be considered dragging
                    int deltaX = Math.abs(e.getX() - pressedMouseX);
                    int deltaY = Math.abs(e.getY() - pressedMouseY);
                    
                    if (deltaX > CLICK_TOLERANCE || deltaY > CLICK_TOLERANCE) {
                        isDragging = true;
                    }
                    
                    if (isDragging) {
                        double moveDeltaX = e.getX() - lastMouseX;
                        double moveDeltaY = e.getY() - lastMouseY;
                        
                        // Convert to world movement with much higher sensitivity for faster panning
                        double worldDeltaX = -moveDeltaX / (camera.getZoom() * IsometricMath.TILE_WIDTH/8); // 4x faster
                        double worldDeltaY = -moveDeltaY / (camera.getZoom() * IsometricMath.TILE_HEIGHT/8); // 4x faster
                        
                        camera.panBy(worldDeltaX, worldDeltaY);
                        
                        lastMouseX = e.getX();
                        lastMouseY = e.getY();
                    }
                }
            });
            
            addMouseWheelListener(e -> {
                // Much smaller zoom increments for gradual, smooth zooming (reduced by 500%)
                double zoomFactor = e.getWheelRotation() > 0 ? 0.96 : 1.04; // 4% steps instead of 20%/25% 
                camera.zoomTowards(camera.getZoom() * zoomFactor, e.getX(), e.getY());
            });
        }
        
        /**
         * Handle mouse click (not drag)
         */
        private void handleMouseClick(MouseEvent e) {
            Position worldPos = camera.screenToWorld(e.getX(), e.getY());
            double elevation = getElevationSafe(worldPos.getX(), worldPos.getY());
            
            // DEBUG: Show the conversion chain
            System.out.println(String.format("CLICK DEBUG: Screen(%d,%d) -> World(%d,%d)", 
                e.getX(), e.getY(), worldPos.getX(), worldPos.getY()));
            
            if (constructionMode == ConstructionMode.NONE) {
                // Debug mode - show coordinates
                System.out.println(String.format("Clicked: Screen(%d,%d) -> World(%d,%d) Elevation=%.1f", 
                    e.getX(), e.getY(), worldPos.getX(), worldPos.getY(), elevation));
            } else {
                // Construction mode - handle building
                handleConstructionClick(worldPos);
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Create offscreen buffer if needed
            if (offscreenBuffer == null || 
                offscreenBuffer.getWidth() != getWidth() || 
                offscreenBuffer.getHeight() != getHeight()) {
                
                offscreenBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                offscreenGraphics = offscreenBuffer.createGraphics();
                offscreenGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            
            // Clear offscreen buffer
            offscreenGraphics.setColor(new Color(173, 216, 230)); // Light blue
            offscreenGraphics.fillRect(0, 0, getWidth(), getHeight());
            
            // Render game world
            renderMountain(offscreenGraphics);
            renderSlopes(offscreenGraphics);
            renderLifts(offscreenGraphics);
            renderGuests(offscreenGraphics);
            renderUI(offscreenGraphics);
            
            // Draw offscreen buffer to screen
            g.drawImage(offscreenBuffer, 0, 0, null);
        }
        
        /**
         * Render the entire mountain as a smooth 3D isometric surface
         */
        private void renderMountain(Graphics2D g) {
            // Enable anti-aliasing for smooth rendering
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate visible mountain area
            Camera2D.ViewBounds bounds = camera.getVisibleBounds();
            
            // Render mountain as 3D surface patches
            for (int y = Math.max(0, bounds.minY); y < Math.min(mountain.getHeight() - 1, bounds.maxY); y++) {
                for (int x = Math.max(0, bounds.minX); x < Math.min(mountain.getWidth() - 1, bounds.maxX); x++) {
                    renderMountainPatch(g, x, y);
                }
            }
            
            // Add subtle animated snow shimmer on top
            addSnowShimmer(g);
            
            // Add rock outcrops as 3D elements
            addRockOutcrops(g);
        }
        
        /**
         * Render a single mountain patch as a 3D quad with proper shading
         */
        private void renderMountainPatch(Graphics2D g, int worldX, int worldY) {
            // Get elevations for this patch and neighbors
            double elevation = getElevationSafe(worldX, worldY);
            double elevationRight = getElevationSafe(worldX + 1, worldY);
            double elevationDown = getElevationSafe(worldX, worldY + 1);
            double elevationDiag = getElevationSafe(worldX + 1, worldY + 1);
            
            // Skip if all elevations are too low (below snow line)
            if (elevation < 1 && elevationRight < 1 && elevationDown < 1 && elevationDiag < 1) {
                return;
            }
            
            // Calculate 3D corners of this patch
            ScreenPoint topLeft = camera.worldToScreen(worldX, worldY, elevation);
            ScreenPoint topRight = camera.worldToScreen(worldX + 1, worldY, elevationRight);
            ScreenPoint bottomLeft = camera.worldToScreen(worldX, worldY + 1, elevationDown);
            ScreenPoint bottomRight = camera.worldToScreen(worldX + 1, worldY + 1, elevationDiag);
            
            // Create quad polygon
            int[] xPoints = {topLeft.x, topRight.x, bottomRight.x, bottomLeft.x};
            int[] yPoints = {topLeft.y, topRight.y, bottomRight.y, bottomLeft.y};
            
            // Calculate average elevation for this patch
            double avgElevation = (elevation + elevationRight + elevationDown + elevationDiag) / 4.0;
            
            // Calculate surface normal for lighting (simplified)
            double slopeX = (elevationRight + elevationDiag) - (elevation + elevationDown);
            double slopeY = (elevationDown + elevationDiag) - (elevation + elevationRight);
            
            // Calculate lighting based on surface normal (simulated sunlight from top-left)
            double lightIntensity = Math.max(0.3, 1.0 - (slopeX * 0.1 + slopeY * 0.1));
            lightIntensity = Math.min(1.0, lightIntensity);
            
            // Base white color with lighting - ensure values stay in valid range
            int baseWhite = 250;
            int litWhite = (int)(baseWhite * lightIntensity);
            litWhite = Math.max(200, Math.min(255, litWhite)); // Clamp to valid range
            
            // Add some variation based on elevation
            if (avgElevation > 30) {
                litWhite = Math.max(litWhite - 10, 200); // Ensure minimum value
            }
            
            Color patchColor = new Color(litWhite, litWhite, litWhite);
            
            // Check for rock outcrops
            int rockSeed = (worldX * 17 + worldY * 23) % 100;
            boolean hasRock = (avgElevation > 20 && avgElevation < 60 && rockSeed < 8) || 
                             (avgElevation > 40 && rockSeed < 4);
            
            if (hasRock) {
                // Rocky patches are darker - ensure all values stay in valid range
                Color rockColor = avgElevation > 40 ? new Color(140, 140, 130) : new Color(120, 100, 80);
                int rockR = (int)(rockColor.getRed() * lightIntensity);
                int rockG = (int)(rockColor.getGreen() * lightIntensity);
                int rockB = (int)(rockColor.getBlue() * lightIntensity);
                
                // Clamp all color values to valid range [0, 255]
                rockR = Math.max(0, Math.min(255, rockR));
                rockG = Math.max(0, Math.min(255, rockG));
                rockB = Math.max(0, Math.min(255, rockB));
                
                patchColor = new Color(rockR, rockG, rockB);
            }
            
            // Fill the patch
            g.setColor(patchColor);
            g.fillPolygon(xPoints, yPoints, 4);
            
            // Add subtle edge highlighting for 3D effect - ensure valid color ranges
            int highlightR = Math.min(255, patchColor.getRed() + 15);
            int highlightG = Math.min(255, patchColor.getGreen() + 15);
            int highlightB = Math.min(255, patchColor.getBlue() + 15);
            
            g.setColor(new Color(highlightR, highlightG, highlightB));
            g.setStroke(new BasicStroke(0.5f));
            
            // Draw top and left edges lighter (highlight)
            g.drawLine(topLeft.x, topLeft.y, topRight.x, topRight.y); // Top edge
            g.drawLine(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y); // Left edge
            
            // Draw bottom and right edges darker (shadow) - ensure valid color ranges
            int shadowR = Math.max(0, patchColor.getRed() - 25);
            int shadowG = Math.max(0, patchColor.getGreen() - 25);
            int shadowB = Math.max(0, patchColor.getBlue() - 25);
            
            g.setColor(new Color(shadowR, shadowG, shadowB));
            g.drawLine(topRight.x, topRight.y, bottomRight.x, bottomRight.y); // Right edge
            g.drawLine(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y); // Bottom edge
        }
        
        /**
         * Add animated snow shimmer effect
         */
        private void addSnowShimmer(Graphics2D g) {
            // Use game time for animation
            long time = System.currentTimeMillis();
            
            // Create shimmer points across the mountain
            g.setColor(new Color(255, 255, 255, 100));
            
            for (int i = 0; i < 50; i++) {
                // Deterministic but animated shimmer positions
                double shimmerX = (Math.sin(time * 0.001 + i * 0.3) * 0.5 + 0.5) * getWidth();
                double shimmerY = (Math.sin(time * 0.0008 + i * 0.7) * 0.5 + 0.5) * getHeight() * 0.7; // Upper 70% only
                
                int size = (int)(Math.sin(time * 0.002 + i) * 2 + 3);
                g.fillOval((int)shimmerX, (int)shimmerY, size, size);
            }
        }
        
        /**
         * Add smooth rock outcrops
         */
        private void addRockOutcrops(Graphics2D g) {
            // Create smooth rock shapes at specific mountain positions
            Camera2D.ViewBounds bounds = camera.getVisibleBounds();
            
            for (int y = Math.max(0, bounds.minY); y <= Math.min(mountain.getHeight() - 1, bounds.maxY); y += 3) {
                for (int x = Math.max(0, bounds.minX); x <= Math.min(mountain.getWidth() - 1, bounds.maxX); x += 3) {
                    double elevation = getElevationSafe(x, y);
                    
                    // Rock logic (same as before but smoother)
                    int rockSeed = (x * 17 + y * 23) % 100;
                    boolean hasRock = (elevation > 20 && elevation < 60 && rockSeed < 10) || 
                                     (elevation > 40 && rockSeed < 5);
                    
                    if (hasRock) {
                        ScreenPoint rockCenter = camera.worldToScreen(x, y, elevation + 2);
                        
                        // Create smooth rock shape
                        int rockSize = (int)(15 * camera.getZoom());
                        Color rockColor = elevation > 40 ? new Color(140, 140, 130) : new Color(120, 100, 80);
                        
                        g.setColor(rockColor);
                        g.fillOval(rockCenter.x - rockSize/2, rockCenter.y - rockSize/2, rockSize, rockSize);
                        
                        // Add rock highlight
                        g.setColor(new Color(rockColor.getRed() + 20, rockColor.getGreen() + 20, rockColor.getBlue() + 20));
                        g.fillOval(rockCenter.x - rockSize/3, rockCenter.y - rockSize/3, rockSize/2, rockSize/2);
                    }
                }
            }
        }
        
        /**
         * Render slopes with enhanced graphics and grooming patterns
         */
        private void renderSlopes(Graphics2D g) {
            for (Slope slope : mountain.getSlopes()) {
                Position start = slope.getStartPosition();
                Position end = slope.getEndPosition();
                
                double startElevation = getElevationSafe(start.getX(), start.getY());
                double endElevation = getElevationSafe(end.getX(), end.getY());
                
                ScreenPoint startScreen = camera.worldToScreen(start, startElevation);
                ScreenPoint endScreen = camera.worldToScreen(end, endElevation);
                
                // Enhanced colors based on difficulty with transparency
                Color slopeColor = switch (slope.getDifficulty()) {
                    case BEGINNER -> new Color(34, 139, 34, 200);    // Forest green
                    case INTERMEDIATE -> new Color(30, 144, 255, 200); // Dodger blue
                    case ADVANCED -> new Color(0, 0, 0, 220);         // Black
                    case EXPERT -> new Color(220, 20, 60, 200);       // Crimson
                    default -> new Color(128, 128, 128, 200);         // Gray fallback
                };
                
                // Draw wider slope path with gradient effect
                float strokeWidth = Math.max(2.0f, 8.0f * (float)camera.getZoom());
                g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Draw shadow/outline first
                g.setColor(new Color(0, 0, 0, 100));
                g.drawLine(startScreen.x + 1, startScreen.y + 1, endScreen.x + 1, endScreen.y + 1);
                
                // Draw main slope
                g.setColor(slopeColor);
                g.drawLine(startScreen.x, startScreen.y, endScreen.x, endScreen.y);
                
                // Add grooming pattern if zoomed in enough
                if (camera.getZoom() > 0.4) {
                    g.setStroke(new BasicStroke(1.0f * (float)camera.getZoom(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.setColor(new Color(slopeColor.getRed(), slopeColor.getGreen(), slopeColor.getBlue(), 150));
                    
                    // Draw grooming lines perpendicular to slope
                    int segments = (int)(camera.getZoom() * 5);
                    for (int i = 1; i < segments; i++) {
                        float ratio = (float)i / segments;
                        int x = (int)(startScreen.x + ratio * (endScreen.x - startScreen.x));
                        int y = (int)(startScreen.y + ratio * (endScreen.y - startScreen.y));
                        
                        // Calculate perpendicular offset
                        int dx = endScreen.y - startScreen.y;
                        int dy = startScreen.x - endScreen.x;
                        int length = (int)Math.sqrt(dx*dx + dy*dy);
                        if (length > 0) {
                            dx = dx * 3 / length;
                            dy = dy * 3 / length;
                            g.drawLine(x - dx, y - dy, x + dx, y + dy);
                        }
                    }
                }
                
                // Draw slope markers and name if zoomed in
                if (camera.getZoom() > 0.3) {
                    // Draw difficulty markers at start
                    int markerSize = (int)(12 * camera.getZoom());
                    g.setColor(slopeColor);
                    g.fillOval(startScreen.x - markerSize/2, startScreen.y - markerSize/2, markerSize, markerSize);
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(2.0f));
                    g.drawOval(startScreen.x - markerSize/2, startScreen.y - markerSize/2, markerSize, markerSize);
                    
                    // Draw difficulty symbol
                    String difficultySymbol = switch (slope.getDifficulty()) {
                        case BEGINNER -> "●";      // Circle
                        case INTERMEDIATE -> "■";   // Square  
                        case ADVANCED -> "♦";      // Diamond
                        case EXPERT -> "♦♦";       // Double diamond
                        default -> "?";            // Unknown difficulty
                    };
                    
                    g.setColor(Color.WHITE);
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(10 * camera.getZoom())));
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(difficultySymbol);
                    int textHeight = fm.getAscent();
                    g.drawString(difficultySymbol, 
                        startScreen.x - textWidth/2, 
                        startScreen.y + textHeight/4);
                }
                
                // Draw slope name if zoomed in more
                if (camera.getZoom() > 0.6) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(12 * camera.getZoom())));
                    
                    // Draw text with shadow
                    int textX = (startScreen.x + endScreen.x) / 2;
                    int textY = (startScreen.y + endScreen.y) / 2 - 10;
                    
                    g.setColor(Color.BLACK);
                    g.drawString(slope.getName(), textX + 1, textY + 1);
                    g.setColor(Color.WHITE);
                    g.drawString(slope.getName(), textX, textY);
                }
            }
        }
        
        /**
         * Render lifts with detailed stations, towers, and cables
         */
        private void renderLifts(Graphics2D g) {
            for (Lift lift : mountain.getLifts()) {
                Position bottom = lift.getBottomPosition();
                Position top = lift.getTopPosition();
                
                double bottomElevation = getElevationSafe(bottom.getX(), bottom.getY());
                double topElevation = getElevationSafe(top.getX(), top.getY());
                
                ScreenPoint bottomScreen = camera.worldToScreen(bottom, bottomElevation);
                ScreenPoint topScreen = camera.worldToScreen(top, topElevation);
                
                // Colors based on lift type
                Color liftColor = switch (lift.getType()) {
                    case CHAIRLIFT -> new Color(105, 105, 105);      // Dark gray
                    case GONDOLA -> new Color(220, 20, 60);          // Crimson  
                    case T_BAR -> new Color(139, 69, 19);            // Saddle brown
                    case MAGIC_CARPET -> new Color(255, 140, 0);     // Dark orange
                    default -> new Color(105, 105, 105);             // Default gray
                };
                
                // Draw main cable with shadow
                float cableWidth = Math.max(1.5f, 3.0f * (float)camera.getZoom());
                g.setStroke(new BasicStroke(cableWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Cable shadow
                g.setColor(new Color(0, 0, 0, 100));
                g.drawLine(bottomScreen.x + 1, bottomScreen.y + 2, topScreen.x + 1, topScreen.y + 2);
                
                // Main cable
                g.setColor(liftColor);
                g.drawLine(bottomScreen.x, bottomScreen.y, topScreen.x, topScreen.y);
                
                // Draw support towers if lift is long enough and zoomed in
                if (camera.getZoom() > 0.3) {
                    double distance = Math.sqrt(Math.pow(topScreen.x - bottomScreen.x, 2) + 
                                              Math.pow(topScreen.y - bottomScreen.y, 2));
                    
                    if (distance > 100) { // Add towers for longer lifts
                        int numTowers = (int)(distance / 80); // Tower every ~80 pixels
                        
                        for (int i = 1; i <= numTowers; i++) {
                            float ratio = (float)i / (numTowers + 1);
                            int towerX = (int)(bottomScreen.x + ratio * (topScreen.x - bottomScreen.x));
                            int towerY = (int)(bottomScreen.y + ratio * (topScreen.y - bottomScreen.y));
                            
                            // Get elevation at tower position for height
                            int worldTowerX = (int)(bottom.getX() + ratio * (top.getX() - bottom.getX()));
                            int worldTowerY = (int)(bottom.getY() + ratio * (top.getY() - bottom.getY()));
                            double towerElevation = getElevationSafe(worldTowerX, worldTowerY);
                            
                            // Draw tower
                            int towerHeight = (int)(15 * camera.getZoom());
                            g.setColor(new Color(139, 69, 19)); // Brown tower
                            g.setStroke(new BasicStroke(Math.max(2.0f, 4.0f * (float)camera.getZoom())));
                            g.drawLine(towerX, towerY, towerX, towerY + towerHeight);
                            
                            // Tower top
                            int capSize = (int)(4 * camera.getZoom());
                            g.fillRect(towerX - capSize/2, towerY - capSize/2, capSize, capSize);
                        }
                    }
                }
                
                // Draw enhanced stations
                int stationSize = (int)(Math.max(8, 16 * camera.getZoom()));
                
                // Bottom station
                drawLiftStation(g, bottomScreen.x, bottomScreen.y, stationSize, liftColor, lift.getType(), true);
                
                // Top station  
                drawLiftStation(g, topScreen.x, topScreen.y, stationSize, liftColor, lift.getType(), false);
                
                // Draw chairs/gondolas if zoomed in enough
                if (camera.getZoom() > 0.5 && lift.getType() == Lift.LiftType.CHAIRLIFT) {
                    int numChairs = Math.max(3, (int)(camera.getZoom() * 6));
                    for (int i = 0; i < numChairs; i++) {
                        float ratio = (float)i / numChairs;
                        int chairX = (int)(bottomScreen.x + ratio * (topScreen.x - bottomScreen.x));
                        int chairY = (int)(bottomScreen.y + ratio * (topScreen.y - bottomScreen.y)) + 8;
                        
                        // Draw chair
                        g.setColor(new Color(139, 69, 19));
                        int chairSize = (int)(3 * camera.getZoom());
                        g.fillRect(chairX - chairSize, chairY, chairSize * 2, chairSize);
                        
                        // Chair cable
                        g.setStroke(new BasicStroke(1.0f));
                        g.setColor(Color.DARK_GRAY);
                        g.drawLine(chairX, chairY - (int)(3 * camera.getZoom()), chairX, chairY);
                    }
                }
                
                // Draw lift name with enhanced styling
                if (camera.getZoom() > 0.4) {
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(11 * camera.getZoom())));
                    int textX = (bottomScreen.x + topScreen.x) / 2;
                    int textY = (bottomScreen.y + topScreen.y) / 2 - (int)(15 * camera.getZoom());
                    
                    // Text shadow
                    g.setColor(Color.BLACK);
                    g.drawString(lift.getName(), textX + 1, textY + 1);
                    
                    // Main text
                    g.setColor(Color.WHITE);
                    g.drawString(lift.getName(), textX, textY);
                }
            }
        }
        
        /**
         * Draw detailed lift station
         */
        private void drawLiftStation(Graphics2D g, int x, int y, int size, Color liftColor, Lift.LiftType type, boolean isBottom) {
            // Station building
            g.setColor(new Color(139, 69, 19)); // Brown building
            g.fillRect(x - size/2, y - size/4, size, size/2);
            
            // Building outline
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(x - size/2, y - size/4, size, size/2);
            
            // Station platform based on type
            g.setColor(liftColor);
            if (type == Lift.LiftType.CHAIRLIFT) {
                // Loading platform
                g.fillRect(x - size/3, y - size/6, size*2/3, size/8);
            } else if (type == Lift.LiftType.GONDOLA) {
                // Gondola station
                g.fillOval(x - size/3, y - size/4, size*2/3, size/2);
            } else if (type == Lift.LiftType.MAGIC_CARPET) {
                // Magic carpet platform
                g.fillRect(x - size/2, y, size, size/6);
            }
            
            // Station details
            g.setColor(Color.WHITE);
            int windowSize = Math.max(2, size/8);
            g.fillRect(x - size/4, y - size/8, windowSize, windowSize); // Window
            
            // Station type indicator
            String indicator = switch (type) {
                case CHAIRLIFT -> "🪑";
                case GONDOLA -> "🚡"; 
                case T_BAR -> "T";
                case MAGIC_CARPET -> "🎿";
                default -> "L";
            };
            
            if (size > 12) { // Only show if big enough
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size/3));
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(indicator);
                g.setColor(Color.BLACK);
                g.drawString(indicator, x - textWidth/2, y + size/8);
            }
        }
        
        /**
         * Render guests
         */
        private void renderGuests(Graphics2D g) {
            if (guestManager.getActiveGuests().isEmpty()) return;
            
            g.setColor(Color.YELLOW);
            int guestSize = (int)(6 * camera.getZoom());
            
            for (var guest : guestManager.getActiveGuests()) {
                if (guest.getCurrentPosition() != null) {
                    Position pos = guest.getCurrentPosition();
                    double elevation = getElevationSafe(pos.getX(), pos.getY());
                    ScreenPoint screen = camera.worldToScreen(pos, elevation);
                    
                    g.fillOval(screen.x - guestSize/2, screen.y - guestSize/2, guestSize, guestSize);
                    g.setColor(Color.BLACK);
                    g.drawOval(screen.x - guestSize/2, screen.y - guestSize/2, guestSize, guestSize);
                    g.setColor(Color.YELLOW);
                }
            }
        }
        
        /**
         * Render UI overlay
         */
        private void renderUI(Graphics2D g) {
            g.setColor(Color.BLACK);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            
            g.drawString("✅ Swing GUI - Rock Solid on macOS!", 10, 20);
            g.drawString(String.format("🏔️ %s (%dx%d)", 
                mountain.getName(), mountain.getWidth(), mountain.getHeight()), 10, 40);
            
            // Show infrastructure or encourage building
            if (mountain.getSlopes().isEmpty() && mountain.getLifts().isEmpty()) {
                g.drawString("🏗️ Ready to build your ski resort empire!", 10, 60);
            } else {
                g.drawString(String.format("🏗️ Infrastructure: %d slopes, %d lifts", 
                    mountain.getSlopes().size(), mountain.getLifts().size()), 10, 60);
            }
            
            // Show construction mode status
            if (constructionMode != ConstructionMode.NONE) {
                g.setColor(Color.BLUE);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                String modeText = switch (constructionMode) {
                    case BUILD_SLOPE -> constructionStart == null ? 
                        "🛷 Click start point for slope" : "🛷 Click end point for slope";
                    case BUILD_LIFT -> constructionStart == null ? 
                        "🚠 Click bottom point for lift" : "🚠 Click top point for lift";
                    default -> "";
                };
                g.drawString(modeText, 10, 90);
                
                // Show construction preview if we have start point
                if (constructionStart != null) {
                    g.setColor(Color.YELLOW);
                    double elevation = getElevationSafe(constructionStart.getX(), constructionStart.getY());
                    ScreenPoint startScreen = camera.worldToScreen(constructionStart, elevation);
                    g.fillOval(startScreen.x - 5, startScreen.y - 5, 10, 10);
                }
            }
        }
    }
    
    /**
     * Helper methods
     */
    private double getElevationSafe(int x, int y) {
        try {
            if (x >= 0 && x < mountain.getWidth() && y >= 0 && y < mountain.getHeight()) {
                return mountain.getElevationAt(x, y);
            }
        } catch (Exception e) {
            // Ignore
        }
        return 0.0;
    }
    
    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < mountain.getWidth() && y >= 0 && y < mountain.getHeight();
    }
    
    public static void main(String[] args) {
        System.out.println("🎿 Starting Ski Resort Tycoon - Swing Edition!");
        System.out.println("This version uses Java Swing - 100% stable on macOS!");
        
        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Using default look and feel");
        }
        
        SwingUtilities.invokeLater(() -> {
            new SkiResortTycoonGUI().setVisible(true);
        });
    }
} 