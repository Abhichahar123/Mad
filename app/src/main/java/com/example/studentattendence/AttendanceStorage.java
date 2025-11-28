package com.example.mad; // <<< change package

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendanceStorage {

    private static final String PREF_NAME = "attendance_pref";
    private static final String KEY_RECORDS = "records";

    public static void save(Context context, String subject, String timestamp) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        long ts = Long.parseLong(timestamp);
        String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                .format(new Date(ts));

        String record = subject + " | " + dateTime + "\n";

        String old = sp.getString(KEY_RECORDS, "");
        sp.edit().putString(KEY_RECORDS, old + record).apply();
    }

    public static String getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_RECORDS, "No attendance records yet.");
    }
}
