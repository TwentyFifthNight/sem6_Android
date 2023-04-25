package com.example.aplikacja2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ElementDao {
    @Insert
    void insert(Phone phone);


    @Query("DELETE FROM Phones")
    void deleteAll();

    @Delete
    void delete(Phone phone);

    @Query("SELECT * FROM Phones ORDER BY Model ASC")
    LiveData<List<Phone>> getAlphabetizedElements();

    @Update
    void update(Phone phone);
}
