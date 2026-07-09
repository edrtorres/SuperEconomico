package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.usecases.UpdateProfileUseCase;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final UpdateProfileUseCase updateProfileUseCase;

    private final MutableLiveData<Usuario> _user = new MutableLiveData<>();
    public LiveData<Usuario> user = _user;

    private final MutableLiveData<List<DireccionRequest>> _addresses = new MutableLiveData<>();
    public LiveData<List<DireccionRequest>> addresses = _addresses;

    private final MutableLiveData<List<com.uth.supereconomico.domain.entities.MetodoPago>> _payments = new MutableLiveData<>();
    public LiveData<List<com.uth.supereconomico.domain.entities.MetodoPago>> payments = _payments;

    private final MutableLiveData<Boolean> _isUpdateSuccess = new MutableLiveData<>();
    public LiveData<Boolean> isUpdateSuccess = _isUpdateSuccess;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public ProfileViewModel(AuthRepository authRepository, UpdateProfileUseCase updateProfileUseCase) {
        this.authRepository = authRepository;
        this.updateProfileUseCase = updateProfileUseCase;
        loadCurrentUser();
    }

    public void loadCurrentUser() {
        Usuario currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            _user.setValue(currentUser);
            loadAddresses(currentUser.getId());
            loadPaymentMethods(currentUser.getId());
        }
    }

    public void loadPaymentMethods(String perfilId) {
        _isLoading.setValue(true);
        authRepository.getPaymentMethods(perfilId, new AuthRepository.Callback<List<com.uth.supereconomico.domain.entities.MetodoPago>>() {
            @Override
            public void onSuccess(List<com.uth.supereconomico.domain.entities.MetodoPago> result) {
                _isLoading.postValue(false);
                _payments.postValue(result);
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void addPaymentMethod(String titular, String numero, String vencimiento) {
        Usuario currentUser = authRepository.getCurrentUser();
        if (currentUser == null) return;

        com.uth.supereconomico.data.remote.models.MetodoPagoDTO dto = new com.uth.supereconomico.data.remote.models.MetodoPagoDTO();
        dto.perfilId = currentUser.getId();
        dto.titular = titular;
        dto.tipo = "tarjeta";
        // Enmascarar número para guardar solo los últimos 4
        dto.numeroEnmascarado = "**** **** **** " + numero.substring(numero.length() - 4);
        dto.fechaVencimiento = vencimiento;

        _isLoading.setValue(true);
        authRepository.addPaymentMethod(dto, new AuthRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadPaymentMethods(currentUser.getId());
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void deletePaymentMethod(long methodId) {
        Usuario currentUser = authRepository.getCurrentUser();
        if (currentUser == null) return;

        _isLoading.setValue(true);
        authRepository.deletePaymentMethod(methodId, new AuthRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadPaymentMethods(currentUser.getId());
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void loadAddresses(String perfilId) {
        _isLoading.setValue(true);
        authRepository.getAddresses(perfilId, new AuthRepository.Callback<List<DireccionRequest>>() {
            @Override
            public void onSuccess(List<DireccionRequest> result) {
                _isLoading.postValue(false);
                _addresses.postValue(result);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void addAddress(String etiqueta, String direccion, double lat, double lng) {
        Usuario currentUser = authRepository.getCurrentUser();
        if (currentUser == null) return;

        DireccionRequest newAddr = new DireccionRequest(etiqueta, direccion, lat, lng);
        newAddr.perfilId = currentUser.getId();

        _isLoading.setValue(true);
        authRepository.addAddress(newAddr, new AuthRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadAddresses(currentUser.getId());
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void deleteAddress(long addressId) {
        Usuario currentUser = authRepository.getCurrentUser();
        if (currentUser == null) return;

        _isLoading.setValue(true);
        authRepository.deleteAddress(addressId, new AuthRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadAddresses(currentUser.getId());
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void updateProfile(String nombreCompleto, String telefono, String direccion, String descripcion, String avatarUrl) {
        Usuario currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            _error.setValue("No hay una sesión activa");
            return;
        }

        _isLoading.setValue(true);
        updateProfileUseCase.execute(currentUser.getId(), nombreCompleto, telefono, direccion, descripcion, avatarUrl, new AuthRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                _isLoading.postValue(false);
                _isUpdateSuccess.postValue(true);
                loadCurrentUser(); // Recargar para reflejar cambios
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    // Mantener versión simple para la foto de perfil si se usa sola
    public void updateProfilePhoto(String avatarUrl) {
        Usuario u = _user.getValue();
        if (u != null) {
            updateProfile(u.getNombreCompleto(), u.getTelefono(), u.getDireccion(), u.getDescripcion(), avatarUrl);
        }
    }

    public void logout() {
        authRepository.logout();
    }
}
