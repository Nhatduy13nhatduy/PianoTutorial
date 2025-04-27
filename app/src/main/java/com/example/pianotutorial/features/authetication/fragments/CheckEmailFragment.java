package com.example.pianotutorial.features.authetication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pianotutorial.R;
import com.example.pianotutorial.databinding.FragmentCheckEmailBinding;
import com.example.pianotutorial.features.authetication.eventhandlers.CheckEmailEventHandler;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.CheckEmailViewModel;


public class CheckEmailFragment extends Fragment {
    private FragmentCheckEmailBinding Binding;
    private CheckEmailViewModel viewModel;
    private CheckEmailEventHandler eventHandler;
    private AuthViewModel authViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_email, container, false);
        viewModel = new ViewModelProvider(this).get(CheckEmailViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        eventHandler = new CheckEmailEventHandler(viewModel, authViewModel,getContext());
        Binding.setViewModel(viewModel);
        Binding.setLifecycleOwner(this);
        Binding.setEventHandler(eventHandler);

        return Binding.getRoot();
    }
}
