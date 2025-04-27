package com.example.pianotutorial.features.authetication.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<Boolean> _navigateBackToMainMenu = new MutableLiveData<>();

    public LiveData<Boolean> getNavigateBackToMainMenu(){
        return _navigateBackToMainMenu;
    }
    public void onBackClicked(){
        _navigateBackToMainMenu.setValue(true);
    }
    public void doneNavigatingBack(){
        _navigateBackToMainMenu.setValue(false);
    }
}
