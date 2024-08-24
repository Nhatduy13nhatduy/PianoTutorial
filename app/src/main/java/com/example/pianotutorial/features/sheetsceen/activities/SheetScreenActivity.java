package com.example.pianotutorial.features.sheetsceen.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pianotutorial.R;
import com.example.pianotutorial.constants.adapters.sheet.SheetAdapter;
import com.example.pianotutorial.databinding.ActivitySheetScreenBinding;
import com.example.pianotutorial.features.sheetsceen.eventhandlers.SheetScreenEventHandler;
import com.example.pianotutorial.features.sheetsceen.viewmodels.SheetScreenViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SheetScreenActivity extends AppCompatActivity {

    private ActivitySheetScreenBinding activitySheetScreenBinding;
    private SheetScreenEventHandler sheetScreenEventHandler;
    private SheetScreenViewModel sheetScreenViewModel;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private Runnable autoScrollRunnable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        activitySheetScreenBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_sheet_screen
        );

        // Initialize ViewModel
        sheetScreenViewModel = new ViewModelProvider(this).get(SheetScreenViewModel.class);

        // Initialize EventHandler (corrected)
        sheetScreenEventHandler = new SheetScreenEventHandler(sheetScreenViewModel, this);
        activitySheetScreenBinding.setEventHandler(sheetScreenEventHandler);
        activitySheetScreenBinding.setLifecycleOwner(this);

        // Get the sheetId passed from the previous activity
        if (getIntent().getExtras() != null) {
            int sheetId = getIntent().getExtras().getInt("sheetId", -1); // Default to -1 if not found
            sheetScreenEventHandler.getSheetById(sheetId);
        }

        mediaPlayer = new MediaPlayer();

        // Use sheetId as needed in your activity (e.g., to load sheet details)
        sheetScreenViewModel.getCurrentSheet().observe(this, currentSheet -> {

            try {
                mediaPlayer.setDataSource(currentSheet.getSheetFile());
                mediaPlayer.prepare();
            } catch (Exception e) {
                Toast.makeText(this, "Không tìm thấy bài nhạc!", Toast.LENGTH_SHORT).show();
            }
        });

        sheetScreenViewModel.getIsShowTopBar().observe(this, isShowTopBar -> {
            if (isShowTopBar) {
                activitySheetScreenBinding.topBar.setVisibility(View.VISIBLE);
                Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
                activitySheetScreenBinding.topBar.startAnimation(slideIn);
            } else {
                Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
                activitySheetScreenBinding.topBar.startAnimation(slideOut);
                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        activitySheetScreenBinding.topBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });

        sheetScreenViewModel.getIsShowMusicSeek().observe(this, isShowMusicSeek -> {
            if (isShowMusicSeek) {
                activitySheetScreenBinding.listenToMusicButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(this, R.color.darkGreen)
                );
                activitySheetScreenBinding.musicSeekBarLayout.setVisibility(View.VISIBLE);
                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                activitySheetScreenBinding.musicSeekBarLayout.startAnimation(fadeIn);
            } else {
                activitySheetScreenBinding.listenToMusicButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(this, R.color.green)
                );
                Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                activitySheetScreenBinding.musicSeekBarLayout.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        activitySheetScreenBinding.musicSeekBarLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                sheetScreenViewModel.getIsPlayed().setValue(false);
            }
        });

        sheetScreenViewModel.getIsAutoScroll().observe(this, isAutoScroll -> {
            if (isAutoScroll) {
                activitySheetScreenBinding.autoScrollButton.setBackgroundResource(R.drawable.vector_scroll_speed_pause);
                startAutoScroll();

            } else {
                activitySheetScreenBinding.autoScrollButton.setBackgroundResource(R.drawable.vector_scroll_speed_play);
                stopAutoScroll();
            }
        });

        sheetScreenViewModel.getIsPlayed().observe(this, isPlayed -> {
            if (isPlayed) {
                activitySheetScreenBinding.playIcon.setBackgroundResource(R.drawable.vector_pause);
                mediaPlayer.start();
                updateSeekBar();

            } else {
                activitySheetScreenBinding.playIcon.setBackgroundResource(R.drawable.vector_play);
                mediaPlayer.pause();

            }
        });

        activitySheetScreenBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert progress to a value between 0.5 and 2
                float min = 0.5f;
                float max = 2.0f;
                float normalizedProgress = min + (float) progress / 100 * (max - min);

                // Update scrollSpeed in ViewModel
                sheetScreenViewModel.getScrollSpeed().setValue(normalizedProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        sheetScreenViewModel.getScrollSpeed().observe(this, scrollSpeed -> {
            String formattedScrollSpeed = String.format("x%.1f", scrollSpeed);
            activitySheetScreenBinding.scollValue.setText(formattedScrollSpeed);
        });

        sheetScreenViewModel.getMusicSeekBarProgress().observe(this, progress -> {
            // Convert progress (milliseconds) to minutes and seconds
            int minutes = (progress / 1000) / 60;
            int seconds = (progress / 1000) % 60;

            // Format the time as "MM:SS"
            String timeFormatted = String.format("%02d:%02d", minutes, seconds);

            // Update the TextView with the formatted time
            activitySheetScreenBinding.textViewTime.setText(timeFormatted);

        });

        activitySheetScreenBinding.musicSeekBar.setMax(1000);
        activitySheetScreenBinding.musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    int duration = mediaPlayer.getDuration();
                    // Convert percentage back to milliseconds
                    int newProgress = (int) ((progress / 1000.0) * duration);

                    // Update ViewModel with new progress
                    sheetScreenViewModel.getMusicSeekBarProgress().setValue(newProgress);

                    // Seek the media player to the new position
                    mediaPlayer.seekTo(newProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        // Update SeekBar as the music plays
        mediaPlayer.setOnCompletionListener(mp -> sheetScreenViewModel.getIsPlayed().setValue(false));

        // Initialize data
        List<String> imageUrlList = new ArrayList<>();
        // Add sample data to the list
        imageUrlList.add("https://firebasestorage.googleapis.com/v0/b/pianoaiapi.appspot.com/o/Img%2Fz5730364656077_1fe15aea8687ae24ada1c89b7dbb7f88.jpg?alt=media&token=9b5deebf-c94f-4f65-9daa-e94deb6da8e2");
        imageUrlList.add("https://firebasestorage.googleapis.com/v0/b/pianoaiapi.appspot.com/o/Img%2Fz5730364656266_1d590c848b1ee1cb0c336b939d4de9da.jpg?alt=media&token=38ae9257-fd16-4a43-85f9-51ea20ddd308");
        imageUrlList.add("https://firebasestorage.googleapis.com/v0/b/pianoaiapi.appspot.com/o/Img%2Fz5730367051104_a080c83ad1c99d61ded15273bec1f11a.jpg?alt=media&token=6fa38e29-8728-4575-89ad-0f77d46631ba");

        // Initialize the adapter
        SheetAdapter sheetAdapter = new SheetAdapter(this, imageUrlList);

        // Set the adapter and LinearLayoutManager
        activitySheetScreenBinding.recyclerViewSheet.setLayoutManager(new LinearLayoutManager(this));
        activitySheetScreenBinding.recyclerViewSheet.setAdapter(sheetAdapter);
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int duration = mediaPlayer.getDuration();
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (duration > 0) {
                int percentageProgress = (int) ((currentPosition / (float) duration) * 1000);
                sheetScreenViewModel.getMusicSeekBarProgress().setValue(currentPosition);
                activitySheetScreenBinding.musicSeekBar.setProgress(percentageProgress);
            }
            handler.postDelayed(this::updateSeekBar, 16);
        }
    }

    private float accumulatedScroll = 0f; // Variable to store the accumulated scroll value (including the fractional part)

    private void startAutoScroll() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (sheetScreenViewModel.getIsAutoScroll().getValue() != null &&
                        sheetScreenViewModel.getIsAutoScroll().getValue()) {

                    float scrollSpeedFactor = sheetScreenViewModel.getScrollSpeed().getValue();
                    // Calculate the scroll distance based on scrollSpeedFactor

                    // Add scrollDistance to accumulatedScroll
                    accumulatedScroll += scrollSpeedFactor;

                    // Extract the integer part of accumulatedScroll for scrolling
                    int scrollAmount = (int) accumulatedScroll;

                    if (scrollAmount != 0) {
                        // Perform the scroll with the integer part
                        activitySheetScreenBinding.recyclerViewSheet.scrollBy(0, scrollAmount);

                        // Subtract the scrolled integer amount from accumulatedScroll
                        accumulatedScroll -= scrollAmount;
                    }

                    // Continue scrolling after a delay
                    handler.postDelayed(this, 16);
                }
            }
        };

        // Start the scrolling process
        handler.postDelayed(autoScrollRunnable, 16);
    }



    private void stopAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoScroll();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}