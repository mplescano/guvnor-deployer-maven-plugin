package com.sample.domain.template;

public class Item {

    private String name;
    
    private int weight;
    
    private int price;
    
    private ItemCode code;



    public Item(String name, int weight, int price, ItemCode code) {
        super();
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(int weight) {
        this.weight = weight;
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

    /**
     * @return the code
     */
    public ItemCode getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(ItemCode code) {
        this.code = code;
    }
    
    
    
}
