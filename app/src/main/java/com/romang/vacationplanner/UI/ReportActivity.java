package com.romang.vacationplanner.UI;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.romang.vacationplanner.R;

import java.io.IOException;
import java.io.OutputStream;

public class ReportActivity extends AppCompatActivity {

    private TableLayout reportTable;
    private String reportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Back button functionality
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vacations Report");

        reportTable = findViewById(R.id.reportTable);

        // Get the report text from the intent
        reportText = getIntent().getStringExtra("reportText");
        if (reportText != null) {
            populateTable(reportText);
        }

        // Download button functionality
        ImageButton downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(v -> saveReportToDownloads(reportText));
    }

    private void populateTable(String csvText) {
        String[] lines = csvText.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] columns = line.split(",");

            TableRow row = new TableRow(this);
            for (String column : columns) {
                TextView cell = new TextView(this);
                cell.setText(column.trim());
                cell.setPadding(8,8,8,8);
                cell.setTextSize(16);
                cell.setGravity(Gravity.CENTER);

                if (i == 0) {
                    cell.setTypeface(null, Typeface.BOLD);
                }

                row.addView(cell);
            }

            reportTable.addView(row);
        }
    }

    private void saveReportToDownloads(String reportText) {
        String fileName = "vacations_report.csv";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
        values.put(MediaStore.Downloads.IS_PENDING, 1);

        ContentResolver resolver = getContentResolver();
        Uri collection = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }
        Uri fileUri = resolver.insert(collection, values);

        try (OutputStream out = resolver.openOutputStream(fileUri)) {
            out.write(reportText.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Failed to save file.", Toast.LENGTH_LONG).show());
            return;
        }

        values.clear();
        values.put(MediaStore.Downloads.IS_PENDING, 0);
        resolver.update(fileUri, values, null, null);

        runOnUiThread(() -> Toast.makeText(this, "Report saved.", Toast.LENGTH_LONG).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}