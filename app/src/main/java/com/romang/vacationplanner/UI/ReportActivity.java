package com.romang.vacationplanner.UI;

import android.content.ActivityNotFoundException;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.romang.vacationplanner.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ReportActivity extends AppCompatActivity {

    private TableLayout reportTable;
    private String reportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        reportTable = findViewById(R.id.reportTable);

        //get the report text from the intent
        reportText = getIntent().getStringExtra("reportText");
        if (reportText != null) {
            populateTable(reportText);
        }

        //download button functionality
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(v -> generatePDF(reportText));

    }

    //populate the table
    private void populateTable(String csvText) {
        String[] lines = csvText.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] columns = line.split(",");

            TableRow row = new TableRow(this);
            for (String column : columns) {
                TextView cell = new TextView(this);
                cell.setText(column.trim());
                cell.setPadding(8, 8, 8, 8);
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

    //generate a PDF from the report table
    private void generatePDF(String reportText) {
        //make sure there is a report
        if (reportTable.getChildCount() == 0) {
            Toast.makeText(this, "No data found.", Toast.LENGTH_LONG).show();
            return;
        }
        PdfDocument document = new PdfDocument();
        //page info for standard paper print out
        PageInfo pageInfo = new PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        //PDF drawing tools
        Paint textPaint = new Paint();
        textPaint.setTextSize(10f);
        int startX = 40;
        int startY = 40;
        int currentY = startY;
        final int LINE_HEIGHT = 16;
        final int MAX_Y = 800;

        //draw table
        for (int i = 0; i < reportTable.getChildCount(); i++) {
            TableRow row = (TableRow) reportTable.getChildAt(i);
            if (currentY > MAX_Y) {
                document.finishPage(page);
                pageInfo = new PageInfo.Builder(595, 842, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                currentY = startY;
            }

            //set headers
            if (i == 0) {
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            } else {
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            }

            int currentX = startX;
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                String text = cell.getText().toString();

                page.getCanvas().drawText(text, currentX, currentY, textPaint);
                currentX += (pageInfo.getPageWidth() - 2 * startX) / row.getChildCount();
            }

            currentY += LINE_HEIGHT;
        }
        document.finishPage(page);

        //cache the file
        File cachePath = new File(getCacheDir(), "reports");
        if (!cachePath.exists()) {
            cachePath.mkdirs();
        }
        File tempFile = new File(cachePath, "vacations_report.pdf");

        try (OutputStream out = new FileOutputStream(tempFile)) {
            document.writeTo(out);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to create PDF.", Toast.LENGTH_LONG).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(intent, "Open report with..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found for viewing PDF.", Toast.LENGTH_LONG).show();
        }
    }
}