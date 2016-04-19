package com.sample.domain.insuring;

public class SmokerProfile {

    private String status;

    public SmokerProfile(String smokerStatus) {
        super();
        this.status = smokerStatus;
    }

    /**
     * @return the smokerStatus
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param smokerStatus the smokerStatus to set
     */
    public void setStatus(String smokerStatus) {
        this.status = smokerStatus;
    }
    
    
}
