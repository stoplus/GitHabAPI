package com.example.den.githabapi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.den.githabapi.adapters.AdapterRepo;
import com.example.den.githabapi.entity.GitHubRepo;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerRepo)
    RecyclerView recyclerRepo;
    @BindView(R.id.idProgress)
    ProgressBar progressBar;
    private String BASE_URL = "https://api.github.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);
        ButterKnife.bind(this);

        String nameList = getIntent().getStringExtra("nameList");
        String nameListForTitle = nameList.substring(0, 1).toUpperCase() + nameList.substring(1);//The first letter is uppercase
        int publicRepos = getIntent().getIntExtra("publicRepos", 0);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(nameListForTitle + " Repositories: " + publicRepos);

        start(nameList);
    }

    private void start(String nameList) {
        if (InternetConnection.checkConnection(getApplicationContext())) {
            progressBar.setVisibility(View.VISIBLE);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GitHubClientInterface client = retrofit.create(GitHubClientInterface.class);
            AuthorizationUtils utils = new AuthorizationUtils();
            String access_token = utils.restore(RepoActivity.this);
            Call<List<GitHubRepo>> call = client.repoUser("/users/" + nameList + "/repos?access_token=" + access_token + "&per_page=100");
//            Call<List<GitHubRepo>> call = client.repoUser("/users/"+nameList+"/repos?access_token="+access_token+"&per_page=100&page=2");//second hundred
            call.enqueue(new Callback<List<GitHubRepo>>() {
                @Override
                public void onResponse(@NonNull Call<List<GitHubRepo>> call, @NonNull Response<List<GitHubRepo>> response) {
                    List<GitHubRepo> repos = response.body();
                    AdapterRepo adapter = new AdapterRepo(RepoActivity.this, repos);
                    recyclerRepo.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<List<GitHubRepo>> call, @NonNull Throwable t) {
                    Toast.makeText(RepoActivity.this, "error", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
