package com.example.den.githabapi;

import com.example.den.githabapi.entity.GitHubRepo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GitHubClientInterface {
    //для получения репозитория
//    @GET("/users/{user}/repos")
//    Call<List<GitHubRepo>> repoUser(@Path("user") String user);

    @GET
    Call<List<GitHubRepo>> repoUser(@Url String fileUrl);


    @GET("/orgs/:org")
    Call<List<GitHubRepo>> reposForusers(@Path("user") String user);

    @GET
    Call<ResponseBody> getDataOrg1(@Url String fileUrl);

    //получаем список организай
    @GET
    Call<ResponseBody> getResponse(@Url String fileUrl);

    //получаем url для перехода
    @GET
    Call<ResponseBody> getResponseUrl(@Url String fileUrl);

//    @GET
//    Call<Organization> getUser(@Url String fileUrl);

    //
    @GET
    Call<ResponseBody> getUser(@Url String fileUrl);
}

