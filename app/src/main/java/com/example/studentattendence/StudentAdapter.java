package com.example.studentattendence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> studentList;
    private OnAttendanceClickListener listener;

    public interface OnAttendanceClickListener {
        void onPresentClick(int position);
        void onAbsentClick(int position);
        void onDeleteClick(int position);
    }

    public StudentAdapter(List<Student> studentList, OnAttendanceClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.nameTextView.setText(student.getName());
        holder.rollNumberTextView.setText("Roll: " + student.getRollNumber());
        
        // Update status badge
        if (student.isPresent()) {
            holder.statusTextView.setText("Present");
            holder.statusCard.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.success_green_light));
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.success_green_dark));
        } else {
            holder.statusTextView.setText("Absent");
            holder.statusCard.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.error_red_light));
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.error_red_dark));
        }

        // Show attendance percentage
        double percentage = student.getAttendancePercentage();
        if (student.getTotalDays() > 0) {
            holder.attendancePercentageTextView.setText(String.format("📊 Attendance: %.1f%% (%d/%d)", 
                    percentage, student.getPresentDays(), student.getTotalDays()));
            holder.attendancePercentageTextView.setVisibility(View.VISIBLE);
        } else {
            holder.attendancePercentageTextView.setVisibility(View.GONE);
        }

        holder.presentButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPresentClick(position);
            }
        });

        holder.absentButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAbsentClick(position);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView rollNumberTextView;
        TextView statusTextView;
        TextView attendancePercentageTextView;
        MaterialCardView statusCard;
        Button presentButton;
        Button absentButton;
        Button deleteButton;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvStudentName);
            rollNumberTextView = itemView.findViewById(R.id.tvRollNumber);
            statusTextView = itemView.findViewById(R.id.tvStatus);
            statusCard = itemView.findViewById(R.id.cardStatusBadge);
            attendancePercentageTextView = itemView.findViewById(R.id.tvAttendancePercentage);
            presentButton = itemView.findViewById(R.id.btnPresent);
            absentButton = itemView.findViewById(R.id.btnAbsent);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}

