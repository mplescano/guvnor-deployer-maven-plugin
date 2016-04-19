package com.sample.domain.insuring;

public class QuickQuoteInputProfile {

    private String state;
    
    private String gender;
    
    private int age;
    
    private Double faceAmount;
    
    private boolean adverseDiagnosis;
    
    private SmokerProfile smokingProf;

    public QuickQuoteInputProfile() {}
    
    public QuickQuoteInputProfile(String state, String gender, int age,
            Double faceAmount, boolean adverseDiagnosis) {
        super();
        this.state = state;
        this.gender = gender;
        this.age = age;
        this.faceAmount = faceAmount;
        this.adverseDiagnosis = adverseDiagnosis;
    }
    
    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the faceAmount
     */
    public Double getFaceAmount() {
        return faceAmount;
    }

    /**
     * @param faceAmount the faceAmount to set
     */
    public void setFaceAmount(Double faceAmount) {
        this.faceAmount = faceAmount;
    }

    /**
     * @return the adverseDiagnosis
     */
    public boolean isAdverseDiagnosis() {
        return adverseDiagnosis;
    }

    /**
     * @param adverseDiagnosis the adverseDiagnosis to set
     */
    public void setAdverseDiagnosis(boolean adverseDiagnosis) {
        this.adverseDiagnosis = adverseDiagnosis;
    }

    /**
     * @return the smokingProf
     */
    public SmokerProfile getSmokingProf() {
        return smokingProf;
    }

    /**
     * @param smokingProf the smokingProf to set
     */
    public void setSmokingProf(SmokerProfile smokingProf) {
        this.smokingProf = smokingProf;
    }
}