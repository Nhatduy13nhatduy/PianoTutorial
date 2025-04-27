package com.example.pianotutorial.features.authetication.eventhandlers;

import android.content.Context;
import android.view.View;

import com.example.pianotutorial.features.authetication.fragments.ForgotPasswordFragment;
import com.example.pianotutorial.features.authetication.fragments.LoginFragment;
import com.example.pianotutorial.features.authetication.fragments.RegisterFragment;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.RegisterViewModel;

public class RegisterEventHandler {
    private final RegisterViewModel registerViewModel;
    private final AuthViewModel authViewModel;
    Context context;
    public RegisterEventHandler(RegisterViewModel viewModel, AuthViewModel authViewModel, Context context) {
        this.registerViewModel = viewModel;
        this.authViewModel = authViewModel;
        this.context = context;
    }
    public void navigateToLogin(View view) {
        authViewModel.getAuthFragment().setValue(new LoginFragment());
        authViewModel.setPreviousFragment(new RegisterFragment());
    }
    public void navigateBack(View view) {
        authViewModel.getAuthFragment().setValue(authViewModel.getPreviousFragment());
        authViewModel.setPreviousFragment(new ForgotPasswordFragment());

    }
}
