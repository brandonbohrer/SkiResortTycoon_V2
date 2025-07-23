package com.skiresort.components.guests.core;

/**
 * Represents an activity that a guest wants to perform
 * Used by the guest AI system to make decisions
 */
public class GuestActivity {
    
    public enum Type {
        FIND_LIFT,      // Look for an available lift to ride
        FIND_SLOPE,     // Look for a suitable slope to ski
        WAIT,           // Wait in current location
        LEAVE_RESORT    // Leave the resort (satisfaction/time based)
    }
    
    private final Type type;
    private final Object target; // Could be a specific Lift, Slope, or null
    
    public GuestActivity(Type type, Object target) {
        this.type = type;
        this.target = target;
    }
    
    public Type getType() {
        return type;
    }
    
    public Object getTarget() {
        return target;
    }
    
    @Override
    public String toString() {
        return String.format("GuestActivity{type=%s, target=%s}", type, target);
    }
} 