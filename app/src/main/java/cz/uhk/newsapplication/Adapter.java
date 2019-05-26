package cz.uhk.newsapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import cz.uhk.newsapplication.model.Article;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<Article> articles;
    private Context context;
    private OnClickListener onClickListener;
    private int decider;

    public Adapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (decider == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_find_something, parent,
                    false);

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_what_is_new, parent,
                    false);
        }

        return new MyViewHolder(view, onClickListener);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holders, int position) {
        Article model = articles.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {

                        if (decider == 0) {
                            holders.progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {

                        if (decider == 0) {
                            holders.progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holders.imageView);
        holders.newsTitle.setText(model.getTitle());
        holders.newsContent.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setOnItemClickListener(OnClickListener onItemClickListener) {
        this.onClickListener = onItemClickListener;
    }

    public interface OnClickListener {
        void onItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView newsTitle, newsContent;
        ImageView imageView;
        ProgressBar progressBar;
        OnClickListener onClickListener;

        public MyViewHolder(View itemView, OnClickListener onClickListener) {
            super(itemView);

            itemView.setOnClickListener(this);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsContent = itemView.findViewById(R.id.newsContent);
            imageView = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progressImage);

            this.onClickListener = onClickListener;
        }

        @Override
        public void onClick(View v) {
            onClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public void setDecider(int decider) {
        this.decider = decider;
    }
}
