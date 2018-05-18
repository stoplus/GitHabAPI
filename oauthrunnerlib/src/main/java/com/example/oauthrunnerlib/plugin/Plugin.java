package com.example.oauthrunnerlib.plugin;

import com.example.oauthrunnerlib.Runner;

public interface Plugin {

    String getUrl();

    boolean isContainsBody(String urlString);

    PluginResponse proceed(String response, Runner.Callback callback, Runner.IsDone isDone);

    void onFailure(String response, Runner.Callback callback);
}
