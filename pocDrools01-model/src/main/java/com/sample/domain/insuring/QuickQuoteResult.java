package com.sample.domain.insuring;

public class QuickQuoteResult {

    private QuickQuoteInputProfile profile;
    
    private boolean eligible;
    
    public QuickQuoteResult(boolean eligible) {
        super();
        this.eligible = eligible;
    }


    /**
     * @return the eligible
     */
    public boolean isEligible() {
        return eligible;
    }

    /**
     * @param eligible the eligible to set
     */
    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    /**
     * @return the profile
     */
    public QuickQuoteInputProfile getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(QuickQuoteInputProfile profile) {
        this.profile = profile;
    }

}
