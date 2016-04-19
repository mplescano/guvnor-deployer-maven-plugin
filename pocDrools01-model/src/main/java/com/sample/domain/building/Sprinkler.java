package com.sample.domain.building;

public class Sprinkler {
    
    private Room room;
    
    private boolean on;

    public Sprinkler(Room room, boolean on) {
        super();
        this.room = room;
        this.on = on;
    }

    /**
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * @return the on
     */
    public boolean isOn() {
        return on;
    }

    /**
     * @param on the on to set
     */
    public void setOn(boolean on) {
        this.on = on;
    }

    
    
}
