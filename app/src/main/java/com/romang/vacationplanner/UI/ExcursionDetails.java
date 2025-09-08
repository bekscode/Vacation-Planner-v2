package com.romang.vacationplanner.UI;

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

public class ExcursionDetails extends AppCompatActivity {
    String excursionTitle;
    String excursionDate;
    int excursionID;
    int vacationID;
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
        vacationID = getIntent().getIntExtra("vacationID", 1);
        editExcursionTitle.setText(excursionTitle);
        editExcursionDate.setText(excursionDate);

        repository = new Repository(getApplication());

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

        return true;
    }
}