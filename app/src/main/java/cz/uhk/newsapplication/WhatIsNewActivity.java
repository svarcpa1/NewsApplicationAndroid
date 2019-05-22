package cz.uhk.newsapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.newsapplication.api.ApiClient;
import cz.uhk.newsapplication.api.ApiInterf;
import cz.uhk.newsapplication.model.Article;
import cz.uhk.newsapplication.model.News;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhatIsNewActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener{

    public static final String API_KEY = "f452f55f5b324b488263282b0d192e5e";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_is_new);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView = findViewById(R.id.recyclerView1);
        layoutManager = new LinearLayoutManager(WhatIsNewActivity.this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        //loadJSON();
        onLoadingSwipeRefresh();
    }

    public void loadJSON(){
        swipeRefreshLayout.setRefreshing(true);

        ApiInterf apiInterf = ApiClient.getApiClient().create(ApiInterf.class);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedCountry = mSharedPreferences.getString(getString(R.string.countryListPref),"");
        String selectedCategory = mSharedPreferences.getString(getString(R.string.categoryListPref),"");

        String country;

        if ("1".equals(selectedCountry)){
            country= Utils.getCountry();
        } else if ("2".equals(selectedCountry)){
            country="us";
        } else if ("3".equals(selectedCountry)){
            country="gb";
        } else if ("4".equals(selectedCountry)){
            country="de";
        } else if ("5".equals(selectedCountry)){
            country="fr";
        } else {
            country="cz";
        }

        Call<News> call;
        if ("1".equals(selectedCategory)){
            call = apiInterf.getNewsCall(country,API_KEY);
        } else if ("2".equals(selectedCategory)){
            call = apiInterf.getNewsCallCategory(country, "business",API_KEY);
        } else if ("3".equals(selectedCategory)){
            call = apiInterf.getNewsCallCategory(country, "entertainment",API_KEY);
        } else if ("4".equals(selectedCategory)){
            call = apiInterf.getNewsCallCategory(country, "general",API_KEY);
        } else if ("5".equals(selectedCategory)){
            call = apiInterf.getNewsCallCategory(country, "health",API_KEY);
        } else if ("6".equals(selectedCategory)){
            call = apiInterf.getNewsCallCategory(country, "science",API_KEY);
        } else if ("7".equals(selectedCategory)){
            call = apiInterf.getNewsCallCategory(country, "sports",API_KEY);
        } else {
            call = apiInterf.getNewsCallCategory(country, "technology",API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body()!= null){

                    if(!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    adapter= new Adapter(articles, WhatIsNewActivity.this);
                    adapter.setDecider(0);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();

                    swipeRefreshLayout.setRefreshing(false);

                    Toast.makeText(WhatIsNewActivity.this,"Here is your result",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WhatIsNewActivity.this,"Nothing found :(",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(WhatIsNewActivity.this,"Connection failure",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener(){
        adapter.setOnItemClickListener(new Adapter.OnClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent =
                        new Intent(WhatIsNewActivity.this, DetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img", article.getUrlToImage());

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_what_is_new,menu);
        return true;
    }

    @Override
    public void onRefresh() {
        loadJSON();
    }

    private void onLoadingSwipeRefresh(){
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadJSON();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
