package com.example.pianotutorial.features.playscreen.eventhandlers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import androidx.media3.exoplayer.ExoPlayer;

import com.example.pianotutorial.constants.GlobalVariables;
import com.example.pianotutorial.features.course_detail.activities.CourseDetailActivity;
import com.example.pianotutorial.features.playscreen.activities.PlayScreenActivity;
import com.example.pianotutorial.features.playscreen.servicehandlers.PlayScreenServiceHandler;
import com.example.pianotutorial.features.playscreen.viewmodels.PlayScreenViewModel;

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
        playScreenServiceHandler.getAllSheets();
    }
    /*public void onInitial(int sheetId,int leftSheetId) {
        playScreenServiceHandler.getSheetById(sheetId);
        playScreenServiceHandler.getLeftSheetById(sheetId);
    }*/

    public void onPlayIconClick(View view) {
        boolean isPlaying = Boolean.TRUE.equals(playScreenViewModel.getIsPlayed().getValue());
        playScreenViewModel.getIsPlayed().setValue(!isPlaying);
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
        Intent intent = new Intent(context, CourseDetailActivity.class);
        context.startActivity(intent);
    }
}

