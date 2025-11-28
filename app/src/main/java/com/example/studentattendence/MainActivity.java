package com.example.studentattendence;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements StudentAdapter.OnAttendanceClickListener {
    private EditText etStudentName, etRollNumber, etSearch;
    private Button btnAddStudent, btnResetAttendance, btnMarkAllPresent, btnMarkAllAbsent, btnSelectDate, btnSaveAttendance;
    private RecyclerView recyclerView;
    private TextView tvAttendanceSummary, tvCurrentDate;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private List<Student> filteredList;
    private DataManager dataManager;
    private String currentDate;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Set status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue_dark));

        dataManager = new DataManager(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = dateFormat.format(new Date());
        
        // Load saved date or use current date
        String savedDate = dataManager.getCurrentDate();
        if (!savedDate.isEmpty()) {
            currentDate = savedDate;
        }

        initializeViews();
        setupRecyclerView();
        loadStudents();
        setupClickListeners();
        setupSearch();
        updateDateDisplay();
    }

    private void initializeViews() {
        etStudentName = findViewById(R.id.etStudentName);
        etRollNumber = findViewById(R.id.etRollNumber);
        etSearch = findViewById(R.id.etSearch);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnResetAttendance = findViewById(R.id.btnResetAttendance);
        btnMarkAllPresent = findViewById(R.id.btnMarkAllPresent);
        btnMarkAllAbsent = findViewById(R.id.btnMarkAllAbsent);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSaveAttendance = findViewById(R.id.btnSaveAttendance);
        recyclerView = findViewById(R.id.recyclerView);
        tvAttendanceSummary = findViewById(R.id.tvAttendanceSummary);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(filteredList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAddStudent.setOnClickListener(v -> addStudent());
        btnResetAttendance.setOnClickListener(v -> resetAttendance());
        btnMarkAllPresent.setOnClickListener(v -> markAllPresent());
        btnMarkAllAbsent.setOnClickListener(v -> markAllAbsent());
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSaveAttendance.setOnClickListener(v -> saveAttendance());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterStudents(String query) {
        if (adapter == null) return;
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(studentList);
        } else {
            query = query.toLowerCase();
            for (Student student : studentList) {
                if (student.getName().toLowerCase().contains(query) ||
                    student.getRollNumber().toLowerCase().contains(query)) {
                    filteredList.add(student);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                currentDate = dateFormat.format(calendar.getTime());
                dataManager.saveCurrentDate(currentDate);
                updateDateDisplay();
                loadAttendanceForDate();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(currentDate);
            if (date != null) {
                String formattedDate = displayFormat.format(date);
                // Check if it's today
                SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                if (todayFormat.format(new Date()).equals(currentDate)) {
                    tvCurrentDate.setText("📅 Today, " + formattedDate.substring(formattedDate.indexOf(",") + 2));
                } else {
                    tvCurrentDate.setText("📅 " + formattedDate);
                }
            }
        } catch (Exception e) {
            tvCurrentDate.setText("📅 " + currentDate);
        }
    }

    private void loadStudents() {
        studentList = dataManager.loadStudents();
        if (studentList == null || studentList.isEmpty()) {
            studentList = new ArrayList<>();
        }
        filteredList.clear();
        filteredList.addAll(studentList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        loadAttendanceForDate();
        updateAttendanceSummary();
    }

    private void loadAttendanceForDate() {
        for (Student student : studentList) {
            Boolean wasPresent = student.getAttendanceHistory().get(currentDate);
            if (wasPresent != null) {
                student.setPresent(wasPresent);
            } else {
                student.setPresent(false);
            }
        }
        adapter.notifyDataSetChanged();
        updateAttendanceSummary();
    }

    private void addStudent() {
        String name = etStudentName.getText().toString().trim();
        String rollNumber = etRollNumber.getText().toString().trim();

        if (name.isEmpty() || rollNumber.isEmpty()) {
            Toast.makeText(this, "Please enter both name and roll number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if roll number already exists
        for (Student student : studentList) {
            if (student.getRollNumber().equals(rollNumber)) {
                Toast.makeText(this, "Roll number already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Student student = new Student(name, rollNumber);
        studentList.add(student);
        filterStudents(etSearch.getText().toString());
        dataManager.saveStudents(studentList);
        
        etStudentName.setText("");
        etRollNumber.setText("");
        updateAttendanceSummary();
        
        Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
    }

    private void resetAttendance() {
        for (Student student : studentList) {
            student.setPresent(false);
        }
        adapter.notifyDataSetChanged();
        updateAttendanceSummary();
        Toast.makeText(this, "Attendance reset", Toast.LENGTH_SHORT).show();
    }

    private void markAllPresent() {
        for (Student student : studentList) {
            student.setPresent(true);
        }
        adapter.notifyDataSetChanged();
        updateAttendanceSummary();
        Toast.makeText(this, "All students marked as present", Toast.LENGTH_SHORT).show();
    }

    private void markAllAbsent() {
        for (Student student : studentList) {
            student.setPresent(false);
        }
        adapter.notifyDataSetChanged();
        updateAttendanceSummary();
        Toast.makeText(this, "All students marked as absent", Toast.LENGTH_SHORT).show();
    }

    private void saveAttendance() {
        for (Student student : studentList) {
            student.recordAttendance(currentDate, student.isPresent());
        }
        dataManager.saveStudents(studentList);
        Toast.makeText(this, "Attendance saved for " + currentDate, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPresentClick(int position) {
        if (position >= 0 && position < filteredList.size()) {
            Student student = filteredList.get(position);
            student.setPresent(true);
            adapter.notifyItemChanged(position);
            updateAttendanceSummary();
        }
    }

    @Override
    public void onAbsentClick(int position) {
        if (position >= 0 && position < filteredList.size()) {
            Student student = filteredList.get(position);
            student.setPresent(false);
            adapter.notifyItemChanged(position);
            updateAttendanceSummary();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        if (position >= 0 && position < filteredList.size()) {
            Student studentToDelete = filteredList.get(position);
            new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + studentToDelete.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    studentList.remove(studentToDelete);
                    filterStudents(etSearch.getText().toString());
                    dataManager.saveStudents(studentList);
                    updateAttendanceSummary();
                    Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
    }

    private void updateAttendanceSummary() {
        int total = studentList.size();
        int present = 0;
        for (Student student : studentList) {
            if (student.isPresent()) {
                present++;
            }
        }
        int absent = total - present;
        
        if (total > 0) {
            double percentage = total > 0 ? (present * 100.0) / total : 0;
            tvAttendanceSummary.setText(String.format("Total: %d | Present: %d | Absent: %d | %.1f%%", 
                    total, present, absent, percentage));
        } else {
            tvAttendanceSummary.setText("No students added yet");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Auto-save when leaving the activity
        saveAttendance();
    }
}