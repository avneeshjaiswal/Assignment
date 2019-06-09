package com.inventory.assignment.model;

public class GenereModel {

    private int id;
    private String name;

    public GenereModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public GenereModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
