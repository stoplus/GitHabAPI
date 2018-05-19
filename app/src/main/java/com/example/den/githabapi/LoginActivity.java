package com.example.den.githabapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oauthrunnerlib.Runner;
import com.example.oauthrunnerlib.plugin.GitHubPlugin;
import com.example.oauthrunnerlib.plugin.PluginResponse;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_client_id)
    EditText etClientId;
    @BindView(R.id.et_redirect_url)
    EditText etRedirectUrl;
    @BindView(R.id.et_client_secret)
    EditText etClientSecret;
    @BindView(R.id.call_git)
    Button callGit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        callGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callGitAuth();
            }
        });
    }

    private void callGitAuth() {
        if (etClientId.getText().toString().isEmpty() || etClientSecret.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this,
                    getResources().getString(R.string.enter_date), Toast.LENGTH_LONG).show();
        } else {
            GitHubPlugin.Builder builder = new GitHubPlugin.Builder();
            builder.setClientId(etClientId.getText().toString());
            builder.setClientSecret(etClientSecret.getText().toString());
            builder.setRedirectUri(etRedirectUrl.getText().toString());

            GitHubPlugin plugin = builder.build();
            Runner runner = new Runner(this, plugin);
            runner.execute(new Runner.Callback() {
                @Override
                public void onSuccess(PluginResponse response) {
                    GitHubPlugin.GitHubResponse gitHubResponse = (GitHubPlugin.GitHubResponse) response;
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.auth_success)
                                    + gitHubResponse.getAccessToken(), Toast.LENGTH_LONG).show();

                    AuthorizationUtils utils = new AuthorizationUtils();
                    utils.setAuthorized(LoginActivity.this);//устанавливаем флаг true в преференсах
                    utils.access_token = gitHubResponse.getAccessToken();
                    utils.save(LoginActivity.this);
                    onLoginCompleted();//запуск активности
                }

                @Override
                public void onFailure(String failureMessage) {
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.error_auth) + failureMessage,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //	start MainActivity
    private void onLoginCompleted() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }//onLoginCompleted

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
