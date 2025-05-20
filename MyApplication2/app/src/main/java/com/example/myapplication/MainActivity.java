package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText credit, interest, termM, termMon, delay, deferralMonth, prad, pabaig;
    private Spinner gChoice;
    private Button cal, filter, showGraph, save;
    private RecyclerView tableView;
    private LineChart lineChart;

    private Loan loan;
    private List<LoanPayment> latestPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bindViews();

        setupSpinner();

        cal.setOnClickListener(v -> calculateLoan());

        filter.setOnClickListener(v -> applyFilter());

        showGraph.setOnClickListener(v -> {
            if (latestPayments != null && !latestPayments.isEmpty()) {
                showLoanGraph(latestPayments);
            }
        });

        save.setOnClickListener(v -> saveReport());
    }

    private void bindViews() {
        credit = findViewById(R.id.credit);
        interest = findViewById(R.id.interest);
        termM = findViewById(R.id.termM);
        termMon = findViewById(R.id.termMon);
        delay = findViewById(R.id.delay);
        deferralMonth = findViewById(R.id.deferralMonth);
        prad = findViewById(R.id.prad);
        pabaig = findViewById(R.id.pabaig);

        cal = findViewById(R.id.cal);
        filter = findViewById(R.id.filter);
        showGraph = findViewById(R.id.showGraph);
        save = findViewById(R.id.save);
        gChoice = findViewById(R.id.gChoice);

        tableView = findViewById(R.id.tableView);
        lineChart = findViewById(R.id.lineChart);
        tableView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.loan_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gChoice.setAdapter(adapter);
    }

    private void calculateLoan() {
        double amount = parseDoubleOrDefault(credit.getText().toString(), 0);
        double proc = parseDoubleOrDefault(interest.getText().toString(), 0);
        int years = parseIntOrDefault(termM.getText().toString(), 0);
        int months = parseIntOrDefault(termMon.getText().toString(), 0);
        int del = parseIntOrDefault(delay.getText().toString(), 0);
        int defMonth = parseIntOrDefault(deferralMonth.getText().toString(), 1);
        String type = gChoice.getSelectedItem().toString();

        int totalMonths = years * 12 + months;

        if (defMonth < 1 || defMonth > totalMonths) {
            Toast.makeText(this, "Netinkamas atidėjimo mėnuo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (del < 0 || del > totalMonths - defMonth + 1) {
            Toast.makeText(this, "Netinkama atidėjimo trukmė", Toast.LENGTH_SHORT).show();
            return;
        }

        loan = type.equals("Anuitetas")
                ? new Anuites(amount, years, months, del, defMonth, proc)
                : new Linijins(amount, years, months, del, defMonth, proc);

        latestPayments = loan.calculatePayments();
        tableView.setAdapter(new LoanPaymentAdapter(latestPayments));
    }

    private void applyFilter() {
        int start = parseIntOrDefault(prad.getText().toString(), 1);
        int end = parseIntOrDefault(pabaig.getText().toString(), Integer.MAX_VALUE);

        if (latestPayments == null) return;

        List<LoanPayment> filtered = new ArrayList<>();
        for (LoanPayment p : latestPayments) {
            if (p.getMonth() >= start && p.getMonth() <= end) {
                filtered.add(p);
            }
        }

        tableView.setAdapter(new LoanPaymentAdapter(filtered));
        showLoanGraph(filtered);
    }

    private void saveReport() {
        if (loan == null) {
            Toast.makeText(this, "Pirmiausia apskaičiuokite paskolą", Toast.LENGTH_SHORT).show();
            return;
        }

        List<LoanPayment> payments = loan.calculatePayments();
        StringBuilder sb = new StringBuilder();

        for (LoanPayment p : payments) {
            sb.append("Mėn. ").append(p.getMonth())
                    .append("\nLikutis: ").append(p.getRemainder())
                    .append("\nĮmoka: ").append(p.getPayment()).append("\n\n");
        }

        try {
            File file = new File(getExternalFilesDir(null), "ataskaita.txt");
            FileOutputStream out = new FileOutputStream(file);
            out.write(sb.toString().getBytes());
            out.close();
            Toast.makeText(this, "Išsaugota: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Klaida išsaugant", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoanGraph(List<LoanPayment> payments) {
        List<Entry> entries = new ArrayList<>();
        for (LoanPayment p : payments) {
            entries.add(new Entry(p.getMonth(), (float) p.getPayment()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Mėnesinė įmoka");
        dataSet.setDrawValues(false);
        dataSet.setColor(android.graphics.Color.BLUE);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); //refreshina
    }

    private int parseIntOrDefault(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private double parseDoubleOrDefault(String s, double def) {
        try {
            return Double.parseDouble(s.trim().replace(',', '.'));
        } catch (Exception e) {
            return def;
        }
    }
}
