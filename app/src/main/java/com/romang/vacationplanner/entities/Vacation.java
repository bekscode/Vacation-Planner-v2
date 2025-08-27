package com.romang.vacationplanner.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    private int vacationID;
    private String vacationTitle;
    private String vacationHotel;
    private String vacationStart;
    private String vacationEnd;

    //Generated constructor
    public Vacation(int vacationID, String vacationTitle, String vacationHotel, String vacationStart, String vacationEnd) {
        this.vacationID = vacationID;
        this.vacationTitle = vacationTitle;
        this.vacationHotel = vacationHotel;
        this.vacationStart = vacationStart;
        this.vacationEnd = vacationEnd;
    }

    //Generated getters and setters
    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getVacationTitle() {
        return vacationTitle;
    }

    public void setVacationTitle(String vacationTitle) {
        this.vacationTitle = vacationTitle;
    }

    public String getVacationHotel() {
        return vacationHotel;
    }

    public void setVacationHotel(String vacationHotel) {
        this.vacationHotel = vacationHotel;
    }

    public String getVacationStart() {
        return vacationStart;
    }

    public void setVacationStart(String vacationStart) {
        this.vacationStart = vacationStart;
    }

    public String getVacationEnd() {
        return vacationEnd;
    }

    public void setVacationEnd(String vacationEnd) {
        this.vacationEnd = vacationEnd;
    }
}
