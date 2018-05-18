package com.example.den.githabapi.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GitHubRepo {

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("name")
    @Expose
    private String name;

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