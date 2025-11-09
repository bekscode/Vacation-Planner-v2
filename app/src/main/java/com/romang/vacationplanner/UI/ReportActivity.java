package com.romang.vacationplanner.UI;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.romang.vacationplanner.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private TableLayout reportTable;
    private String reportText;
    private String[][] tableData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        reportTable = findViewById(R.id.reportTable);

        // Get the report text from the intent
        reportText = getIntent().getStringExtra("reportText");
        if (reportText != null) {
            populateTable(reportText);
        }

        // Download button functionality
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(v -> generatePDF());
    }


    private void populateTable(String csvText) {
        String[] lines = csvText.split("\n");
        tableData = new String[lines.length][];

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] columns = line.split(",");
            tableData[i] = columns;

            TableRow row = new TableRow(this);
            for (String column : columns) {
                TextView cell = new TextView(this);
                cell.setText(column.trim());
                cell.setPadding(16, 12, 16, 12);
                cell.setTextSize(14);
                cell.setGravity(Gravity.CENTER);

                if (i == 0) {
                    cell.setTypeface(null, Typeface.BOLD);
                    cell.setBackgroundColor(0xFFE8F5E9);
                }
                row.addView(cell);
            }
            reportTable.addView(row);
        }
    }


    private void generatePDF() {
        if (tableData == null || tableData.length == 0) {
            Toast.makeText(this, "No data available to generate report.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create cache directory
        File cachePath = new File(getCacheDir(), "reports");
        if (!cachePath.exists()) {
            cachePath.mkdirs();
        }

        // Generate filename with timestamp to prevent file overwrite
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File pdfFile = new File(cachePath, "vacation_report_" + timestamp + ".pdf");

        try {
            // Initialize PDF writer and document
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            Paragraph title = new Paragraph("Vacation Report")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            // Add generation date
            String dateStr = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(new Date());
            Paragraph dateInfo = new Paragraph("Generated on: " + dateStr)
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(dateInfo);

            // Create table with appropriate number of columns
            int numColumns = tableData[0].length;
            Table table = new Table(UnitValue.createPercentArray(numColumns))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            // Add header row with styling
            for (String header : tableData[0]) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(header.trim()).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(8);
                table.addHeaderCell(headerCell);
            }

            // Add data rows
            for (int i = 1; i < tableData.length; i++) {
                for (String cellData : tableData[i]) {
                    Cell dataCell = new Cell()
                            .add(new Paragraph(cellData.trim()))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(6);
                    table.addCell(dataCell);
                }
            }

            document.add(table);

            // Add footer
            Paragraph footer = new Paragraph("End of Report")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);


            document.close();

            Toast.makeText(this, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
            openPDF(pdfFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void openPDF(File pdfFile) {
        Uri fileUri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(Intent.createChooser(intent, "Open report with..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to view PDF.", Toast.LENGTH_LONG).show();
        }
    }


    private void sharePDF(File pdfFile) {
        Uri fileUri = FileProvider.getUriForFile(
                this,
                getApplicationContext().getPackageName() + ".provider",
                pdfFile
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation Report");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Please find attached vacation report.");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(shareIntent, "Share report via..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to share PDF.", Toast.LENGTH_LONG).show();
        }
    }
}