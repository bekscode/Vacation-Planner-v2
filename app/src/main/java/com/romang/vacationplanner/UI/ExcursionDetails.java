package com.romang.vacationplanner.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        repository = new Repository(getApplication());

        editExcursionTitle = findViewById(R.id.excursionTitleText);
        editExcursionDate = findViewById(R.id.excursionDateInput);

        //get values from intent
        excursionTitle = getIntent().getStringExtra("title");
        excursionDate = getIntent().getStringExtra("excursionDate");
        excursionID = getIntent().getIntExtra("excursionID", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        vacationStart = getIntent().getStringExtra("vacationStart");
        vacationEnd = getIntent().getStringExtra("vacationEnd");

        if (excursionTitle != null) editExcursionTitle.setText(excursionTitle);
        if (excursionDate != null) editExcursionDate.setText(excursionDate);

        editExcursionDate.setOnClickListener(v -> showDate(editExcursionDate));
    }

    //menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    //save and update functionality
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //save new excursion
        if (item.getItemId() == R.id.excursion_save) {
            String title = editExcursionTitle.getText().toString();
            String excursionDate = editExcursionDate.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

            //require all fields to be entered before saving
            if (title.isEmpty() || excursionDate.isEmpty()) {
                Toast.makeText(this,
                        "All fields are required",
                        Toast.LENGTH_LONG).show();
                return true;
            }
            //excursion date validation
            try {
                Date dateCheck = sdf.parse(excursionDate);
                Date vacationStartCheck = sdf.parse(vacationStart);
                Date vacationEndCheck = sdf.parse(vacationEnd);

                //checks the excursion occurs after vacation start date
                if (dateCheck.before(vacationStartCheck)) {
                    Toast.makeText(this,
                            "Excursion cannot occur before vacation begins",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
                //checks the excursion occurs before vacation end date
                if (dateCheck.after(vacationEndCheck)) {
                    Toast.makeText(this,
                            "Excursion cannot occur after vacation ends",
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

            Excursion excursion;
            if (excursionID == -1) {
                if (repository.getmAllExcursions().isEmpty())
                    excursionID = 1;
                else
                    excursionID = repository.getmAllExcursions()
                            .get(repository.getmAllExcursions().size() - 1)
                            .getExcursionID() + 1;

                excursion = new Excursion(
                        excursionID,
                        editExcursionTitle.getText().toString(),
                        editExcursionDate.getText().toString(),
                        vacationID
                );
                repository.insert(excursion);
                this.finish();
            }

            //update excursion
            else {
                excursion = new Excursion(
                        excursionID,
                        editExcursionTitle.getText().toString(),
                        editExcursionDate.getText().toString(),
                        vacationID
                );
                repository.update(excursion);
                this.finish();
            }
        }

        //delete excursion
        if (item.getItemId() == R.id.excursion_delete) {
            Excursion excursion;
            excursion = new Excursion(
                    excursionID,
                    editExcursionTitle.getText().toString(),
                    editExcursionDate.getText().toString(),
                    vacationID
            );
            Toast.makeText(ExcursionDetails.this,
                    "Excursion Deleted.",
                    Toast.LENGTH_LONG).show();

            repository.delete(excursion);
            this.finish();
        }

        //alert functionality for excursion
        if (item.getItemId() == R.id.excursion_notify) {
            String dateExcursionStart = editExcursionDate.getText().toString();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date notifyExcursionStart = null;

            try {
                notifyExcursionStart = sdf.parse(dateExcursionStart);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //excursion start alert
            Long trigger = notifyExcursionStart.getTime();
            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
            String excursionNotify = "Your excursion: " + editExcursionTitle.getText().toString() + " is today";
            intent.putExtra("notification", excursionNotify);

            PendingIntent sender = PendingIntent.getBroadcast(
                    ExcursionDetails.this,
                    ++MainActivity.numAlert,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);

        }
        //back arrow navigation
        if (item.getItemId() == android.R.id.home) {
            this.finish();
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
                    selectedYear % 100);

            targetedEditText.setText(formattedDate);
            targetedEditText.setError(null);

            if (!formattedDate.matches(("\\d{2}/\\d{2}/\\d{2}"))) {
                targetedEditText.setError("Invalid date.");
            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();

    }
}