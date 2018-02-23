package com.jarecamang.buslocate;

/**
 * Created by Alejandro on 21/02/2018.
 */

public class Bus {
    private String name, description;
    private Integer id;

    public Bus() {
    }

    public Bus(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
