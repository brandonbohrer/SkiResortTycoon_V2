package com.skiresort.shared;

import java.util.Objects;

/**
 * Immutable value object representing a 2D position on the mountain/resort
 */
public final class Position {
    private final int x;
    private final int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Position move(int deltaX, int deltaY) {
        return new Position(x + deltaX, y + deltaY);
    }
    
    public double distanceTo(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public boolean isWithinBounds(int maxX, int maxY) {
        return x >= 0 && y >= 0 && x < maxX && y < maxY;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return String.format("Position(%d, %d)", x, y);
    }
} 