package com.sample.domain;

import java.util.Date;

public class Application {

    private Date dateApplied;
    
    private boolean valid;

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the dateApplied
     */
    public Date getDateApplied() {
        return dateApplied;
    }

    /**
     * @param dateApplied the dateApplied to set
     */
    public void setDateApplied(Date dateApplied) {
        this.dateApplied = dateApplied;
    }
    
    
}
