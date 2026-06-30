package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.usecases.VerifyOtpUseCase;

public class VerifyOtpViewModel extends ViewModel {
    private final VerifyOtpUseCase verifyOtpUseCase;

    private final MutableLiveData<Usuario> _user = new MutableLiveData<>();
    public LiveData<Usuario> user = _user;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public VerifyOtpViewModel(VerifyOtpUseCase verifyOtpUseCase) {
        this.verifyOtpUseCase = verifyOtpUseCase;
    }

    public void verifyOtp(String email, String otp) {
        _isLoading.setValue(true);
        verifyOtpUseCase.execute(email, otp, new AuthRepository.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario result) {
                _isLoading.postValue(false);
                _user.postValue(result);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }
}
