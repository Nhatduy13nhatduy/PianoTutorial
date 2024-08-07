package com.example.pianotutorial.constants.adapters.course_detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pianotutorial.R;
import com.example.pianotutorial.databinding.ItemCourseDetailBinding;
import com.example.pianotutorial.features.playscreen.activities.PlayScreenActivity;

import java.util.List;

public class CourseDetailAdapter extends RecyclerView.Adapter<CourseDetailAdapter.CourseDetailViewHolder> {
    private final Context context;
    private final List<Integer> integerList;

    public CourseDetailAdapter(Context context, List<Integer> integerList) {
        this.context = context;
        this.integerList = integerList;
    }

    @NonNull
    @Override
    public CourseDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseDetailBinding binding = ItemCourseDetailBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseDetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseDetailViewHolder holder, int position) {
        int value = integerList.get(position);
        holder.binding.courseButton.setOnClickListener(v -> {
            ProgressBar progressBar = holder.binding.progressBar;
            progressBar.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(context, PlayScreenActivity.class);
                intent.putExtra("ITEM_VALUE", value);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }, 500);
        });

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return integerList.size();
    }

    public static class CourseDetailViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseDetailBinding binding;

        public CourseDetailViewHolder(@NonNull ItemCourseDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
