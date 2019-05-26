package cz.uhk.newsapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class DetailActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener {

    private boolean hideToolbar = false;
    private LinearLayout titleAppBar;
    private String mURL;
    private String mTitle;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout =
                findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
        titleAppBar = findViewById(R.id.appbar);
        ImageView imageView = findViewById(R.id.backdrop);
        TextView title = findViewById(R.id.title);

        //getting attributes from intent (WhatIsNewActivity)
        Intent intent = getIntent();
        mURL = intent.getStringExtra("url");
        String mImg = intent.getStringExtra("img");
        mTitle = intent.getStringExtra("title");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(new ColorDrawable(Color.parseColor("#1ba798")));
        Glide.with(this)
                .load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions
                        .withCrossFade())
                .into(imageView);

        title.setText(mTitle);
        initWebView(mURL);
    }

    private void initWebView(String url) {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        float percentage = (float) Math.abs(verticalOffset) /
                (float) appBarLayout.getTotalScrollRange();

        if (percentage == 1. && hideToolbar) {
            titleAppBar.setVisibility(View.VISIBLE);
            hideToolbar = !hideToolbar;
        } else if (percentage < 1. && hideToolbar) {
            titleAppBar.setVisibility(View.GONE);
            hideToolbar = !hideToolbar;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.view_web) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mURL));
            startActivity(i);
        } else if (id == R.id.share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plan");
                i.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                String body = "Shared from Whats new APP \n" + mURL;
                i.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(i, "Share: "));
            } catch (Exception e) {
                Toast.makeText(this, "Cannot be shared", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
