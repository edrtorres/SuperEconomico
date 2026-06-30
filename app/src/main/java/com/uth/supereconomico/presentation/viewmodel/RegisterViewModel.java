package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.usecases.RegisterUseCase;
import java.util.List;

public class RegisterViewModel extends ViewModel {
    private final RegisterUseCase registerUseCase;

    private final MutableLiveData<Boolean> _isSuccess = new MutableLiveData<>();
    public LiveData<Boolean> isSuccess = _isSuccess;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public RegisterViewModel(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    public void register(String email, String password, String nombreCompleto, String telefono, List<DireccionRequest> direcciones) {
        _isLoading.setValue(true);
        registerUseCase.execute(email, password, nombreCompleto, telefono, direcciones, new AuthRepository.Callback<Void>() {
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
