package com.romang.vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.List;

public class VacationList extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);

        FloatingActionButton fab = findViewById(R.id.fabVacationList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.vacationListRecyclerView);
        repository = new Repository(getApplication());
        List<Vacation> allVacations = repository.getmAllVacations();
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu){
            getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
            return true;
        }

//        @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//            if (item.getItemId() == R.id.sample) {
//                repository=new Repository(getApplication());
//                //Toast.makeText(VacationList.this, "put in sample data", Toast.LENGTH_LONG).show();
//                Vacation vacation=new Vacation(0, "DragonCon", "Westin", "08/28/25", "09/01/25");
//                repository.insert(vacation);
//                vacation=new Vacation(0, "Pigeon Forge", "Margaritaville Island Inn", "07/02/26", "07/05/26");
//                repository.insert(vacation);
//                Excursion excursion= new Excursion(0,"Dollywood", "07/03/26", 1);
//                repository.insert(excursion);
//                excursion=new Excursion(0,"Mini Golf", "07/04/26",1);
//                repository.insert(excursion);
//                return true;
//            }
//            if (item.getItemId()==android.R.id.home){
//                this.finish();
//                return true;
//            }
//            return true;
//
//        }
}