package com.example.pianotutorial.features.authetication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.pianotutorial.R;
import com.example.pianotutorial.databinding.ActivityAuthBinding;
import com.example.pianotutorial.features.authetication.viewmodels.AuthViewModel;

public class   AuthActivity extends AppCompatActivity{
    private ActivityAuthBinding Binding;
    private AuthViewModel viewModel;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        Binding.setLifecycleOwner(this);

        viewModel.getAuthFragment().observe(this, fragment -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.auth_fragment, fragment)
                    .commit();
        });
    }
}
