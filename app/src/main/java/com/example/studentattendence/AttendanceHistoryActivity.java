package com.example.mad; // <<< change package

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AttendanceHistoryActivity extends AppCompatActivity {

    TextView historyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyText = findViewById(R.id.historyText);
        historyText.setText(AttendanceStorage.getAll(this));
    }
}
