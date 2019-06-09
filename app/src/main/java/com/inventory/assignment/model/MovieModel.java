package com.inventory.assignment.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movies")
public class MovieModel {

    @PrimaryKey
    private int id;
    @ColumnInfo(name = "avg_vote")
    private int voteAverage;
    @ColumnInfo(name = "vote_count")
    private int voteCount;
    @ColumnInfo(name = "original_title")
    private String originalTitle;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "movie_overview")
    private String overview;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "popularity")
    private double popularity;
    @ColumnInfo(name = "tag")
    private String tag;


    public MovieModel(int id, int voteAverage, int voteCount, String originalTitle, String title, String overview, String releaseDate, String posterPath, double popularity, String tag) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.originalTitle = originalTitle;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.tag = tag;
    }

    public MovieModel() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }
}
