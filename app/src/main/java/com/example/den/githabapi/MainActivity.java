package com.example.den.githabapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
        //если пользователь не авторизован
        if (!utils.isAuthorized(this)) {
            onLogout();
            return;
        }

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
                    mHandler.postDelayed(mFilterTask, 1500);
                }
            }
        });
    }

    private void start(String name) {
        count = 0;
        allOrganizations.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubClientInterface client = retrofit.create(GitHubClientInterface.class);
        String access_token = utils.restore(MainActivity.this);
        Call<ResponseBody> call = client.getResponse("/search/users?access_token=" + access_token + "&q=" + name + "%20in:login+type:org");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    createListFromJson(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Ошибка запроса", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createListFromJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            int length = jsonArray.length() > 15 ? 15 : jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jProduct = jsonArray.getJSONObject(i);
                String name = jProduct.getString("login");
                getDataOrg(name);
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDataOrg(final String name) {

        String BASE_URL = "https://api.github.com";

        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitHubClientInterface service = client.create(GitHubClientInterface.class);
        String access_token = utils.restore(MainActivity.this);
        Call<ResponseBody> call = service.getUser("/users/" + name + "?access_token=" + access_token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
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
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
    }

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
