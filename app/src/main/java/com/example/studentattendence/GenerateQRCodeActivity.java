package com.example.studentattendence;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class GenerateQRCodeActivity extends AppCompatActivity {

    EditText subjectInput;
    Button generateBtn;
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        subjectInput = findViewById(R.id.subjectInput);
        generateBtn = findViewById(R.id.generateBtn);
        qrImage = findViewById(R.id.qrImage);

        generateBtn.setOnClickListener(v -> {
            String subject = subjectInput.getText().toString().trim();
            if (TextUtils.isEmpty(subject)) {
                Toast.makeText(this, "Enter subject name", Toast.LENGTH_SHORT).show();
                return;
            }

            String data = subject + "|" + System.currentTimeMillis();
            Bitmap bmp = generateQRCode(data);
            if (bmp != null) {
                qrImage.setImageBitmap(bmp);
            } else {
                Toast.makeText(this, "Error generating QR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap generateQRCode(String text) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.QR_CODE, 500, 500);

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
