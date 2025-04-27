package com.example.pianotutorial.features.authetication.eventhandlers;

import android.content.Context;
import android.view.View;

import com.example.pianotutorial.features.authetication.fragments.CheckEmailFragment;
import com.example.pianotutorial.features.authetication.fragments.LoginFragment;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.CheckEmailViewModel;

public class CheckEmailEventHandler {
    private final CheckEmailViewModel checkEmailViewModel;
    private final AuthViewModel authViewModel;
    Context context;
    public CheckEmailEventHandler(CheckEmailViewModel viewModel, AuthViewModel authViewModel, Context context) {
        this.checkEmailViewModel = viewModel;
        this.authViewModel = authViewModel;
        this.context = context;
    }
    public void navigateBack(View view) {
        authViewModel.getAuthFragment().setValue(authViewModel.getPreviousFragment());
        authViewModel.setPreviousFragment(new CheckEmailFragment());
    }
    public void navigateToLogin(View view){
        authViewModel.getAuthFragment().setValue(new LoginFragment());
        authViewModel.setPreviousFragment(new CheckEmailFragment());
    }
}
