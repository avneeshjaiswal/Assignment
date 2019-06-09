package com.inventory.assignment.roomDb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.inventory.assignment.model.MovieModel;

import java.util.List;

@Dao
public interface MovieDao {

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)*/
    @Insert
    public void insertDat(MovieModel movieModel);

    @Query("select * from movies where tag = :datatag")
    public List<MovieModel> getPopularMovies(String datatag);


}
