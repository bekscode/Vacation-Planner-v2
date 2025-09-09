package com.romang.vacationplanner.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.romang.vacationplanner.R;
import com.romang.vacationplanner.database.Repository;
import com.romang.vacationplanner.entities.Excursion;
import com.romang.vacationplanner.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {
    String excursionTitle;
    String excursionDate;
    int excursionID;
    int vacationID;
    String vacationStart;
    String vacationEnd;
    EditText editExcursionTitle;
    EditText editExcursionDate;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editExcursionTitle = findViewById(R.id.excursionTitleText);
        editExcursionDate = findViewById(R.id.excursionDateInput);

        excursionTitle = getIntent().getStringExtra("title");
        excursionDate = getIntent().getStringExtra("date");
        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        vacationStart = getIntent().getStringExtra("vacationStart");
        vacationEnd = getIntent().getStringExtra("vacationEnd");
        editExcursionTitle.setText(excursionTitle);
        editExcursionDate.setText(excursionDate);

        editExcursionDate.setOnClickListener(v -> showDate(editExcursionDate));
        repository = new Repository(getApplication());

        //pre-populate calendar with today's date
        if (vacationID == -1) {
        Calendar calendar = Calendar.getInstance();
        String today = String.format(Locale.US, "%02d/%02d/%02d", calendar.get(Calendar.MONTH) +1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
        editExcursionDate.setText(today);
        }

    }

    //menu inflater
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    //save and update functionality
    public boolean onOptionsItemSelected(MenuItem item) {
        //save new excursion
        if (item.getItemId() == R.id.excursion_save) {
            String excursionDate = editExcursionDate.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

            //excursion date validation
            try {
                Date dateCheck = sdf.parse(excursionDate);
                Vacation vacation = repository.getVacationById(vacationID);
                if (vacation == null) {
                    Toast.makeText(this, "Vacation not found", Toast.LENGTH_LONG).show();
                    return true;
                }

                Date vacationStartCheck = sdf.parse(vacation.getVacationStart());
                Date vacationEndCheck = sdf.parse(vacation.getVacationEnd());

                if (dateCheck.before(vacationStartCheck)) {
                    Toast.makeText(this, "Excursion cannot occur before vacation begins", Toast.LENGTH_LONG).show();
                    return true;
                }
                if (dateCheck.after(vacationEndCheck)) {
                    Toast.makeText(this, "Excursion cannot occur after vacation ends", Toast.LENGTH_LONG).show();
                    return true;
                }

            }
            catch (ParseException e) {
                Toast.makeText(this, "Invalid date", Toast.LENGTH_LONG).show();
                return true;
            }

            Excursion excursion;
            if (excursionID == -1) {
                if (repository.getmAllExcursions().isEmpty())
                    excursionID = 1;
                else
                    excursionID = repository.getmAllExcursions().get(repository.getmAllExcursions().size() - 1).getExcursionID() + 1;
                excursion = new Excursion(excursionID, editExcursionTitle.getText().toString(), editExcursionDate.getText().toString(), vacationID);
                repository.insert(excursion);
                this.finish();
            }
            //update excursion
            else {
                excursion = new Excursion(excursionID, editExcursionTitle.getText().toString(), editExcursionDate.getText().toString(), vacationID);
                repository.update(excursion);
                this.finish();
            }
        }

        //delete excursion
        if (item.getItemId() == R.id.excursion_delete) {
            Excursion excursion;
            excursion = new Excursion(excursionID, editExcursionTitle.getText().toString(), editExcursionDate.getText().toString(), vacationID);
            Toast.makeText(ExcursionDetails.this, "Excursion Deleted.", Toast.LENGTH_LONG).show();
            repository.delete(excursion);
            this.finish();
        }

        //alert functionality for excursion
        if(item.getItemId() == R.id.excursion_notify) {
            String dateExcursionStart = editExcursionDate.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date notifyExcursionStart = null;
            try {
                notifyExcursionStart = sdf.parse(dateExcursionStart);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (notifyExcursionStart != null) {
                Long trigger = notifyExcursionStart.getTime();
                Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                String excursionNotify = "Your excursion: " + editExcursionTitle.getText().toString() + " is today";
                intent.putExtra("notification", excursionNotify);
                PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            }
        }

        return true;
    }

    //date format validation
    private void showDate(EditText targetedEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth)-> {
            String formattedDate = String.format("%02d/%02d/%02d", selectedMonth + 1, selectedDayOfMonth, selectedYear % 100);
            targetedEditText.setText(formattedDate);
            targetedEditText.setError(null);

            if (!formattedDate.matches(("\\d{2}/\\d{2}/\\d{2}"))) {
                targetedEditText.setError("Invalid date.");
            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();

    }
}