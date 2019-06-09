package com.inventory.assignment.networking;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static com.inventory.assignment.config.Config.API_KEY;

public interface ApiService {

    //get popular movie list
    @GET("popular?api_key=" + API_KEY)
    Call<JsonObject> ApiPopularMovieList();

    //get upcoming movie list
    @GET("upcoming?api_key=" + API_KEY)
    Call<JsonObject> ApiUpComingMovie();

    //get toprated movie
    @GET("top_rated?api_key=" + API_KEY)
    Call<JsonObject> ApiTopRatedMovie();

    //get movie details
    @GET("{movie_id}?api_key=99737f8315a26abf80360b53f10151eb")
    Call<JsonObject> ApiMovieDetail(
            @Path("movie_id") int id
    );

}
