package com.example.studentattendence;

import java.util.HashMap;
import java.util.Map;

public class Student {
    private String name;
    private String rollNumber;
    private boolean isPresent;
    private Map<String, Boolean> attendanceHistory; // Date -> Present/Absent
    private int totalDays;
    private int presentDays;

    public Student(String name, String rollNumber) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.isPresent = false; // Default to absent
        this.attendanceHistory = new HashMap<>();
        this.totalDays = 0;
        this.presentDays = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public Map<String, Boolean> getAttendanceHistory() {
        return attendanceHistory;
    }

    public void setAttendanceHistory(Map<String, Boolean> attendanceHistory) {
        this.attendanceHistory = attendanceHistory;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
    }

    public double getAttendancePercentage() {
        if (totalDays == 0) return 0.0;
        return (presentDays * 100.0) / totalDays;
    }

    public void recordAttendance(String date, boolean present) {
        if (attendanceHistory.containsKey(date)) {
            // Update existing record
            Boolean oldValue = attendanceHistory.get(date);
            if (oldValue != null) {
                if (oldValue && !present) {
                    // Was present, now absent
                    presentDays--;
                } else if (!oldValue && present) {
                    // Was absent, now present
                    presentDays++;
                }
            }
        } else {
            // New record
            totalDays++;
            if (present) {
                presentDays++;
            }
        }
        
        attendanceHistory.put(date, present);
    }
}

