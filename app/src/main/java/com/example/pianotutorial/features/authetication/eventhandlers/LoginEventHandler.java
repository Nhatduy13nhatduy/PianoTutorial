package com.example.pianotutorial.features.authetication.eventhandlers;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.pianotutorial.features.authetication.fragments.LoginFragment;
import com.example.pianotutorial.features.authetication.fragments.MainMenuFragment;
import com.example.pianotutorial.features.authetication.fragments.RegisterFragment;
import com.example.pianotutorial.features.authetication.viewmodels.LoginViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.navigation_bar.activities.NavigationBarActivity;
import com.example.pianotutorial.features.authetication.fragments.ForgotPasswordFragment;

public class LoginEventHandler {
    private final LoginViewModel loginViewModel;
    private final AuthViewModel authViewModel;
    Context context;

    public LoginEventHandler(LoginViewModel viewModel, AuthViewModel authViewModel, Context context) {
        this.loginViewModel = viewModel;
        this.authViewModel = authViewModel;
        this.context = context;
    }

    public void navigateToNavigationBar(View view){
        Intent intent = new Intent(context, NavigationBarActivity.class);
        context.startActivity(intent);}
    public void navigateToForgotPassword(View view) {
        authViewModel.getAuthFragment().setValue(new ForgotPasswordFragment());
        authViewModel.setPreviousFragment(new LoginFragment());
    }
    public void navigateToRegister(View view){
        authViewModel.getAuthFragment().setValue(new RegisterFragment());
        authViewModel.setPreviousFragment(new LoginFragment());
    }
    public void navigateBack(View view) {
        authViewModel.getAuthFragment().setValue(new MainMenuFragment());
        authViewModel.setPreviousFragment(new ForgotPasswordFragment());

    }

}