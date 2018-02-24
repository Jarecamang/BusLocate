package com.jarecamang.buslocate;

/**
 * Created by Alejandro on 21/02/2018.
 */

public class Bus {
    private String name;
    private String description;
    private String stops_url;

    public String getStops_url() {
        return stops_url;
    }

    public void setStops_url(String stops_url) {
        this.stops_url = stops_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    private String img_url;
    private Integer id;

    public Bus() {
    }

    public Bus(Integer id ,String name, String description, String stops_url, String img_url) {
        this.name = name;
        this.description = description;
        this.stops_url = stops_url;
        this.img_url = img_url;
        this.id = id;
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
