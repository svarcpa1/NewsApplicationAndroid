package cz.uhk.newsapplication.api;

import cz.uhk.newsapplication.model.News;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterf {

    //call without searching
    @GET("top-headlines")
    Call<News> getNewsCall(
      @Query("country") String country,
      @Query("apiKey") String apiKey

    );

    //call without searching with category
    @GET("top-headlines")
    Call<News> getNewsCallCategory(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    //call with searching
    @GET("everything")
    Call<News> getNewsSearchCall(
            @Query("q") String searchedText,
            @Query("from") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );

    //call with searching with language
    @GET("everything")
    Call<News> getNewsSearchCallLang(
            @Query("q") String searchedText,
            @Query("from") String from,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );

    //call with searching in domains
    @GET("everything")
    Call<News> getNewsDomainCall(
            @Query("domains") String searchedText,
            @Query("apiKey") String apiKey
    );
}
