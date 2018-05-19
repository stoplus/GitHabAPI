package com.example.den.githabapi.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Organization {

    @SerializedName("login")
    @Expose
    private String login;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;

    @SerializedName("blog")
    @Expose
    private String blog;

    @SerializedName("public_repos")
    @Expose
    private Integer publicRepos;

    public Organization(String avatarUrl, String name, String blog, String location) {
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.blog = blog;
        this.location = location;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPublicRepos() {
        return publicRepos;
    }

    public void setPublicRepos(Integer publicRepos) {
        this.publicRepos = publicRepos;
    }
}