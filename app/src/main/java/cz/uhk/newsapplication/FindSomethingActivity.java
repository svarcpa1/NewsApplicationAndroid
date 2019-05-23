package cz.uhk.newsapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
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

public class FindSomethingActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener{

    public static final String API_KEY = "f452f55f5b324b488263282b0d192e5e";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String searchedWord="";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_something);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh2);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView = findViewById(R.id.recyclerView1);
        layoutManager = new LinearLayoutManager(FindSomethingActivity.this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh("");
    }

    public void loadJSON(final String searchedWord) {
        ApiInterf apiInterf = ApiClient.getApiClient().create(ApiInterf.class);
        swipeRefreshLayout.setRefreshing(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String selectedLang = mSharedPreferences.getString(getString(R.string.languageListPref),"");
        String selectedSortBy = mSharedPreferences.getString(getString(R.string.sortByListPref),"");
        String sortBy;

        String country = Utils.getCountry();
        //news api allow max 1 month old search results
        String from = Utils.getDateBeforeMonth();

        Call<News> call;

        if("1".equals(selectedSortBy)){
            sortBy="relevancy";
        } else if ("2".equals(selectedSortBy)){
            sortBy="popularity";
        }else {
            sortBy="publishedAt";
        }

        //if typed text into search box
        if (searchedWord.length()!=0){
            if("1".equals(selectedLang)){
                call = apiInterf.getNewsSearchCall(searchedWord, from, sortBy,
                        API_KEY);
            } else if ("2".equals(selectedLang)){
                call = apiInterf.getNewsSearchCallLang(searchedWord, from, "en",
                        sortBy, API_KEY);
            } else if ("3".equals(selectedLang)){
                call = apiInterf.getNewsSearchCallLang(searchedWord, from, "de",
                        sortBy, API_KEY);
            } else if ("4".equals(selectedLang)){
                call = apiInterf.getNewsSearchCallLang(searchedWord, from, "es",
                        sortBy, API_KEY);
            } else {
                call = apiInterf.getNewsSearchCallLang(searchedWord, from, "fr",
                        sortBy, API_KEY);
            }
        }else {
            call = apiInterf.getNewsCall(country, API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    adapter = new Adapter(articles, FindSomethingActivity.this);
                    adapter.setDecider(1);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();

                    swipeRefreshLayout.setRefreshing(false);

                    Toast.makeText(FindSomethingActivity.this,"Here is your result",
                            Toast.LENGTH_SHORT).show();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(FindSomethingActivity.this,"Nothing found :(",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(FindSomethingActivity.this,"Connection failure",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener(){
        adapter.setOnItemClickListener(new Adapter.OnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent =
                        new Intent(FindSomethingActivity.this, DetailActivity.class);

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
        menuInflater.inflate(R.menu.menu_find_something,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        MenuItem menuItem = menu.findItem(R.id.search_bar);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchedWord = query;

                if(query.length()>2){
                    onLoadingSwipeRefresh(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menuItem.getIcon().setVisible(false, false);
        return true;
    }

    @Override
    public void onRefresh() {
        loadJSON(searchedWord);
    }

    private void onLoadingSwipeRefresh(final String searchedWord){
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadJSON(searchedWord);
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
