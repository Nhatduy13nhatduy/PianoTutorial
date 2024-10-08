package com.example.pianotutorial.features.playscreen.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pianotutorial.models.Sheet;

import java.util.List;

public class PlayScreenViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isPlayed = new MutableLiveData<>(false);
    private final MutableLiveData<List<Sheet>> sheetList = new MutableLiveData<>();
    private final MutableLiveData<Sheet> currentSheet = new MutableLiveData<>();
    private final MutableLiveData<Sheet> currentLeftSheet = new MutableLiveData<>();
    private final MutableLiveData<Float> speed = new MutableLiveData<>(1f);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isDone = new MutableLiveData<>(false);


    public MutableLiveData<Boolean> getIsPlayed() {
        return isPlayed;
    }
    public MutableLiveData<List<Sheet>> getSheetList() {
        return sheetList;
    }
    public MutableLiveData<Float> getSpeed() {
        return speed;
    }
    public MutableLiveData<Sheet> getCurrentSheet() {
        return currentSheet;
    }
    public MutableLiveData<Sheet> getCurrentLeftSheet() {
        return currentLeftSheet;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsDone() {
        return isDone;
    }
}
