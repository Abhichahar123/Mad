package com.example.mad; // <<< change package

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRCodeActivity extends AppCompatActivity {

    Button startScanBtn;
    IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        startScanBtn = findViewById(R.id.startScanBtn);

        startScanBtn.setOnClickListener(v -> {
            qrScan = new IntentIntegrator(this);
            qrScan.setPrompt("Scan Attendance QR");
            qrScan.setBeepEnabled(true);
            qrScan.setOrientationLocked(true);
            qrScan.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // Got QR data
                String qrData = result.getContents();
                saveAttendance(qrData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveAttendance(String data) {
        // Expected format: "subject|timestamp"
        String[] parts = data.split("\\|");
        String subject = parts.length > 0 ? parts[0] : "Unknown";
        String timestamp = parts.length > 1 ? parts[1] : String.valueOf(System.currentTimeMillis());

        AttendanceStorage.save(this, subject, timestamp);
        Toast.makeText(this, "Attendance marked for " + subject, Toast.LENGTH_LONG).show();
    }
}
