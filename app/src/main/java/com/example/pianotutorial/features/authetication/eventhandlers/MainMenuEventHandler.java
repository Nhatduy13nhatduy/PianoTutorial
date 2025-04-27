package com.example.pianotutorial.features.authetication.eventhandlers;
import android.content.Context;
import android.view.View;

import com.example.pianotutorial.features.authetication.fragments.LoginFragment;
import com.example.pianotutorial.features.authetication.fragments.RegisterFragment;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.MainMenuViewModel;

public class MainMenuEventHandler {
    private final MainMenuViewModel viewModel;
    private final AuthViewModel authViewModel;
    Context context;
    public MainMenuEventHandler(MainMenuViewModel viewModel, AuthViewModel authViewModel, Context context) {
        this.viewModel = viewModel;
        this.authViewModel = authViewModel;
        this.context = context;
    }

    public void navigateToRegister(View view){
        authViewModel.getAuthFragment().setValue(new RegisterFragment());
    }

    public void navigateToLogin(View view){
        authViewModel.getAuthFragment().setValue(new LoginFragment());
    }
}
