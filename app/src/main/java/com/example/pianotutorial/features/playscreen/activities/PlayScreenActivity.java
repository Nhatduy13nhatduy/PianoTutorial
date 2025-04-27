package com.example.pianotutorial.features.playscreen.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.pianotutorial.R;
import com.example.pianotutorial.constants.GlobalVariables;
import com.example.pianotutorial.databinding.ActivityPlayscreenBinding;
import com.example.pianotutorial.features.components.helpers.DownloadTask;
import com.example.pianotutorial.features.components.helpers.MidiAware;
import com.example.pianotutorial.features.components.helpers.MidiNotesReceiver;
import com.example.pianotutorial.features.components.helpers.Note;
import com.example.pianotutorial.features.components.helpers.NoteActionListener;
import com.example.pianotutorial.features.playscreen.eventhandlers.PlayScreenEventHandler;
import com.example.pianotutorial.features.playscreen.viewmodels.PlayScreenViewModel;
import com.example.pianotutorial.models.Sheet;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.List;
import java.util.Objects;

public class PlayScreenActivity extends AppCompatActivity implements MidiAware, CompoundButton.OnCheckedChangeListener, MidiDriver.OnMidiStartListener, NoteActionListener {

    private static final String TAG = "PlayScreenActivity";
    private PlayScreenEventHandler playScreenEventHandler;
    private PlayScreenViewModel playScreenViewModel;
    private ActivityPlayscreenBinding activityPlayscreenBinding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView countdownTextView;
    private MidiManager midiManager;
    protected MidiDriver midi;
    protected MediaPlayer player;
    private String sheetFile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPlayscreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_playscreen);
        playScreenViewModel = new ViewModelProvider(this).get(PlayScreenViewModel.class);
        playScreenEventHandler = new PlayScreenEventHandler(playScreenViewModel, this);
        activityPlayscreenBinding.setViewModel(playScreenViewModel);
        activityPlayscreenBinding.setEventHandler(playScreenEventHandler);
        activityPlayscreenBinding.setLifecycleOwner(this);

        countdownTextView = activityPlayscreenBinding.getRoot().findViewById(R.id.countdownText);

        playScreenEventHandler.onInitial();
        setupObservers();
        initializeMIDI();

        if (midi != null) {
            midi.setOnMidiStartListener(this);
        }
    }

    private void setupObservers() {
        playScreenViewModel.getCurrentSheet().observe(this, currentSheet -> {
            if (currentSheet != null) {
                sheetFile=currentSheet.getSheetFile();
                loadMusicView(currentSheet);
            }
        });

        playScreenViewModel.getIsDone().observe(this, isDone -> {
            if (isDone) {
                // Slide-out combined with fade-out animation for opacityView
                activityPlayscreenBinding.totalScoreScreen.animate()
                        .translationY(activityPlayscreenBinding.totalScoreScreen.getHeight()) // Slide down
                        .alpha(0f) // Fade out
                        .setDuration(500) // Duration of the animation in milliseconds
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                activityPlayscreenBinding.totalScoreScreen.setVisibility(View.GONE);
                                activityPlayscreenBinding.totalScoreScreen.setTranslationY(0); // Reset translation
                                activityPlayscreenBinding.totalScoreScreen.setAlpha(1f); // Reset alpha
                            }
                        })
                        .start();
            }
        });

        playScreenViewModel.getIsPlayed().observe(this, isPlayed -> {
            if (isPlayed != null) {
                playScreenViewModel.getIsDone().setValue(false);
                if (!isPlayed) {
                    handlePause();
                    startUpdatingStaff();

                } else {
                    startCountdown(this);
                }
            }
        });

        playScreenViewModel.getSpeed().

                observe(this, this::updateSpeed);
    }

    private void loadMusicView(Sheet currentSheet) {
        activityPlayscreenBinding.musicView.setVisibility(View.GONE);

        handler.post(() -> {
            GlobalVariables.RIGHT_CLEF = currentSheet.getRightHandMeasures().get(0).getChords().get(0).getClef();
            GlobalVariables.LEFT_CLEF = currentSheet.getLeftHandMeasures().get(0).getChords().get(0).getClef();
            GlobalVariables.TOP_SIGNATURE = currentSheet.getTopSignature();
            GlobalVariables.BOTTOM_SIGNATURE = currentSheet.getBottomSignature();
            activityPlayscreenBinding.musicView.setMeasures(currentSheet);
            activityPlayscreenBinding.musicView.startDrawing(System.currentTimeMillis());

            // Hide the progress bar and show the MusicView
            activityPlayscreenBinding.musicView.setVisibility(View.VISIBLE);
        });
    }

    private void handlePause() {
        activityPlayscreenBinding.menuIcon.setVisibility(View.VISIBLE);
        activityPlayscreenBinding.playCircleVector.setVisibility(View.VISIBLE); // Countdown from 3 to 1

        // Reset the initial state for the animation
        activityPlayscreenBinding.opacityView.setAlpha(0f); // Start fully transparent
        activityPlayscreenBinding.opacityView.setTranslationY(activityPlayscreenBinding.opacityView.getHeight()); // Start below the screen
        activityPlayscreenBinding.opacityView.setVisibility(View.VISIBLE);

        // Slide-in combined with fade-in animation for opacityView
        activityPlayscreenBinding.opacityView.animate()
                .translationY(0) // Slide up to its original position
                .alpha(1f) // Fade in to fully visible
                .setDuration(500) // Duration of the animation in milliseconds
                .setListener(null) // No need for a listener since we don't need to reset anything afterward
                .start();

        activityPlayscreenBinding.playIcon.setVisibility(View.GONE);
        GlobalVariables.SPEED = 0;
        if (player != null) {
            player.stop();
            player.reset();
        }
    }


    private void updateSpeed(float speed) {
        String fileURL = sheetFile;

        int speed1Res = R.drawable.white_border;
        int speed2Res = R.drawable.white_border;
        int speed3Res = R.drawable.white_border;

        int speed1TextColor = R.color.white;
        int speed2TextColor = R.color.white;
        int speed3TextColor = R.color.white;

        if (speed == 0.6f) {
            speed1Res = R.color.white;
            speed1TextColor = R.color.black;
        } else if (speed == 0.8f) {
            speed2Res = R.color.white;
            speed2TextColor = R.color.black;
        } else if (speed == 1f) {
            speed3Res = R.color.white;
            speed3TextColor = R.color.black;
        }

        if (player != null) player.stop();
        playAudio(fileURL);
        setPlayerPlaybackSpeed(player, speed);
        player.pause();

        activityPlayscreenBinding.speed1Layout.setBackgroundResource(speed1Res);
        activityPlayscreenBinding.speed2Layout.setBackgroundResource(speed2Res);
        activityPlayscreenBinding.speed3Layout.setBackgroundResource(speed3Res);

        activityPlayscreenBinding.speed1Text.setTextColor(ContextCompat.getColor(this, speed1TextColor));
        activityPlayscreenBinding.speed2Text.setTextColor(ContextCompat.getColor(this, speed2TextColor));
        activityPlayscreenBinding.speed3Text.setTextColor(ContextCompat.getColor(this, speed3TextColor));


        // Start the download task
        DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(fileURL);
        startUpdatingStaff();
    }

    private void setPlayerPlaybackSpeed(MediaPlayer player, float speed) {
        PlaybackParams params = new PlaybackParams();
        params.setSpeed(speed);
        player.setPlaybackParams(params);
    }

    private void initializeMIDI() {
        midiManager = (MidiManager) getSystemService(MIDI_SERVICE);
        midi = MidiDriver.getInstance(this);

        midiManager.registerDeviceCallback(deviceCallback, null);
        checkForMidiDevices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (midi != null) midi.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (midi != null) midi.stop();
        if (player != null) player.stop();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        midi.setReverb(isChecked ? ReverbConstants.CHAMBER : ReverbConstants.OFF);
    }

    @Override
    public void onMidiStart() {
        sendMidi(MidiConstants.PROGRAM_CHANGE, GeneralMidiConstants.ACOUSTIC_GRAND_PIANO);
    }

    protected void sendMidi(int... messages) {
        byte[] msg = new byte[messages.length];
        for (int i = 0; i < messages.length; i++) {
            msg[i] = (byte) messages[i];
        }
        midi.write(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        midiManager.unregisterDeviceCallback(deviceCallback);
    }

    private void checkForMidiDevices() {
        for (MidiDeviceInfo device : midiManager.getDevices()) {
            Log.d(TAG, "MIDI device available: " + device.toString());
            openMidiDevice(device);
        }
    }

    private final MidiManager.DeviceCallback deviceCallback = new MidiManager.DeviceCallback() {
        @Override
        public void onDeviceAdded(MidiDeviceInfo device) {
            Log.d(TAG, "MIDI device added: " + device.toString());
            openMidiDevice(device);
        }

        @Override
        public void onDeviceRemoved(MidiDeviceInfo device) {
            Log.d(TAG, "MIDI device removed: " + device.toString());
        }
    };

    private void openMidiDevice(MidiDeviceInfo deviceInfo) {
        midiManager.openDevice(deviceInfo, midiDevice -> {
            if (midiDevice == null) {
                Log.e(TAG, "Could not open MIDI device");
            } else {
                onDeviceSuccess(midiDevice);
            }
        }, null);
    }

    public void onDeviceSuccess(MidiDevice midiDevice) {
        Log.i(TAG, "Midi device has been connected");
        MidiOutputPort midiOutputPort = midiDevice.openOutputPort(0);
        if (midiOutputPort != null) {
            midiOutputPort.connect(new MidiNotesReceiver(this));
        }
    }

    @Override
    public void guessNoteAction(Note note, boolean forceRightGuess) {
        Log.d(TAG, "Note pressed: " + note.toString());
        sendMidi(MidiConstants.NOTE_ON, note.getNoteId(), 63);
        activityPlayscreenBinding.musicView.updateWhiteKeyIndices(note, true);
        activityPlayscreenBinding.musicView.updateBlackKeyIndices(note, true);
        activityPlayscreenBinding.musicView.getNotesAndMeasuresDrawer().setCorrectNoteAction(note, true);
        activityPlayscreenBinding.musicView.getNotesAndMeasuresDrawerLeftHand().setCorrectNoteAction(note, true);

    }

    @Override
    public void stopGuessNoteAction(Note note) {
        sendMidi(MidiConstants.NOTE_OFF, note.getNoteId(), 0);
        activityPlayscreenBinding.musicView.updateWhiteKeyIndices(note, false);
        activityPlayscreenBinding.musicView.updateBlackKeyIndices(note, false);
        activityPlayscreenBinding.musicView.getNotesAndMeasuresDrawer().setCorrectNoteAction(note, false);
        activityPlayscreenBinding.musicView.getNotesAndMeasuresDrawerLeftHand().setCorrectNoteAction(note, false);
    }

    private void startCountdown(Context context) {
        GlobalVariables.SPEED = Objects.requireNonNull(playScreenViewModel.getSpeed().getValue());
        startUpdatingStaff();
        activityPlayscreenBinding.playCircleVector.setVisibility(View.GONE); // Countdown from 3 to 1
        playAudio(sheetFile);


        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                countdownTextView.setVisibility(View.VISIBLE); // Countdown from 3 to 1
                countdownTextView.setText(String.valueOf(secondsRemaining + 1)); // Countdown from 3 to 1

            }

            @Override
            public void onFinish() {
                countdownTextView.setVisibility(View.GONE);

                // Slide-out combined with fade-out animation for opacityView
                activityPlayscreenBinding.opacityView.animate()
                        .translationY(activityPlayscreenBinding.opacityView.getHeight()) // Slide down
                        .alpha(0f) // Fade out
                        .setDuration(500) // Duration of the animation in milliseconds
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                activityPlayscreenBinding.opacityView.setVisibility(View.GONE);
                                activityPlayscreenBinding.opacityView.setTranslationY(0); // Reset translation
                                activityPlayscreenBinding.opacityView.setAlpha(1f); // Reset alpha
                            }
                        })
                        .start();

                activityPlayscreenBinding.playIcon.setVisibility(View.VISIBLE);
                activityPlayscreenBinding.menuIcon.setVisibility(View.GONE);
            }

        }.start();
    }


    private void startUpdatingStaff() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                activityPlayscreenBinding.musicView.updateView();
                handler.postDelayed(this, 8);
            }
        });
    }

    private void playAudio(String filePath) {
        if (player != null) {
            player.release();
        }

        player = new MediaPlayer();

        try {
            player.setDataSource(filePath);
            player.prepare();
            activityPlayscreenBinding.musicView.setMediaPlayer(player);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playScreenViewModel.getIsPlayed().setValue(false);
                    float countCorrect = GlobalVariables.COUNT_CORRECT;
                    float countIncorrect = GlobalVariables.COUNT_INCORRECT;
                    float currentPercent = 0f;
                    if (GlobalVariables.COUNT_INCORRECT != 0) {
                        currentPercent = countCorrect / (countCorrect + countIncorrect);
                    }
                    currentPercent *= 3;
                    int currentStar = (int) currentPercent;
                    if (currentStar < 1) {
                        activityPlayscreenBinding.firstStar.setBackgroundResource(R.drawable.vector_star_score);
                        activityPlayscreenBinding.secondStar.setBackgroundResource(R.drawable.vector_star_score);
                        activityPlayscreenBinding.thirdStar.setBackgroundResource(R.drawable.vector_star_score);
                    } else if (currentStar == 1) {
                        activityPlayscreenBinding.firstStar.setBackgroundResource(R.drawable.vector_star_score_fill);
                        activityPlayscreenBinding.secondStar.setBackgroundResource(R.drawable.vector_star_score);
                        activityPlayscreenBinding.thirdStar.setBackgroundResource(R.drawable.vector_star_score);
                    } else if (currentStar == 2) {
                        activityPlayscreenBinding.firstStar.setBackgroundResource(R.drawable.vector_star_score_fill);
                        activityPlayscreenBinding.secondStar.setBackgroundResource(R.drawable.vector_star_score_fill);
                        activityPlayscreenBinding.thirdStar.setBackgroundResource(R.drawable.vector_star_score);
                    } else {
                        activityPlayscreenBinding.firstStar.setBackgroundResource(R.drawable.vector_star_score_fill);
                        activityPlayscreenBinding.secondStar.setBackgroundResource(R.drawable.vector_star_score_fill);
                        activityPlayscreenBinding.thirdStar.setBackgroundResource(R.drawable.vector_star_score_fill);
                    }

                    activityPlayscreenBinding.firstStar.setScaleX(0f);
                    activityPlayscreenBinding.firstStar.setScaleY(0f);
                    activityPlayscreenBinding.secondStar.setScaleX(0f);
                    activityPlayscreenBinding.secondStar.setScaleY(0f);
                    activityPlayscreenBinding.thirdStar.setScaleX(0f);
                    activityPlayscreenBinding.thirdStar.setScaleY(0f);

                    ObjectAnimator scaleXFirstStar = ObjectAnimator.ofFloat(activityPlayscreenBinding.firstStar, "scaleX", 1f);
                    ObjectAnimator scaleYFirstStar = ObjectAnimator.ofFloat(activityPlayscreenBinding.firstStar, "scaleY", 1f);
                    ObjectAnimator scaleXSecondStar = ObjectAnimator.ofFloat(activityPlayscreenBinding.secondStar, "scaleX", 1f);
                    ObjectAnimator scaleYSecondStar = ObjectAnimator.ofFloat(activityPlayscreenBinding.secondStar, "scaleY", 1f);
                    ObjectAnimator scaleXThirdStar = ObjectAnimator.ofFloat(activityPlayscreenBinding.thirdStar, "scaleX", 1f);
                    ObjectAnimator scaleYThirdStar = ObjectAnimator.ofFloat(activityPlayscreenBinding.thirdStar, "scaleY", 1f);

                    scaleXFirstStar.setDuration(300);
                    scaleYFirstStar.setDuration(300);
                    scaleXSecondStar.setDuration(300);
                    scaleYSecondStar.setDuration(300);
                    scaleXThirdStar.setDuration(300);
                    scaleYThirdStar.setDuration(300);

                    new Handler().postDelayed(() -> {
                        activityPlayscreenBinding.firstStar.setVisibility(View.VISIBLE);
                        scaleXFirstStar.start();
                        scaleYFirstStar.start();
                    }, 300);

                    new Handler().postDelayed(() -> {
                        activityPlayscreenBinding.secondStar.setVisibility(View.VISIBLE);
                        scaleXSecondStar.start();
                        scaleYSecondStar.start();
                    }, 600);

                    new Handler().postDelayed(() -> {
                        activityPlayscreenBinding.thirdStar.setVisibility(View.VISIBLE);
                        scaleXThirdStar.start();
                        scaleYThirdStar.start();
                    }, 900);

                    activityPlayscreenBinding.score.setText(GlobalVariables.COUNT_CORRECT + "/" + (GlobalVariables.COUNT_CORRECT + GlobalVariables.COUNT_INCORRECT));
                    activityPlayscreenBinding.totalScoreScreen.setAlpha(0f);
                    activityPlayscreenBinding.totalScoreScreen.setTranslationY(activityPlayscreenBinding.totalScoreScreen.getHeight());
                    activityPlayscreenBinding.totalScoreScreen.setVisibility(View.VISIBLE);

                    activityPlayscreenBinding.totalScoreScreen.animate()
                            .alpha(1f)
                            .translationY(0)
                            .setDuration(500)
                            .start();
                    GlobalVariables.COUNT_CORRECT = 0;
                    GlobalVariables.COUNT_INCORRECT = 0;
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Could not play audio file", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDeviceOpened(MidiOutputPort midiOutputPort) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            activityPlayscreenBinding.progressBar.setVisibility(View.GONE);
        }
    }
}
