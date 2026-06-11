package com.windpvp.hub.particle;

import org.bukkit.Effect;
import org.bukkit.Location;

public class PersistentParticle {
    private final String id;
    private final Location location;
    private final Effect type;

    public PersistentParticle(String id, Location location, Effect type) {
        this.id = id;
        this.location = location;
        this.type = type;
    }

    public String getId() { 
        return id; 
    }
    
    public Location getLocation() { 
        return location; 
    }
    
    public Effect getType() { 
        return type; 
    }
}