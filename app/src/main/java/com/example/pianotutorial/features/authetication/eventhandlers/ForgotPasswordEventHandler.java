package com.example.pianotutorial.features.authetication.eventhandlers;

import android.content.Context;
import android.view.View;

import com.example.pianotutorial.features.authetication.fragments.CheckEmailFragment;
import com.example.pianotutorial.features.authetication.fragments.ForgotPasswordFragment;
import com.example.pianotutorial.features.authetication.fragments.LoginFragment;
import com.example.pianotutorial.features.authetication.viewmodels.ForgotPasswordViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;

public class ForgotPasswordEventHandler {
    private final ForgotPasswordViewModel forgotPasswordViewModel;
    private final AuthViewModel authViewModel;
    Context context;
    public ForgotPasswordEventHandler(ForgotPasswordViewModel viewModel, AuthViewModel authViewModel, Context context) {
        this.forgotPasswordViewModel = viewModel;
        this.authViewModel = authViewModel;
        this.context = context;
    }
    public void navigateBack(View view) {
        authViewModel.getAuthFragment().setValue(new LoginFragment());
        authViewModel.setPreviousFragment(new ForgotPasswordFragment());
    }
    public void navigateToCheckEmail(View view){
        authViewModel.getAuthFragment().setValue(new CheckEmailFragment());
        authViewModel.setPreviousFragment(new ForgotPasswordFragment());
    }


}
