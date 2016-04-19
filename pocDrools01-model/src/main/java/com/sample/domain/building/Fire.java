package com.sample.domain.building;

public class Fire {

    private Room room;

    public Fire(Room room) {
        super();
        this.room = room;
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
    
}