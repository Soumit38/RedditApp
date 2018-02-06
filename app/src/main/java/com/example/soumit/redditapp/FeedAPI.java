package com.example.soumit.redditapp;

import com.example.soumit.redditapp.account.CheckLogin;
import com.example.soumit.redditapp.comments.CheckComment;
import com.example.soumit.redditapp.model.Feed;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Soumit on 2/2/2018.
 */

public interface FeedAPI {

    String BASE_URL = "https://www.reddit.com/r/";

    //non-static feed name
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

    /*  static feed name
        @GET("earthporn/.rss")
        Call<Feed> getFeed();
    */
    @POST("{user}")
    Call<CheckLogin> signIn(
            @HeaderMap Map<String, String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );

    @POST("{comment}")
    Call<CheckComment> submitComment(
            @HeaderMap Map<String, String> headers,
            @Path("comment") String comment,
            @Query("parent") String parent,
            @Query("amp;text") String text
    );


}
