package com.sample.support;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.sample.domain.template.ItemCode;

public class ParamSet {

    private EnumSet<ItemCode> codes;
    
    private String field;
    
    private int lower;
    
    private int upper;

    public ParamSet(EnumSet<ItemCode> codes, String field, int lower, int upper) {
        super();
        this.codes = codes;
        this.field = field;
        this.lower = lower;
        this.upper = upper;
    }
    
    public String getCodes() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        String conn = "";
        for(ItemCode ic : codes) {
            //sb.append(conn).append('\'').append(ic).append('\'');
            sb.append(conn).append("ItemCode.").append(ic);
            conn = ",";
        }
        sb.append(']');
        
        return sb.toString();
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the lower
     */
    public int getLower() {
        return lower;
    }

    /**
     * @param lower the lower to set
     */
    public void setLower(int lower) {
        this.lower = lower;
    }

    /**
     * @return the upper
     */
    public int getUpper() {
        return upper;
    }

    /**
     * @param upper the upper to set
     */
    public void setUpper(int upper) {
        this.upper = upper;
    }
    
    
}
