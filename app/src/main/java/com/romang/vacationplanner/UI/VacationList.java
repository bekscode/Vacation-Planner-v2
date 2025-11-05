package com.romang.vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romang.vacationplanner.R;
import com.romang.vacationplanner.database.Repository;
import com.romang.vacationplanner.entities.Vacation;

import java.util.List;

import androidx.appcompat.widget.SearchView;

import java.util.concurrent.Executors;

import android.widget.Button;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private VacationAdapter vacationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);

        Button addVacation = findViewById(R.id.addVacationButton);
        addVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.vacationListRecyclerView);
        repository = new Repository(getApplication());
        List<Vacation> allVacations = repository.getmAllVacations();
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);

        //generate report
        Button generateButton = findViewById(R.id.reportButton);
        generateButton.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                List<Vacation> vacations = repository.getmAllVacations();

                generateReport(vacations);
            });
        });

    }

    //menu search option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vacation_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    vacationAdapter.filter(newText);
                    return true;
                }
            });
        }
        return true;
    }

    //generate a report from the vacations table
    private void generateReport(List<Vacation> vacations) {
        Intent intent = getIntent(vacations);

        runOnUiThread(() -> startActivity(intent));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @NonNull
    private Intent getIntent(List<Vacation> vacations) {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Vacation Title, Start Date, End Date\n");

        for (Vacation vacation : vacations) {
            reportBuilder.append(vacation.getVacationTitle()).append(", ")
                    .append(vacation.getVacationStart()).append(", ")
                    .append(vacation.getVacationEnd()).append("\n");
        }

        String reportText = reportBuilder.toString();
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("reportText", reportText);
        return intent;
    }


    @Override
    protected void onResume() {
        super.onResume();
        List<Vacation> allVacations = repository.getmAllVacations();
        RecyclerView recyclerView = findViewById(R.id.vacationListRecyclerView);
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);
    }
}