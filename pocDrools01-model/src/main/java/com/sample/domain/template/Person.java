package com.sample.domain.template;

public class Person {
    
    private String name;
    
    private String likes;
    
    private int age;
    
    private char sex;
    
    private boolean alive;
    
    private String status;

    public Person(String name, String likes, int age) {
        super();
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    public Person() {
        super();
        // TODO Auto-generated constructor stub
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
     * @return the likes
     */
    public String getLikes() {
        return likes;
    }

    /**
     * @param likes the likes to set
     */
    public void setLikes(String likes) {
        this.likes = likes;
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
     * @return the sex
     */
    public char getSex() {
        return sex;
    }

    /**
     * @param sex the sex to set
     */
    public void setSex(char sex) {
        this.sex = sex;
    }

    /**
     * @return the alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * @param alive the alive to set
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    

}
