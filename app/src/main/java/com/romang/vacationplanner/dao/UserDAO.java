package com.romang.vacationplanner.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.romang.vacationplanner.entities.User;

import java.util.List;

@Dao
public interface UserDAO {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);


    @Query("SELECT * FROM USERS WHERE USERNAME = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM USERS WHERE userID = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("SELECT * FROM USERS")
    List<User> getAllUsers();
}
