package com.romang.vacationplanner.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.romang.vacationplanner.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {

    //Database operations for insert, update, and delete
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    //Get all excursions from the database
    @Query("SELECT * FROM EXCURSIONS ORDER BY excursionID ASC")
    List<Excursion> getAllExcursions();

    //Get all excursions that are associated with a specific
    @Query("SELECT * FROM EXCURSIONS WHERE vacationID=:vacation ORDER BY excursionID ASC")
    List<Excursion> getAssociatedExcursions(int vacation);

    //Get excursion by ID
    @Query("SELECT * FROM EXCURSIONS WHERE excursionID = :id LIMIT 1")
    Excursion getExcursionById(int id);
}
