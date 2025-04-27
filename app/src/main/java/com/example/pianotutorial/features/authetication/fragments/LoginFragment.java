package com.example.pianotutorial.features.authetication.fragments;

import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.pianotutorial.R;
import com.example.pianotutorial.databinding.FragmentLoginBinding;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.LoginViewModel;
import com.example.pianotutorial.features.authetication.eventhandlers.LoginEventHandler;

public class LoginFragment extends Fragment {
    private LoginViewModel viewModel;
    private LoginEventHandler eventHandler;
    private FragmentLoginBinding Binding;
    private AuthViewModel authViewModel;
    //
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        eventHandler = new LoginEventHandler(viewModel, authViewModel,getContext());
        Binding.setEventhandler(eventHandler);
        Binding.setViewModel(viewModel);
        Binding.setLifecycleOwner(this);
        viewModel.getNavigateBackToMainMenu().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate) {
                getActivity().onBackPressed();
                viewModel.doneNavigatingBack();
            }
        });

        viewModel.getNavigateToForgotPassword().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate){
                navigateToForgotPassword();
                viewModel.doneNavigateToForgotPassword();
            }
        });


        viewModel.getNavigateToRegister().observe(getViewLifecycleOwner(), nav -> {
            if (nav != null && nav){
                navigateToRegister();
                viewModel.doneNavigatingToRegister();
            }
        });

//        String text = getString(R.string.not_have_account);
//        SpannableString spannableString = new SpannableString(text);
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                viewModel.onRegisterLinkClicked();
//            }
//        };
//
//        int startIndex = text.indexOf("Đăng kí ngay");
//        int endIndex = startIndex + "Đăng kí ngay".length();
//
//        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        Binding.txtChuacotk.setText(spannableString);
//
//        viewModel.navigateToRegister.observe(getViewLifecycleOwner(), navigate -> {
//            if(navigate != null && navigate){
//                NavHostFragment.findNavController(LoginFragment.this)
//                        .navigate(R.id.action_loginFragment_to_registerFragment);
//                viewModel.doneNavigatingToRegister();
//            }
//        });
//
        return Binding.getRoot();
    }
    private void navigateToForgotPassword() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ForgotPasswordFragment())
                .addToBackStack(null)
                .commit();
    }
    private void navigateToRegister(){
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Binding = null;
    }

}