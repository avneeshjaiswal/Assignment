package com.inventory.assignment.roomDb;

import android.arch.persistence.room.RoomDatabase;

import com.inventory.assignment.model.MovieModel;

@android.arch.persistence.room.Database(entities = {MovieModel.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract MovieDao movieDao();
}
