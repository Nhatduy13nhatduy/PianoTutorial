package com.example.pianotutorial.features.authetication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pianotutorial.R;
import com.example.pianotutorial.databinding.FragmentForgotpasswordBinding;
import com.example.pianotutorial.features.authetication.eventhandlers.ForgotPasswordEventHandler;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.ForgotPasswordViewModel;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotpasswordBinding Binding;
    private AuthViewModel authViewModel;
    private ForgotPasswordViewModel viewModel;
    private ForgotPasswordEventHandler eventHandler;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgotpassword, container, false);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        eventHandler = new ForgotPasswordEventHandler(viewModel, authViewModel,getContext());
        Binding.setViewModel(viewModel);
        Binding.setLifecycleOwner(this);
        Binding.setEventHandler(eventHandler);

        viewModel.getNavigateBackToLogin().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate) {
                getActivity().onBackPressed();
                viewModel.doneNavigatingToLogin();
            }
        });

        viewModel.getNavigateToCheckEmail().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate){
                navigateToCheckEmail();
                viewModel.doneNavigatingToCheckEmail();
            }
        });

        return Binding.getRoot();
    }
    private void navigateToCheckEmail() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CheckEmailFragment())
                .addToBackStack(null)
                .commit();
    }
}
