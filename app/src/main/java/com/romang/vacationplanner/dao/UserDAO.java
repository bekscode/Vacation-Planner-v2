package com.romang.vacationplanner.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.romang.vacationplanner.entities.User;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Query("SELECT * FROM USERS WHERE USERNAME = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM USERS")
    List<User> getAllUsers();
}
