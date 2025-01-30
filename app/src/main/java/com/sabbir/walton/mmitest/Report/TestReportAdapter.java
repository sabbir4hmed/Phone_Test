package com.sabbir.walton.mmitest.Report;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sabbir.walton.mmitest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestReportAdapter extends RecyclerView.Adapter<TestReportAdapter.ViewHolder> {
    private List<Map.Entry<String, String>> testResults;

    public TestReportAdapter(Map<String, String> results) {
        updateData(results);
    }

    public void updateData(Map<String, String> newResults) {
        testResults = new ArrayList<>(newResults.entrySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, String> entry = testResults.get(position);
        holder.testNameTextView.setText(entry.getKey());
        holder.testStatusTextView.setText(entry.getValue());

        int textColor;
        switch (entry.getValue().toLowerCase()) {
            case "passed":
                textColor = Color.GREEN;
                break;
            case "failed":
                textColor = Color.RED;
                break;
            default:
                textColor = Color.GRAY;
        }
        holder.testStatusTextView.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return testResults.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView testNameTextView;
        TextView testStatusTextView;

        ViewHolder(View view) {
            super(view);
            testNameTextView = view.findViewById(R.id.testNameTextView);
            testStatusTextView = view.findViewById(R.id.testStatusTextView);
        }
    }
}

