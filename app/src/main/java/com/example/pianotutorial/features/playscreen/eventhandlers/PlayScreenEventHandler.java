package com.example.pianotutorial.features.playscreen.eventhandlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import com.example.pianotutorial.R;
import com.example.pianotutorial.features.navigation_bar.activities.NavigationBarActivity;
import com.example.pianotutorial.features.playscreen.servicehandlers.PlayScreenServiceHandler;
import com.example.pianotutorial.features.playscreen.viewmodels.PlayScreenViewModel;
import com.example.pianotutorial.models.Sheet;

import java.util.List;

public class PlayScreenEventHandler {

    private final PlayScreenViewModel playScreenViewModel;
    private final PlayScreenServiceHandler playScreenServiceHandler;
    private Runnable updateStaff;
    private final Handler handler = new Handler();
    Context context;

    public PlayScreenEventHandler(PlayScreenViewModel viewModel, Context context) {
        this.playScreenViewModel = viewModel;
        this.context = context;
        playScreenServiceHandler = new PlayScreenServiceHandler(context, playScreenViewModel);
    }

    public void onInitial() {
        SheetList sheetList = new SheetList();
        List<Sheet> sheets = sheetList.sheetList();
        if (sheets != null && !sheets.isEmpty()) {
            Sheet firstSheet = sheets.get(0); // Get the first Sheet in the list
            playScreenViewModel.getCurrentSheet().setValue(firstSheet); // Set the first sheet to the ViewModel
        }
    }

    public void onPlayIconClick(View view) {
        boolean isPlaying = Boolean.TRUE.equals(playScreenViewModel.getIsPlayed().getValue());
        playScreenViewModel.getIsPlayed().setValue(!isPlaying);
    }

    public void onPlayAgainClick(View view) {
        boolean isDone = Boolean.TRUE.equals(playScreenViewModel.getIsDone().getValue());
        playScreenViewModel.getIsDone().setValue(!isDone);

    }

    public void onSpeed1Click(View view) {
        playScreenViewModel.getSpeed().setValue(0.6f);
    }

    public void onSpeed2Click(View view) {
        playScreenViewModel.getSpeed().setValue(0.8f);
    }

    public void onSpeed3Click(View view) {
        playScreenViewModel.getSpeed().setValue(1f);
    }

    public void navigateToCourseDetail(View view) {
        ProgressBar progressBar = ((Activity) context).findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(context, NavigationBarActivity.class);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            ((Activity) context).finish();
        }, 1000);
    }
}

