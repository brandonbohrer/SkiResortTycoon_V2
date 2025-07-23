package com.skiresort.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

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
 * Main JavaFX Application for Ski Resort Tycoon
 * Features 2D isometric graphics with the illusion of 3D
 * Fixed camera angle, pannable and zoomable viewport
 */
public class SkiResortTycoonGUI extends Application {
    
    // Window dimensions
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    // Core game components
    private Mountain mountain;
    private GuestManager guestManager;
    private FinanceManager financeManager;
    
    // Graphics components
    private Canvas canvas;
    private GraphicsContext gc;
    private Camera2D camera;
    
    // Mouse interaction
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private boolean isDragging = false;
    
    @Override
    public void start(Stage primaryStage) {
        initializeGame();
        initializeGraphics();
        setupUI(primaryStage);
        startGameLoop();
    }
    
    /**
     * Initialize core game systems
     */
    private void initializeGame() {
        // Create a sample resort with some features
        mountain = new Mountain("Demo Resort", 30, 30);
        guestManager = new GuestManager();
        financeManager = new FinanceManager();
        
        // Add some demo slopes and lifts
        addDemoInfrastructure();
        
        System.out.println("Game initialized: " + mountain.getName());
        System.out.println("Mountain size: " + mountain.getWidth() + "x" + mountain.getHeight());
        System.out.println("Slopes: " + mountain.getSlopes().size());
        System.out.println("Lifts: " + mountain.getLifts().size());
    }
    
    /**
     * Add some demo infrastructure to showcase the graphics
     */
    private void addDemoInfrastructure() {
        // Add a beginner slope
        Slope beginnerSlope = new Slope("Bunny Hill", 
            new Position(15, 8), new Position(15, 22), 
            Slope.Difficulty.BEGINNER, 5000);
        mountain.addSlope(beginnerSlope);
        
        // Add a magic carpet lift
        Lift magicCarpet = new Lift("Magic Carpet", 
            new Position(15, 22), new Position(15, 10), 
            Lift.LiftType.MAGIC_CARPET);
        mountain.addLift(magicCarpet);
        
        // Add an intermediate slope
        Slope intermediateSlope = new Slope("Mountain Run", 
            new Position(10, 5), new Position(18, 25), 
            Slope.Difficulty.INTERMEDIATE, 12000);
        mountain.addSlope(intermediateSlope);
        
        // Add a chairlift
        Lift chairlift = new Lift("Summit Express", 
            new Position(12, 25), new Position(8, 8), 
            Lift.LiftType.CHAIRLIFT);
        mountain.addLift(chairlift);
    }
    
    /**
     * Initialize graphics components
     */
    private void initializeGraphics() {
        canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        camera = new Camera2D(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Center camera on the mountain
        camera.centerOn(new Position(mountain.getWidth()/2, mountain.getHeight()/2));
        
        System.out.println("Graphics initialized: " + camera);
    }
    
    /**
     * Set up the UI and window
     */
    private void setupUI(Stage primaryStage) {
        Pane root = new Pane();
        root.getChildren().add(canvas);
        
        // Set up mouse controls
        setupMouseControls();
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        primaryStage.setTitle("Ski Resort Tycoon - Isometric View Demo");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Keep it simple for now
        primaryStage.show();
        
        System.out.println("Window created and shown");
    }
    
    /**
     * Set up mouse controls for camera manipulation
     */
    private void setupMouseControls() {
        // Mouse drag for panning
        canvas.setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            isDragging = true;
        });
        
        canvas.setOnMouseDragged(event -> {
            if (isDragging) {
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;
                
                // Convert screen movement to world movement
                // Invert and scale based on zoom level
                double worldDeltaX = -deltaX / (camera.getZoom() * IsometricMath.TILE_WIDTH/2);
                double worldDeltaY = -deltaY / (camera.getZoom() * IsometricMath.TILE_HEIGHT/2);
                
                camera.panBy(worldDeltaX, worldDeltaY);
                
                lastMouseX = event.getX();
                lastMouseY = event.getY();
            }
        });
        
        canvas.setOnMouseReleased(event -> {
            isDragging = false;
        });
        
        // Mouse wheel for zooming
        canvas.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            camera.zoomTowards(zoomFactor, (int)event.getX(), (int)event.getY());
        });
        
        // Click to show world coordinates (debug feature)
        canvas.setOnMouseClicked(event -> {
            Position worldPos = camera.screenToWorld((int)event.getX(), (int)event.getY());
            double elevation = getElevationSafe(worldPos.getX(), worldPos.getY());
            System.out.println(String.format("Clicked: Screen(%.0f,%.0f) -> World(%d,%d) Elevation=%.1f", 
                event.getX(), event.getY(), worldPos.getX(), worldPos.getY(), elevation));
        });
    }
    
    /**
     * Start the game loop for continuous rendering
     */
    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
        
        System.out.println("Game loop started");
    }
    
    /**
     * Update game state
     */
    private void update() {
        camera.update(); // Smooth camera movement
        // Future: update guests, lifts, etc.
    }
    
    /**
     * Render the entire scene
     */
    private void render() {
        // Clear the canvas
        gc.setFill(Color.LIGHTBLUE); // Sky color
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Render mountain terrain
        renderMountainTerrain();
        
        // Render slopes
        renderSlopes();
        
        // Render lifts
        renderLifts();
        
        // Render UI overlay
        renderUI();
    }
    
    /**
     * Render mountain terrain as isometric tiles
     */
    private void renderMountainTerrain() {
        Camera2D.ViewBounds bounds = camera.getVisibleBounds();
        
        // Render tiles in proper z-order (back to front, low to high)
        for (int y = bounds.maxY; y >= bounds.minY; y--) {
            for (int x = bounds.minX; x <= bounds.maxX; x++) {
                if (isInBounds(x, y)) {
                    renderTerrainTile(x, y);
                }
            }
        }
    }
    
    /**
     * Render a single terrain tile
     */
    private void renderTerrainTile(int worldX, int worldY) {
        double elevation = getElevationSafe(worldX, worldY);
        ScreenPoint screen = camera.worldToScreen(worldX, worldY, elevation);
        
        // Skip if not visible
        if (!camera.isVisible(worldX, worldY, elevation)) {
            return;
        }
        
        // Get tile corners
        ScreenPoint[] corners = IsometricMath.getTileBounds(worldX, worldY, elevation);
        
        // Apply camera transform to corners
        for (int i = 0; i < corners.length; i++) {
            ScreenPoint worldCorner = corners[i];
            corners[i] = new ScreenPoint(
                (int)((worldCorner.x - camera.getWorldX() * IsometricMath.TILE_WIDTH/2) * camera.getZoom() + camera.getViewportWidth()/2),
                (int)((worldCorner.y - camera.getWorldY() * IsometricMath.TILE_HEIGHT/2) * camera.getZoom() + camera.getViewportHeight()/2)
            );
        }
        
        // Choose color based on elevation
        Color tileColor = getElevationColor(elevation);
        gc.setFill(tileColor);
        
        // Draw the diamond-shaped tile
        double[] xPoints = {corners[0].x, corners[1].x, corners[2].x, corners[3].x};
        double[] yPoints = {corners[0].y, corners[1].y, corners[2].y, corners[3].y};
        gc.fillPolygon(xPoints, yPoints, 4);
        
        // Draw tile outline
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(0.5);
        gc.strokePolygon(xPoints, yPoints, 4);
    }
    
    /**
     * Get color for terrain based on elevation
     */
    private Color getElevationColor(double elevation) {
        // Simple elevation-based coloring
        if (elevation < 5) return Color.LIGHTGREEN;      // Low areas
        else if (elevation < 15) return Color.GREEN;     // Medium areas  
        else if (elevation < 25) return Color.DARKGREEN; // High areas
        else return Color.LIGHTGRAY;                     // Very high/rocky areas
    }
    
    /**
     * Render slopes
     */
    private void renderSlopes() {
        for (Slope slope : mountain.getSlopes()) {
            renderSlope(slope);
        }
    }
    
    /**
     * Render a single slope
     */
    private void renderSlope(Slope slope) {
        Position start = slope.getStartPosition();
        Position end = slope.getEndPosition();
        
        double startElevation = getElevationSafe(start.getX(), start.getY());
        double endElevation = getElevationSafe(end.getX(), end.getY());
        
        ScreenPoint startScreen = camera.worldToScreen(start, startElevation);
        ScreenPoint endScreen = camera.worldToScreen(end, endElevation);
        
        // Choose color based on difficulty
        Color slopeColor = switch (slope.getDifficulty()) {
            case BEGINNER -> Color.GREEN;
            case INTERMEDIATE -> Color.BLUE;
            case ADVANCED -> Color.BLACK;
            case EXPERT -> Color.RED;
        };
        
        gc.setStroke(slopeColor);
        gc.setLineWidth(3.0 * camera.getZoom());
        gc.strokeLine(startScreen.x, startScreen.y, endScreen.x, endScreen.y);
        
        // Draw slope name
        if (camera.getZoom() > 0.5) {
            gc.setFill(slopeColor);
            gc.fillText(slope.getName(), 
                (startScreen.x + endScreen.x) / 2, 
                (startScreen.y + endScreen.y) / 2);
        }
    }
    
    /**
     * Render lifts
     */
    private void renderLifts() {
        for (Lift lift : mountain.getLifts()) {
            renderLift(lift);
        }
    }
    
    /**
     * Render a single lift
     */
    private void renderLift(Lift lift) {
        Position bottom = lift.getBottomPosition();
        Position top = lift.getTopPosition();
        
        double bottomElevation = getElevationSafe(bottom.getX(), bottom.getY());
        double topElevation = getElevationSafe(top.getX(), top.getY());
        
        ScreenPoint bottomScreen = camera.worldToScreen(bottom, bottomElevation);
        ScreenPoint topScreen = camera.worldToScreen(top, topElevation);
        
        // Draw lift line
        gc.setStroke(Color.BROWN);
        gc.setLineWidth(2.0 * camera.getZoom());
        gc.strokeLine(bottomScreen.x, bottomScreen.y, topScreen.x, topScreen.y);
        
        // Draw lift stations
        gc.setFill(Color.BROWN);
        double stationSize = 4 * camera.getZoom();
        gc.fillOval(bottomScreen.x - stationSize/2, bottomScreen.y - stationSize/2, stationSize, stationSize);
        gc.fillOval(topScreen.x - stationSize/2, topScreen.y - stationSize/2, stationSize, stationSize);
        
        // Draw lift name
        if (camera.getZoom() > 0.5) {
            gc.setFill(Color.BROWN);
            gc.fillText(lift.getName(), 
                (bottomScreen.x + topScreen.x) / 2, 
                (bottomScreen.y + topScreen.y) / 2 - 10);
        }
    }
    
    /**
     * Render UI overlay
     */
    private void renderUI() {
        // Camera info
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("Camera: (%.1f, %.1f) Zoom: %.2f", 
            camera.getWorldX(), camera.getWorldY(), camera.getZoom()), 10, 20);
        
        // Controls info
        gc.fillText("Controls: Drag to pan, Scroll to zoom, Click for coordinates", 10, 40);
        
        // Mountain info
        gc.fillText(String.format("Mountain: %s (%dx%d)", 
            mountain.getName(), mountain.getWidth(), mountain.getHeight()), 10, 60);
        
        // Infrastructure count
        gc.fillText(String.format("Infrastructure: %d slopes, %d lifts", 
            mountain.getSlopes().size(), mountain.getLifts().size()), 10, 80);
    }
    
    /**
     * Helper method to safely get elevation, handling out-of-bounds coordinates
     */
    private double getElevationSafe(int x, int y) {
        try {
            if (x >= 0 && x < mountain.getWidth() && y >= 0 && y < mountain.getHeight()) {
                return mountain.getElevationAt(x, y);
            } else {
                return 0.0; // Default elevation for out-of-bounds
            }
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Helper method to check if coordinates are within mountain bounds
     */
    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < mountain.getWidth() && y >= 0 && y < mountain.getHeight();
    }
    
    public static void main(String[] args) {
        System.out.println("Starting Ski Resort Tycoon GUI...");
        launch(args);
    }
} 