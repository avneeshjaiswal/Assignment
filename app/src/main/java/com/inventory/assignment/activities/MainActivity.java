package com.inventory.assignment.activities;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.inventory.assignment.Helper.GridSpacingItemDecoration;
import com.inventory.assignment.Helper.RecyclerTouchListener;
import com.inventory.assignment.Helper.Utils;
import com.inventory.assignment.R;
import com.inventory.assignment.adapters.MovieAdapter;
import com.inventory.assignment.model.MovieModel;
import com.inventory.assignment.networking.ApiConfig;
import com.inventory.assignment.networking.ApiService;
import com.inventory.assignment.networking.RestAdapter;
import com.inventory.assignment.roomDb.Database;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inventory.assignment.Helper.Utils.dpToPx;
import static com.inventory.assignment.config.Config.KEY_AVG_VOTE;
import static com.inventory.assignment.config.Config.KEY_MOVIE_ID;
import static com.inventory.assignment.config.Config.KEY_MOVIE_OVERVIEW;
import static com.inventory.assignment.config.Config.KEY_MOVIE_TITLE;
import static com.inventory.assignment.config.Config.KEY_ORIGINAL_TITLE;
import static com.inventory.assignment.config.Config.KEY_POPULARITY;
import static com.inventory.assignment.config.Config.KEY_POSTER_LINK;
import static com.inventory.assignment.config.Config.KEY_RELEASE_DATA;
import static com.inventory.assignment.config.Config.KEY_RESULT;
import static com.inventory.assignment.config.Config.KEY_TOP_RATED;
import static com.inventory.assignment.config.Config.KEY_UPCOMING;
import static com.inventory.assignment.config.Config.KEY_VOTE_COUNT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //room db
    public static Database mDatabase;

    @BindView(R.id.img_filter)
    public ImageView imgFilter;

    @BindView(R.id.pop_movies_rv)
    public RecyclerView rvMain;

    private ProgressDialog pDialog;

    private MovieAdapter mMovieAdapter;

    public static List<MovieModel> mMovieList = new ArrayList<>();
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //mDatabase init
        mDatabase = Room.databaseBuilder(MainActivity.this, Database.class, "movieDb").allowMainThreadQueries().build();
        pDialog = new ProgressDialog(this);

        if (Utils.isNetworkConnected(MainActivity.this)) {
            initLayout();
        } else {
//            //setting offline data
//            for (MovieModel model : MainActivity.mDatabase.movieDao().getPopularMovies("popularity")) {
//                Log.d(TAG, "onCreate: popularity : " + model.getTitle());
//            }
//            //setting offline data
//            for (MovieModel model : MainActivity.mDatabase.movieDao().getPopularMovies("upcoming")) {
//                Log.d(TAG, "onCreate: upcoming : " + model.getTitle());
//            }
//
//            for (MovieModel model : MainActivity.mDatabase.movieDao().getPopularMovies("top_rated")) {
//                Log.d(TAG, "onCreate: upcoming : " + model.getTitle());
//            }
//
//
//            initLayout();
            Toast.makeText(getApplicationContext(), "Plz connect to internet to get new movies!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initLayout() {
        //init layout grid
        mMovieAdapter = new MovieAdapter(MainActivity.this, mMovieList);
        rvMain.setLayoutManager(new GridLayoutManager(this, 2));
        rvMain.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getApplicationContext(), 10), false));
        rvMain.setItemAnimator(new DefaultItemAnimator());
        rvMain.setAdapter(mMovieAdapter);
        rvMain.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rvMain, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra(KEY_MOVIE_ID, mMovieList.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //fetching basic movie list
        fetchMovies(KEY_POPULARITY);
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                builder.setMessage("Sort By")
                        .setPositiveButton("Top-Rated", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fetchMovies(KEY_TOP_RATED);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("UpComing", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fetchMovies(KEY_UPCOMING);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    //method to get popular movie list from api
    private void fetchMovies(String tag) {

        switch (tag) {
            case KEY_POPULARITY:
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
                final ApiService movieDataService = RestAdapter.createAPIWithUrl(ApiConfig.MOVIE_LIST);
                Call<JsonObject> callBackMovieList = movieDataService.ApiPopularMovieList();
                callBackMovieList.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        pDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response.body());
                        if (response.isSuccessful()) {
                            JsonObject dataObj = response.body();

                            if (dataObj != null && dataObj.get(KEY_RESULT).isJsonArray()) {

                                JsonArray resultArr = dataObj.getAsJsonArray(KEY_RESULT);

                                mMovieList.clear();
                                assert resultArr != null;
                                for (int i = 0; i < resultArr.size(); i++) {
                                    JsonObject movieObj = resultArr.get(i).getAsJsonObject();
                                    MovieModel model = new MovieModel(

                                            movieObj.get(KEY_MOVIE_ID).getAsInt(),
                                            movieObj.get(KEY_AVG_VOTE).getAsInt(),
                                            movieObj.get(KEY_VOTE_COUNT).getAsInt(),
                                            movieObj.get(KEY_ORIGINAL_TITLE).getAsString(),
                                            movieObj.get(KEY_MOVIE_TITLE).getAsString(),
                                            movieObj.get(KEY_MOVIE_OVERVIEW).getAsString(),
                                            movieObj.get(KEY_RELEASE_DATA).getAsString(),
                                            movieObj.get(KEY_POSTER_LINK).getAsString(),
                                            movieObj.get(KEY_POPULARITY).getAsDouble(),
                                            "popularity"
                                    );
                                    mMovieList.add(model);
                                }
                                mMovieAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "onResponse: errro");
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Sever Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        pDialog.dismiss();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
                break;

            case KEY_UPCOMING:
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
                final ApiService upComingMovieDataService = RestAdapter.createAPIWithUrl(ApiConfig.MOVIE_LIST);
                Call<JsonObject> callBackUpComingMovieList = upComingMovieDataService.ApiUpComingMovie();
                callBackUpComingMovieList.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        pDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response.body());
                        if (response.isSuccessful()) {
                            JsonObject dataObj = response.body();

                            if (dataObj != null && dataObj.get(KEY_RESULT).isJsonArray()) {

                                JsonArray resultArr = dataObj.getAsJsonArray(KEY_RESULT);

                                mMovieList.clear();
                                assert resultArr != null;
                                for (int i = 0; i < resultArr.size(); i++) {
                                    JsonObject movieObj = resultArr.get(i).getAsJsonObject();
                                    MovieModel model = new MovieModel(

                                            movieObj.get(KEY_MOVIE_ID).getAsInt(),
                                            movieObj.get(KEY_AVG_VOTE).getAsInt(),
                                            movieObj.get(KEY_VOTE_COUNT).getAsInt(),
                                            movieObj.get(KEY_ORIGINAL_TITLE).getAsString(),
                                            movieObj.get(KEY_MOVIE_TITLE).getAsString(),
                                            movieObj.get(KEY_MOVIE_OVERVIEW).getAsString(),
                                            movieObj.get(KEY_RELEASE_DATA).getAsString(),
                                            movieObj.get(KEY_POSTER_LINK).getAsString(),
                                            movieObj.get(KEY_POPULARITY).getAsDouble(),
                                            "upcoming"
                                    );
                                    mMovieList.add(model);
                                }
                                mMovieAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "onResponse: errro");
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Sever Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        pDialog.dismiss();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
                break;

            case KEY_TOP_RATED:
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
                final ApiService TopRateMovieService = RestAdapter.createAPIWithUrl(ApiConfig.MOVIE_LIST);
                Call<JsonObject> callBackTopMovieList = TopRateMovieService.ApiTopRatedMovie();
                callBackTopMovieList.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        pDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response.body());
                        if (response.isSuccessful()) {
                            JsonObject dataObj = response.body();

                            if (dataObj != null && dataObj.get(KEY_RESULT).isJsonArray()) {

                                JsonArray resultArr = dataObj.getAsJsonArray(KEY_RESULT);

                                mMovieList.clear();
                                assert resultArr != null;
                                for (int i = 0; i < resultArr.size(); i++) {
                                    JsonObject movieObj = resultArr.get(i).getAsJsonObject();
                                    MovieModel model = new MovieModel(

                                            movieObj.get(KEY_MOVIE_ID).getAsInt(),
                                            movieObj.get(KEY_AVG_VOTE).getAsInt(),
                                            movieObj.get(KEY_VOTE_COUNT).getAsInt(),
                                            movieObj.get(KEY_ORIGINAL_TITLE).getAsString(),
                                            movieObj.get(KEY_MOVIE_TITLE).getAsString(),
                                            movieObj.get(KEY_MOVIE_OVERVIEW).getAsString(),
                                            movieObj.get(KEY_RELEASE_DATA).getAsString(),
                                            movieObj.get(KEY_POSTER_LINK).getAsString(),
                                            movieObj.get(KEY_POPULARITY).getAsDouble(),
                                            "top_rated"
                                    );
                                    mMovieList.add(model);
                                }
                                mMovieAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "onResponse: errro");
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Sever Error!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        pDialog.dismiss();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }
}
