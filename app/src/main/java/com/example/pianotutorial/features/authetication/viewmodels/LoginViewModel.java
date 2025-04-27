package com.example.pianotutorial.features.authetication.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Boolean> _navigateBackToMainMenu = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _navigateToForgotPassword = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _navigateToRegister = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _navigateToNavigationBar = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading(){
        return _isLoading;
    }
    public LiveData<Boolean> getNavigateToNavigationBar() {
        return _navigateToNavigationBar;
    }
    public void onLogClicked() {
        _navigateToNavigationBar.setValue(true);
    }
    public void doneNavigateToNavigationBar() {
        _navigateToNavigationBar.setValue(false);
    }
    public LiveData<Boolean> getNavigateBackToMainMenu() {
        return _navigateBackToMainMenu;
    }

    public void onBackClicked() {
        _navigateBackToMainMenu.setValue(true);
    }

    public void doneNavigatingBack() {
        _navigateBackToMainMenu.setValue(false);
    }

    public LiveData<Boolean> getNavigateToForgotPassword() {
        return _navigateToForgotPassword;
    }
    public LiveData<Boolean> getNavigateToRegister(){
        return _navigateToRegister;
    }

    public void onForgotPasswordClicked() {
        _navigateToForgotPassword.setValue(true);
    }

    public void doneNavigateToForgotPassword() {
        _navigateToForgotPassword.setValue(false);
    }




    public void onRegisterLinkClicked() {
        _navigateToRegister.setValue(true);
    }

    public void doneNavigatingToRegister() {
        _navigateToRegister.setValue(false);
    }
}
