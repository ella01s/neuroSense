package com.example.neurosense.authentication.ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.example.neurosense.authentication.data.LoginDataSource;
import com.example.neurosense.authentication.data.LoginRepository;
//import com.example.neurosense.database.DatabaseHelper;

import database.DatabaseHelper;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private DatabaseHelper databaseHelper;

    public LoginViewModelFactory(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource(databaseHelper)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}

