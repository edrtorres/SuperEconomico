package com.uth.supereconomico.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.uth.supereconomico.di.Injection;
import com.uth.supereconomico.domain.usecases.GetProductsUseCase;
import com.uth.supereconomico.domain.usecases.LoginUseCase;
import com.uth.supereconomico.domain.usecases.RecoverPasswordUseCase;
import com.uth.supereconomico.domain.usecases.RegisterUseCase;
import com.uth.supereconomico.domain.usecases.UpdatePasswordUseCase;
import com.uth.supereconomico.domain.usecases.VerifyOtpUseCase;

public class ViewModelFactory implements ViewModelProvider.Factory {
    
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            LoginUseCase loginUseCase = Injection.provideLoginUseCase();
            return (T) new LoginViewModel(loginUseCase);
        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            RegisterUseCase registerUseCase = Injection.provideRegisterUseCase();
            return (T) new RegisterViewModel(registerUseCase);
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            GetProductsUseCase getProductsUseCase = Injection.provideGetProductsUseCase();
            return (T) new HomeViewModel(getProductsUseCase);
        } else if (modelClass.isAssignableFrom(ForgotPasswordViewModel.class)) {
            RecoverPasswordUseCase recoverPasswordUseCase = Injection.provideRecoverPasswordUseCase();
            return (T) new ForgotPasswordViewModel(recoverPasswordUseCase);
        } else if (modelClass.isAssignableFrom(VerifyOtpViewModel.class)) {
            VerifyOtpUseCase verifyOtpUseCase = Injection.provideVerifyOtpUseCase();
            return (T) new VerifyOtpViewModel(verifyOtpUseCase);
        } else if (modelClass.isAssignableFrom(ResetPasswordViewModel.class)) {
            UpdatePasswordUseCase updatePasswordUseCase = Injection.provideUpdatePasswordUseCase();
            return (T) new ResetPasswordViewModel(updatePasswordUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
