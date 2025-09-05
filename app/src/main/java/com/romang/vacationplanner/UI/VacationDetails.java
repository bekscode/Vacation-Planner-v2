package com.romang.vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.romang.vacationplanner.R;
import com.romang.vacationplanner.database.Repository;

public class VacationDetails extends AppCompatActivity {
    String title;
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

        FloatingActionButton fab = findViewById(R.id.fabVacationDetails);
        editTitle = findViewById(R.id.vacationTitleText);
        editHotel = findViewById(R.id.hotelNameText);
        editVacationStart = findViewById(R.id.vacationStartText);
        editVacationEnd = findViewById(R.id.vacationEndText);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
        @Override
                public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
            return true;
        }
    }