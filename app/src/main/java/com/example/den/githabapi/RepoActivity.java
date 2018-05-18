package com.example.den.githabapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.den.githabapi.adapters.AdapterRepo;
import com.example.den.githabapi.entity.GitHubRepo;

import java.util.List;

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
    private AuthorizationUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);
        ButterKnife.bind(this);

        String nameList = getIntent().getStringExtra("nameList");
        String nameListForTitle = nameList.substring(0, 1).toUpperCase() + nameList.substring(1);//Первая буква заглавная
        int publicRepos = getIntent().getIntExtra("publicRepos", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(nameListForTitle + " Repositories: " + publicRepos);

        start(nameList);
    }

    private void start(String nameList) {
        if (InternetConnection.checkConnection(getApplicationContext())) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GitHubClientInterface client = retrofit.create(GitHubClientInterface.class);
            utils = new AuthorizationUtils();
            String access_token = utils.restore(RepoActivity.this);
            Call<List<GitHubRepo>> call = client.repoUser("/users/"+nameList+"/repos?access_token="+access_token+"&per_page=100");
//            Call<List<GitHubRepo>> call = client.repoUser("/users/"+nameList+"/repos?access_token="+access_token+"&per_page=100&page=2");//вторая сотня
            call.enqueue(new Callback<List<GitHubRepo>>() {
                @Override
                public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                    List<GitHubRepo> repos = response.body();
                    AdapterRepo adapter = new AdapterRepo(RepoActivity.this, repos);
                    recyclerRepo.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                    Toast.makeText(RepoActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            });

        } else
            Toast.makeText(getApplicationContext(), "Проверьте соединение с интернетом!", Toast.LENGTH_LONG).show();
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
