package com.example.pianotutorial.features.authetication.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForgotPasswordViewModel extends ViewModel {
    private final MutableLiveData<Boolean> _navigateBackToLogin = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _navigateToCheckEmail = new MutableLiveData<>();

    public LiveData<Boolean> getNavigateBackToLogin(){
        return _navigateBackToLogin;
    }
    public void onBackClicked(){
        _navigateBackToLogin.setValue(true);
    }
    public void doneNavigatingToLogin(){
        _navigateBackToLogin.setValue(false);
    }

    public LiveData<Boolean> getNavigateToCheckEmail() {
        return _navigateToCheckEmail;
    }
    public void onContinueClicked(){
        _navigateToCheckEmail.setValue(true);
    }
    public void doneNavigatingToCheckEmail(){
        _navigateToCheckEmail.setValue(false);
    }
}
