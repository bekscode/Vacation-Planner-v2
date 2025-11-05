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
import java.util.concurrent.Executors;

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
            final String title = editExcursionTitle.getText().toString();
            final String excursionDate = editExcursionDate.getText().toString();
            final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);


            if (title.isEmpty() || excursionDate.isEmpty()) {
                Toast.makeText(this,
                        "All fields required",
                        Toast.LENGTH_LONG).show();
                return true;
            }
            try {
                Date dateCheck = sdf.parse(excursionDate);
                Date vacationStartCheck = sdf.parse(vacationStart);
                Date vacationEndCheck = sdf.parse(vacationEnd);

                if (dateCheck != null && dateCheck.before(vacationStartCheck)) {
                    Toast.makeText(this,
                            "Excursion cannot occur before vacation begins",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
                if (dateCheck != null && dateCheck.after(vacationEndCheck)) {
                    Toast.makeText(this,
                            "Excursion cannot occur after vacation ends",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            } catch (ParseException e) {
                Toast.makeText(this,
                        "Invalid date",
                        Toast.LENGTH_LONG).show();
                return true;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                Excursion excursion;
                int currentExcursionID = excursionID;

                if (currentExcursionID == -1) {
                    if (repository.getmAllExcursions().isEmpty())
                        currentExcursionID = 1;
                    else
                        currentExcursionID = repository.getmAllExcursions()
                                .get(repository.getmAllExcursions().size() - 1)
                                .getExcursionID() + 1;

                    excursion = new Excursion(
                            currentExcursionID, title, excursionDate, vacationID
                    );
                    repository.insert(excursion);

                } else { // update excursion
                    excursion = new Excursion(
                            currentExcursionID, title, excursionDate, vacationID
                    );
                    repository.update(excursion);
                }

                // Finish the activity on the main thread
                runOnUiThread(this::finish);
            });

            return true;
        }

        //delete excursion
        if (item.getItemId() == R.id.excursion_delete) {
            final Excursion excursion = new Excursion(
                    excursionID,
                    editExcursionTitle.getText().toString(),
                    editExcursionDate.getText().toString(),
                    vacationID
            );


            Executors.newSingleThreadExecutor().execute(() -> {
                repository.delete(excursion);

                // UI updates must run on the main thread
                runOnUiThread(() -> {
                    Toast.makeText(ExcursionDetails.this,
                            "Excursion Deleted.",
                            Toast.LENGTH_LONG).show();
                    this.finish(); // Finish after confirming deletion
                });
            });

            return true;
        }

        //alert functionality for excursion (No database calls, safe to keep on main thread)
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
            if (notifyExcursionStart != null) {
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
            return true;
        }

        //back arrow navigation
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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