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
        GitHubPlugin.Builder builder = new GitHubPlugin.Builder();
//        builder.setClientId(etClientId.getText().toString());
//        builder.setClientSecret(etClientSecret.getText().toString());
//        builder.setRedirectUri(etRedirectUrl.getText().toString());

        builder.setClientId("345dd543b7024d008078");
        builder.setClientSecret("2cd553cd26d08fd866fef46773fe441875cf37a5");
        builder.setRedirectUri("");

        GitHubPlugin plugin = builder.build();
        Runner runner = new Runner(this, plugin);
        runner.execute(new Runner.Callback() {
            @Override
            public void onSuccess(PluginResponse response) {
                GitHubPlugin.GitHubResponse gitHubResponse = (GitHubPlugin.GitHubResponse) response;
                Log.d("MainLogTag", "Auth success. accessToken = " + gitHubResponse.getAccessToken());
                Toast.makeText(LoginActivity.this, "Auth success. accessToken = "
                        + gitHubResponse.getAccessToken(), Toast.LENGTH_LONG).show();

                AuthorizationUtils utils = new AuthorizationUtils();
                utils.setAuthorized(LoginActivity.this);//устанавливаем флаг true в преференсах
                utils.access_token = gitHubResponse.getAccessToken();
                utils.save(LoginActivity.this);
                onLoginCompleted();//запуск активности
            }

            @Override
            public void onFailure(String failureMessage) {
                Log.d("MainLogTag", "Auth failure. Message = " + failureMessage);
                Toast.makeText(LoginActivity.this, "Error Auth with message: " + failureMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    //	запуск маинАктивити
    private void onLoginCompleted() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
//        finish();
    }//onLoginCompleted

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
