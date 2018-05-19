package com.example.den.githabapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.den.githabapi.adapters.AdapterGitHubRepo;
import com.example.den.githabapi.entity.Organization;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.list_item)
    RecyclerView recyclerView;
    @BindView(R.id.idProgress)
    ProgressBar progressBar;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.editText)
    EditText editText;
    private List<Organization> allOrganizations = new ArrayList<>();
    private AuthorizationUtils utils;
    private int count;
    private String searchText;
    private Handler mHandler = new Handler();
    private String BASE_URL = "https://api.github.com";
    private GitHubClientInterface client;
    private String access_token;
    private int timeDelayedRequest = 1500;

    private Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            start(searchText);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        utils = new AuthorizationUtils();

        //if the user is not authorized
        if (!utils.isAuthorized(this)) {
            onLogout();
            return;
        }
        install();
    }

    private void install() {
        editText.addTextChangedListener(new TextWatcher() {//слушатель изменения текста
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchText = editable.toString();
                if (searchText.length() > 2) {
                    mHandler.removeCallbacks(mFilterTask);
                    mHandler.postDelayed(mFilterTask, timeDelayedRequest);
                }
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        client = retrofit.create(GitHubClientInterface.class);
        access_token = utils.restore(MainActivity.this);
    }

    private void start(String name) {
        if (InternetConnection.checkConnection(getApplicationContext())) {
            progressBar.setVisibility(View.VISIBLE);
            count = 0;
            allOrganizations.clear();

            Call<ResponseBody> call = client.getResponse("/search/users?access_token=" + access_token + "&q=" + name + "%20in:login+type:org");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                    try {
                        String result = Objects.requireNonNull(response.body()).string();
                        getLoginFromJson(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            });
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
    }

    private void getLoginFromJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            int quantityOfAccounts = 15;
            int length = jsonArray.length() > quantityOfAccounts ? quantityOfAccounts : jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jProduct = jsonArray.getJSONObject(i);
                String login = jProduct.getString("login");
                getDataOrg(login);
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDataOrg(final String login) {
        if (InternetConnection.checkConnection(getApplicationContext())) {
            progressBar.setVisibility(View.VISIBLE);
            Call<ResponseBody> call = client.getResponse("/users/" + login + "?access_token=" + access_token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        String result = Objects.requireNonNull(response.body()).string();
                        Gson gson = new GsonBuilder().create();
                        Organization organization = gson.fromJson(result, Organization.class);
                        allOrganizations.add(organization);
                        progressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    count--;
                    if (count == 0) {
                        AdapterGitHubRepo adapter = new AdapterGitHubRepo(MainActivity.this, allOrganizations);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                }//onResponse

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
    }//getDataOrg

    //	If user is not authorized we finish the main activity
    private void onLogout() {
        utils.access_token = null;
        utils.save(MainActivity.this);
        Intent login = new Intent(this, LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
        finish();
    }//onLogout
}
