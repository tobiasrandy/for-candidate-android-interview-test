package com.tokopedia.testproject.problems.news.network;

import com.tokopedia.testproject.problems.news.model.NewsResultModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {
    @GET("everything")
    Observable<NewsResultModel> getEverything(@Query("q") String query,
                                              @Query("apiKey") String apiKey,
                                              @Query("sortBy") String sortBy,
                                              @Query("page") Integer page);

    @GET("top-headlines")
    Observable<NewsResultModel> getTopHeadlines(@Query("country") String country,
                                           @Query("category") String category,
                                           @Query("apiKey") String apiKey);
}
