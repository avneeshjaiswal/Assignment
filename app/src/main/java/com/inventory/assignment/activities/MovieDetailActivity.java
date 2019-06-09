package com.inventory.assignment.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.inventory.assignment.Helper.Utils;
import com.inventory.assignment.R;
import com.inventory.assignment.model.MovieDetailModel;
import com.inventory.assignment.networking.ApiConfig;
import com.inventory.assignment.networking.ApiService;
import com.inventory.assignment.networking.RestAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inventory.assignment.R.drawable.ic_broken_image_gray_24dp;
import static com.inventory.assignment.config.Config.KEY_AVG_VOTE;
import static com.inventory.assignment.config.Config.KEY_BUDGET;
import static com.inventory.assignment.config.Config.KEY_HOMEPAGE;
import static com.inventory.assignment.config.Config.KEY_IMDB;
import static com.inventory.assignment.config.Config.KEY_LANGUAGE;
import static com.inventory.assignment.config.Config.KEY_MOVIE_ID;
import static com.inventory.assignment.config.Config.KEY_MOVIE_OVERVIEW;
import static com.inventory.assignment.config.Config.KEY_ORIGINAL_TITLE;
import static com.inventory.assignment.config.Config.KEY_POPULARITY;
import static com.inventory.assignment.config.Config.KEY_POSTER_LINK;
import static com.inventory.assignment.config.Config.KEY_RELEASE_DATA;
import static com.inventory.assignment.config.Config.KEY_REVENUE;
import static com.inventory.assignment.config.Config.KEY_RUNTIME;
import static com.inventory.assignment.config.Config.KEY_STATUS;
import static com.inventory.assignment.config.Config.KEY_VOTE_COUNT;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MovieDetailActivity";

    private List<MovieDetailModel> mList;
    private ProgressDialog pDialog;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.header_image)
    public ImageView imgHeader;
    @BindView(R.id.txt_title)
    public TextView tvtitle;
    @BindView(R.id.txt_overview)
    public TextView tvOverview;
    @BindView(R.id.txt_budget)
    public TextView tvBudget;
    @BindView(R.id.txt_home_page)
    public TextView tvHomePage;
    @BindView(R.id.txt_lang)
    public TextView tvLang;
    @BindView(R.id.txt_imdb)
    public TextView tvImdb;
    @BindView(R.id.txt_poster)
    public TextView tvPoster;
    @BindView(R.id.txt_popularity)
    public TextView tvPopularity;
    @BindView(R.id.txt_date)
    public TextView tvtDate;
    @BindView(R.id.txt_revenue)
    public TextView tvRevenue;
    @BindView(R.id.txt_runtime)
    public TextView tvRuntime;
    @BindView(R.id.txt_status)
    public TextView tvStatus;
    @BindView(R.id.txt_avg_vote)
    public TextView tvAvgVote;
    @BindView(R.id.txt_count)
    public TextView tvVoteCnt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        if (Utils.isNetworkConnected(MovieDetailActivity.this)) {
            getData();
        } else {
            Toast.makeText(getApplicationContext(), "Plz connect to internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initLayout() {


        //init data
        for (MovieDetailModel model : mList) {

            collapsingToolbarLayout.setTitle(model.getOriginal_title());
            Picasso.get().load(model.getPoster_path()).config(Bitmap.Config.RGB_565).
                    placeholder(ic_broken_image_gray_24dp)
                    .into(imgHeader, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            imgHeader.setImageResource(ic_broken_image_gray_24dp);
                        }
                    });

            tvtitle.setText(model.getOriginal_title());
            tvOverview.setText(model.getOverview());
            tvBudget.setText("$" + model.getBudget());
            tvHomePage.setText(model.getHomepage());
            tvLang.setText(model.getOriginal_language());
            tvImdb.setText(model.getImdb_id());
            tvPoster.setText(ApiConfig.BASE_URL + model.getPoster_path());
            tvPopularity.setText("" + model.getPopularity());
            tvtDate.setText(model.getRelease_date());
            tvRevenue.setText("$" + model.getRevenue());
            tvRuntime.setText(model.getRuntime() + "mins");
            tvStatus.setText(model.getStatus());
            tvAvgVote.setText("" + model.getVote_average());
            tvVoteCnt.setText("" + model.getVote_count());
        }

        tvHomePage.setOnClickListener(this);
        tvPoster.setOnClickListener(this);


    }

    //geting movie details from api
    private void getData() {
        pDialog.setMessage("Fetching...");
        pDialog.setCancelable(false);
        pDialog.show();
        final ApiService movieDataService = RestAdapter.createAPIWithUrl(ApiConfig.MOVIE_LIST);
        Call<JsonObject> callBackMovieList = movieDataService.ApiMovieDetail(getIntent().getIntExtra(KEY_MOVIE_ID, 0));
        callBackMovieList.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    mList = new ArrayList<>();
                    assert jsonObject != null;
                    MovieDetailModel movieModel = new MovieDetailModel(
                            jsonObject.get(KEY_BUDGET).getAsLong(),
                            jsonObject.get(KEY_MOVIE_ID).getAsInt(),
                            jsonObject.get(KEY_POPULARITY).getAsDouble(),
                            jsonObject.get(KEY_HOMEPAGE).getAsString(),
                            jsonObject.get(KEY_IMDB).getAsString(),
                            jsonObject.get(KEY_LANGUAGE).getAsString(),
                            jsonObject.get(KEY_ORIGINAL_TITLE).getAsString(),
                            jsonObject.get(KEY_MOVIE_OVERVIEW).getAsString(),
                            jsonObject.get(KEY_POSTER_LINK).getAsString(),
                            jsonObject.get(KEY_RELEASE_DATA).getAsString(),
                            jsonObject.get(KEY_REVENUE).getAsInt(),
                            jsonObject.get(KEY_RUNTIME).getAsInt(),
                            jsonObject.get(KEY_STATUS).getAsString(),
                            jsonObject.get(KEY_AVG_VOTE).getAsInt(),
                            jsonObject.get(KEY_VOTE_COUNT).getAsInt()
                    );

                    mList.add(movieModel);
                    initLayout();
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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_home_page:
                try {

                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(tvHomePage.getText().toString()));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(tvHomePage.getText().toString()));
                    startActivity(i);
                }
                break;

            case R.id.txt_poster:
                try {

                    Intent i1 = new Intent("android.intent.action.MAIN");
                    i1.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i1.addCategory("android.intent.category.LAUNCHER");
                    i1.setData(Uri.parse(tvPoster.getText().toString()));
                    startActivity(i1);
                } catch (ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(tvPoster.getText().toString()));
                    startActivity(i);
                }
                break;
        }

    }
}
