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
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    String vacationShare;
    EditText editTitle;
    EditText editHotel;
    EditText editVacationStart;
    EditText editVacationEnd;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);


        ViewCompat.setOnApplyWindowInsetsListener(

                findViewById(R.id.main), (v, insets) ->

                {
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
        editTitle.setText(title);
        editHotel.setText(hotel);
        editVacationStart.setText(vacationStart);
        editVacationEnd.setText(vacationEnd);

        editVacationStart.setOnClickListener(v -> showDate(editVacationStart));
        editVacationEnd.setOnClickListener(v -> showDate(editVacationEnd));

        //pre-populate calendar with today's date
        Calendar calendar = Calendar.getInstance();
        String today = String.format(Locale.US, "%02d/%02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
        editVacationStart.setText(today);
        editVacationEnd.setText(today);


        FloatingActionButton fab = findViewById(R.id.fabVacationDetails);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });

        //excursion recycler view for showing associated excursions
        RecyclerView recyclerView = findViewById(R.id.vacationDetailsRecyclerView);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getmAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
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
            Vacation vacation;
            if (vacationID == -1) {
                if (repository.getmAllVacations().isEmpty()) vacationID = 1;
                else
                    vacationID = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(), editVacationStart.getText().toString(), editVacationEnd.getText().toString());
                repository.insert(vacation);
                this.finish();
            }
            //update vacation
            else {
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(), editVacationStart.getText().toString(), editVacationEnd.getText().toString());
                repository.update(vacation);
                this.finish();
            }
        }

        //delete functionality
        if (item.getItemId() == R.id.vacation_delete) {
            Vacation vacation;
            vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(), editVacationStart.getText().toString(), editVacationEnd.getText().toString());
            Toast.makeText(VacationDetails.this, "Vacation deleted.", Toast.LENGTH_LONG).show();
            //delete validation
            if (repository.getmAssociatedExcursions(vacationID).isEmpty()) {
                repository.delete(vacation);
            } else {
                Toast.makeText(VacationDetails.this, "Unable to delete. This vacation has an associated excursion.", Toast.LENGTH_LONG).show();
            }
        }

        //alert functionality for vacation
        if (item.getItemId() == R.id.vacation_notify) {
            String dateVacationStart = editVacationStart.getText().toString();
            String dateVacationEnd = editVacationEnd.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date notifyVacationStart = null;
            Date notifyVacationEnd = null;
            try {
                notifyVacationStart = sdf.parse(dateVacationStart);
                notifyVacationEnd = sdf.parse(dateVacationEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //vacation start alert
            if (notifyVacationStart != null) {
                Long startTrigger = notifyVacationStart.getTime();
                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                String vacationStartNotify = "Your vacation: " + title + " is starting";
                intent.putExtra("notification", vacationStartNotify);
                PendingIntent startSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, startSender);
            }
            //vacation end alert
            if (notifyVacationEnd != null) {
                Long startTrigger = notifyVacationEnd.getTime();
                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                String vacationEndNotify = "Your vacation: " + title + " is ending";
                intent.putExtra("notification", vacationEndNotify);
                PendingIntent endSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, endSender);
            }
        }

        //vacation share
        if (item.getItemId() == R.id.vacation_share){
            vacationShare = "Vacation Title: " + editTitle.getText().toString() +
                            "\nHotel: " + editHotel.getText().toString() +
                            "\nStart Date: " + editVacationStart.getText().toString() +
                            "\nEnd Date: " + editVacationEnd.getText().toString();
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TITLE, editTitle.getText().toString() + " Details");
            sentIntent.putExtra(Intent.EXTRA_TEXT, vacationShare);
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;

        }
        return true;
    }

    //date format validation
    private void showDate(EditText targetedEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            String formattedDate = String.format(Locale.US, "%02d/%02d/%02d", selectedMonth + 1, selectedDayOfMonth, selectedYear % 100);
            targetedEditText.setText(formattedDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
            simpleDateFormat.setLenient(false);

            //start and end date validation
            try {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.setTime(simpleDateFormat.parse(formattedDate));

                Calendar startDate = Calendar.getInstance();
                startDate.setTime(simpleDateFormat.parse(editVacationStart.getText().toString()));

                Calendar endDate = Calendar.getInstance();
                endDate.setTime(simpleDateFormat.parse(editVacationEnd.getText().toString()));

                if (targetedEditText == editVacationEnd) {
                    if (selectedDate.before(startDate)) {
                        targetedEditText.setError("End Date must come after Start Date.");
                        Toast.makeText(this, "End Date must come after Start Date.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                targetedEditText.setError(null);
                targetedEditText.setText(formattedDate);

                //catch any unexpected errors
            } catch (ParseException e) {
                targetedEditText.setError("Invalid Date");
                Toast.makeText(this, "Invalid Date", Toast.LENGTH_LONG).show();
            }
        },
                year, month, dayOfMonth
        );
        datePickerDialog.show();

    }
}
