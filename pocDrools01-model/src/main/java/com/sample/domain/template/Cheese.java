package com.sample.domain.template;

public class Cheese {
    
    private String type;
    
    private int price;

    public Cheese() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Cheese(String type, int price) {
        super();
        this.type = type;
        this.price = price;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(int price) {
        this.price = price;
    }

    
}
