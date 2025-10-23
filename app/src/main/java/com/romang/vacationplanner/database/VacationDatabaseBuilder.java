package com.romang.vacationplanner.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.romang.vacationplanner.dao.ExcursionDAO;
import com.romang.vacationplanner.dao.UserDAO;
import com.romang.vacationplanner.dao.VacationDAO;
import com.romang.vacationplanner.entities.Excursion;
import com.romang.vacationplanner.entities.User;
import com.romang.vacationplanner.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class, User.class}, version = 15, exportSchema = false)

public abstract class VacationDatabaseBuilder extends RoomDatabase {
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    public abstract UserDAO userDAO();
    private static volatile VacationDatabaseBuilder INSTANCE;

    //Asynchronous database builder
    static VacationDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), VacationDatabaseBuilder.class, "MyVacationDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
