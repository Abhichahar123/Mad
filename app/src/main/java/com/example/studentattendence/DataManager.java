package com.example.studentattendence;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static final String PREFS_NAME = "StudentAttendancePrefs";
    private static final String KEY_STUDENTS = "students";
    private static final String KEY_CURRENT_DATE = "current_date";
    private SharedPreferences prefs;
    private Gson gson;

    public DataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new GsonBuilder().create();
    }

    public void saveStudents(List<Student> students) {
        String json = gson.toJson(students);
        prefs.edit().putString(KEY_STUDENTS, json).apply();
    }

    public List<Student> loadStudents() {
        String json = prefs.getString(KEY_STUDENTS, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<Student>>(){}.getType();
            List<Student> students = gson.fromJson(json, type);
            if (students == null) {
                return new ArrayList<>();
            }
            // Reconstruct attendance history maps if needed
            for (Student student : students) {
                if (student.getAttendanceHistory() == null) {
                    student.setAttendanceHistory(new HashMap<>());
                }
            }
            return students;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveCurrentDate(String date) {
        prefs.edit().putString(KEY_CURRENT_DATE, date).apply();
    }

    public String getCurrentDate() {
        return prefs.getString(KEY_CURRENT_DATE, "");
    }
}

