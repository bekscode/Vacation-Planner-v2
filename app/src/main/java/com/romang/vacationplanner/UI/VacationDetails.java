package com.romang.vacationplanner.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.romang.vacationplanner.R;
import com.romang.vacationplanner.database.Repository;
import com.romang.vacationplanner.entities.Excursion;
import com.romang.vacationplanner.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {
    String title;
    String hotel;
    String vacationStart;
    String vacationEnd;
    int vacationID;
    int excursionID;
    String vacationShare;

    EditText editTitle;
    EditText editHotel;
    EditText editVacationStart;
    EditText editVacationEnd;

    Repository repository;
    private ExcursionAdapter excursionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        editTitle = findViewById(R.id.vacationTitleText);
        editHotel = findViewById(R.id.hotelNameText);
        editVacationStart = findViewById(R.id.vacationStartInput);
        editVacationEnd = findViewById(R.id.vacationEndInput);

        title = getIntent().getStringExtra("title");
        hotel = getIntent().getStringExtra("hotel");
        vacationID = getIntent().getIntExtra("id", -1);
        vacationStart = getIntent().getStringExtra("vacationStart");
        vacationEnd = getIntent().getStringExtra("vacationEnd");
        excursionID = getIntent().getIntExtra("excursionID", -1);

        if (title != null) editTitle.setText(title);
        if (hotel != null) editHotel.setText(hotel);
        if (vacationStart != null) editVacationStart.setText(vacationStart);
        if (vacationEnd != null) editVacationEnd.setText(vacationEnd);

        editVacationStart.setOnClickListener(v -> showDate(editVacationStart));
        editVacationEnd.setOnClickListener(v -> showDate(editVacationEnd));


        Button btn = findViewById(R.id.addExcursionButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = editVacationStart.getText().toString();
                String end = editVacationEnd.getText().toString();

                if (start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(VacationDetails.this,
                            "Please select start and end dates.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacationID", vacationID);
                intent.putExtra("vacationStart", start);
                intent.putExtra("vacationEnd", end);
                intent.putExtra("excursionID", excursionID);
                startActivity(intent);
            }
        });

        //excursion recycler view for showing associated excursions
        repository = new Repository(getApplication());
        RecyclerView recyclerView = findViewById(R.id.vacationDetailsRecyclerView);
        excursionAdapter = new ExcursionAdapter(this, vacationStart, vacationEnd);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadExcursions();
    }

    //menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }


    //save and update functionality
    public boolean onOptionsItemSelected(MenuItem item) {
        //save new vacation
        if (item.getItemId() == R.id.vacation_save) {
            String title = editTitle.getText().toString();
            String hotel = editHotel.getText().toString();
            String start = editVacationStart.getText().toString();
            String end = editVacationEnd.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

            //require all fields to be entered before saving
            if (title.isEmpty() || hotel.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this,
                        "All fields are required",
                        Toast.LENGTH_LONG).show();
                return true;
            }
            //check the end date occurs after the start date
            try {
                Date vacationStartCheck = sdf.parse(start);
                Date vacationEndCheck = sdf.parse(end);

                if (vacationStartCheck != null && vacationEndCheck != null & vacationStartCheck.after(vacationEndCheck)) {
                    Toast.makeText(this,
                            "Start date must come before end date.",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            //catch all other date exceptions
            catch (ParseException e) {
                Toast.makeText(this,
                        "Invalid date",
                        Toast.LENGTH_LONG).show();
                return true;
            }
            //write to the database
            Vacation vacation;
            if (vacationID == -1) {
                if (repository.getmAllVacations().isEmpty()) {
                    vacationID = 1;
                } else {
                    vacationID = repository.getmAllVacations()
                            .get(repository.getmAllVacations().size() - 1)
                            .getVacationID() + 1;
                }
                vacation = new Vacation(vacationID, title, hotel, start, end);
                repository.insert(vacation);

                //update vacation
            } else {
                vacation = new Vacation(vacationID, title, hotel, start, end);
                repository.update(vacation);
            }
            this.finish();
        }


        //delete functionality
        if (item.getItemId() == R.id.vacation_delete) {
            Vacation vacation = new Vacation(
                    vacationID,
                    editTitle.getText().toString(),
                    editHotel.getText().toString(),
                    editVacationStart.getText().toString(),
                    editVacationEnd.getText().toString()
            );
            //delete validation
            if (repository.getmAssociatedExcursions(vacationID).isEmpty()) {
                repository.delete(vacation);
                Toast.makeText(VacationDetails.this,
                        "Vacation deleted",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VacationDetails.this,
                        "Unable to delete. This vacation has an associated excursion.",
                        Toast.LENGTH_LONG).show();
            }
            this.finish();
        }


        //alert functionality for vacation
        if (item.getItemId() == R.id.vacation_notify) {
            String title = editTitle.getText().toString();
            String startDate = editVacationStart.getText().toString();
            String endDate = editVacationEnd.getText().toString();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date notifyVacationStart = null;
            Date notifyVacationEnd = null;

            try {
                notifyVacationStart = sdf.parse(startDate);
                notifyVacationEnd = sdf.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //vacation start alert
            if (notifyVacationStart != null) {
                Long startTrigger = notifyVacationStart.getTime();
                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                String vacationStartNotify = "Your vacation: " + title + " is starting";
                intent.putExtra("notification", vacationStartNotify);

                PendingIntent startSender = PendingIntent.getBroadcast(
                        VacationDetails.this,
                        ++MainActivity.numAlert,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT
                );

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, startSender);
            }

            //vacation end alert
            if (notifyVacationEnd != null) {
                Long endTrigger = notifyVacationEnd.getTime();
                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                String vacationEndNotify = "Your vacation: " + title + " is ending";
                intent.putExtra("notification", vacationEndNotify);

                PendingIntent endSender = PendingIntent.getBroadcast(
                        VacationDetails.this,
                        ++MainActivity.numAlert,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, endTrigger, endSender);
            }
        }


        //vacation share
        if (item.getItemId() == R.id.vacation_share) {
            Vacation vacation = repository.getVacationById(vacationID);
            if (vacation != null) {
                StringBuilder shareBuilder = new StringBuilder();
                shareBuilder.append("Vacation Title: ").append(vacation.getVacationTitle())
                        .append("\nHotel: ").append(vacation.getVacationHotel())
                        .append("\nStart Date: ").append(vacation.getVacationStart())
                        .append("\nEnd Date: ").append(vacation.getVacationEnd());
                //excursion info for sharing
                List<Excursion> excursions = repository.getmAssociatedExcursions(vacationID);
                if (excursions != null && !excursions.isEmpty()) {
                    shareBuilder.append("\n\nAssociated Excursions: ");
                    for (Excursion excursion : excursions) {
                        shareBuilder.append("\n ")
                                .append(excursion.getExcursionTitle())
                                .append(" on ")
                                .append(excursion.getExcursionDate());
                    }
                } else {
                    shareBuilder.append("\n\n No excursions scheduled during this vacation.");
                }

                vacationShare = shareBuilder.toString();

                Intent sentIntent = new Intent();
                sentIntent.setAction(Intent.ACTION_SEND);
                sentIntent.putExtra(Intent.EXTRA_TITLE, vacation.getVacationTitle() + " Details");
                sentIntent.putExtra(Intent.EXTRA_TEXT, vacationShare);
                sentIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sentIntent, null);
                startActivity(shareIntent);
            } else {
                Toast.makeText(this,
                        "No vacation found.",
                        Toast.LENGTH_LONG).show();
            }
            return true;
        }
        //fix back navigation
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return true;

    }

    //display associated excursions in the recycler view
    private void loadExcursions() {
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getmAssociatedExcursions(vacationID)) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }

    //date format validation
    private void showDate(EditText targetedEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (
                view,
                selectedYear,
                selectedMonth,
                selectedDayOfMonth) -> {

            String formattedDate = String.format(Locale.US,
                    "%02d/%02d/%02d",
                    selectedMonth + 1,
                    selectedDayOfMonth,
                    selectedYear % 100
            );

            targetedEditText.setText(formattedDate);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            sdf.setLenient(false);

            //start and end date validation
            try {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.setTime(sdf.parse(formattedDate));

                if (targetedEditText == editVacationEnd) {
                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime(sdf.parse(editVacationStart.getText().toString()));
                    if (selectedDate.before(startDate)) {
                        targetedEditText.setError("Invalid Start Date");
                        Toast.makeText(this,
                                "Start date must come before end date.",
                                Toast.LENGTH_LONG).show();
                    }
                }
                targetedEditText.setError(null);

                //catch any unexpected errors
            } catch (ParseException e) {
                targetedEditText.setError("Invalid Date");
                Toast.makeText(this,
                        "Invalid Date.",
                        Toast.LENGTH_LONG).show();
            }
        },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }

    public String getStartDate() {

        return vacationStart;
    }

    public String getEndDate() {

        return vacationEnd;
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadExcursions();

        //repopulate the vacation fields from the repo
        Vacation vacation = repository.getVacationById(vacationID);
        if (vacation != null) {
            editTitle.setText(vacation.getVacationTitle());
            editHotel.setText(vacation.getVacationHotel());
            editVacationStart.setText(vacation.getVacationStart());
            editVacationEnd.setText(vacation.getVacationEnd());
        }
    }
}
