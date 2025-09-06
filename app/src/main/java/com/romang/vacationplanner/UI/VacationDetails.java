package com.romang.vacationplanner.UI;

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

import java.util.ArrayList;
import java.util.List;

public class VacationDetails extends AppCompatActivity {
    String title;
    String hotel;
    String vacationStart;
    String vacationEnd;
    int vacationID;
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
        editVacationStart = findViewById(R.id.vacationStartText);
        editVacationEnd = findViewById(R.id.vacationEndText);
        title = getIntent().getStringExtra("title");
        hotel = getIntent().getStringExtra("hotel");
        vacationID = getIntent().getIntExtra("id", -1);
        vacationStart = getIntent().getStringExtra("vacationStart");
        vacationEnd = getIntent().getStringExtra("vacationEnd");
        editTitle.setText(title);
        editHotel.setText(hotel);
        editVacationStart.setText(vacationStart);
        editVacationEnd.setText(vacationEnd);

        FloatingActionButton fab = findViewById(R.id.fabVacationDetails);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });

        //excursion recycler view
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
            Toast.makeText(VacationDetails.this,"Vacation deleted.", Toast.LENGTH_LONG).show();
            //delete validation
            if (repository.getmAssociatedExcursions(vacationID).isEmpty()) {
                repository.delete(vacation);
            }
            else {
                Toast.makeText(VacationDetails.this, "Unable to delete. This vacation has an associated excursion.", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }
}