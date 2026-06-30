package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.usecases.RecoverPasswordUseCase;

public class ForgotPasswordViewModel extends ViewModel {
    private final RecoverPasswordUseCase recoverPasswordUseCase;

    private final MutableLiveData<Boolean> _isSuccess = new MutableLiveData<>();
    public LiveData<Boolean> isSuccess = _isSuccess;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public ForgotPasswordViewModel(RecoverPasswordUseCase recoverPasswordUseCase) {
        this.recoverPasswordUseCase = recoverPasswordUseCase;
    }

    public void recoverPassword(String email) {
        _isLoading.setValue(true);
        recoverPasswordUseCase.execute(email, new AuthRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                _isLoading.postValue(false);
                _isSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }
}
