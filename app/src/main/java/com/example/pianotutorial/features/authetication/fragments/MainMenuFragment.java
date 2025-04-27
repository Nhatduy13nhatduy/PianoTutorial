package com.example.pianotutorial.features.authetication.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pianotutorial.R;
import com.example.pianotutorial.databinding.FragmentMainmenuBinding;
import com.example.pianotutorial.features.authetication.eventhandlers.MainMenuEventHandler;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;
import com.example.pianotutorial.features.authetication.viewmodels.MainMenuViewModel;

public class MainMenuFragment extends Fragment {

    private FragmentMainmenuBinding binding;
    private MainMenuViewModel viewModel;
    private AuthViewModel authViewModel;
    private MainMenuEventHandler eventHandler;
    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mainmenu, container, false);
        viewModel = new ViewModelProvider(this).get(MainMenuViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        eventHandler = new MainMenuEventHandler(viewModel,authViewModel, getContext());
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.setEventHandler(eventHandler);

        return binding.getRoot();
    }

}

