package com.example.oauthrunnerlib.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.example.oauthrunnerlib.R;
import com.example.oauthrunnerlib.Runner;

public class OAuthActivity extends AppCompatActivity {

    Runner mRunner;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        mWebView = (WebView) findViewById(R.id.auth_web_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearCookies();
        mRunner = Runner.getInstance();
        mRunner.setWebView(mWebView);
        mRunner.execute(mRunner.getCallback());
        mRunner.setDoneCallback(new Runner.IsDone() {
            @Override
            public void done() {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            cookieManager.removeAllCookie();
        }
    }
}
