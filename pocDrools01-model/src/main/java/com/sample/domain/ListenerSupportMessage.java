package com.sample.domain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ListenerSupportMessage {
    public static final int HELLO = 0;
    public static final int GOODBYE = 1;

    private String message;

    private int status;
    
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        changes.firePropertyChange("message", this.message, message);
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        changes.firePropertyChange("status", this.status, status);
        this.status = status;
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }
}
