package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.usecases.LoginUseCase;
import com.uth.supereconomico.utils.RemoteLogger;

public class LoginViewModel extends ViewModel {
    private final LoginUseCase loginUseCase;
    private String currentIdentifier;
    
    private final MutableLiveData<Usuario> _user = new MutableLiveData<>();
    public LiveData<Usuario> user = _user;
    
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;
    
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public LoginViewModel(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public void login(String email, String password) {
        currentIdentifier = email;
        _isLoading.setValue(true);
        loginUseCase.execute(email, password, new AuthRepository.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                _isLoading.postValue(false);
                _user.postValue(result);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                RemoteLogger.log(
                        "LoginViewModel",
                        "login",
                        message,
                        null,
                        null,
                        "identifier=" + (currentIdentifier != null ? currentIdentifier : "")
                );
                _error.postValue(message);
            }
        });
    }
}
