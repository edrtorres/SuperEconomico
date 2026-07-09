package com.uth.supereconomico.di;

import com.uth.supereconomico.data.remote.AuthApi;
import com.uth.supereconomico.data.remote.RetrofitClient;
import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.repositories.AuthRepositoryImpl;
import com.uth.supereconomico.data.repositories.OrderRepositoryImpl;
import com.uth.supereconomico.data.repositories.ProductRepositoryImpl;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.repositories.OrderRepository;
import com.uth.supereconomico.domain.repositories.ProductRepository;
import com.uth.supereconomico.domain.usecases.GetProductsUseCase;
import com.uth.supereconomico.domain.usecases.LoginUseCase;
import com.uth.supereconomico.domain.usecases.RecoverPasswordUseCase;
import com.uth.supereconomico.domain.usecases.RegisterUseCase;
import com.uth.supereconomico.domain.usecases.UpdatePasswordUseCase;
import com.uth.supereconomico.domain.usecases.UpdateProfileUseCase;
import com.uth.supereconomico.domain.usecases.VerifyOtpUseCase;

public class Injection {
    private static AuthRepository authRepository;
    private static ProductRepository productRepository;
    private static OrderRepository orderRepository;

    public static AuthRepository provideAuthRepository() {
        if (authRepository == null) {
            AuthApi authApi = RetrofitClient.getClient().create(AuthApi.class);
            SupabaseApi supabaseApi = RetrofitClient.getClient().create(SupabaseApi.class);
            authRepository = new AuthRepositoryImpl(authApi, supabaseApi);
        }
        return authRepository;
    }

    public static ProductRepository provideProductRepository() {
        if (productRepository == null) {
            SupabaseApi supabaseApi = RetrofitClient.getClient().create(SupabaseApi.class);
            productRepository = new ProductRepositoryImpl(supabaseApi);
        }
        return productRepository;
    }

    public static OrderRepository provideOrderRepository() {
        if (orderRepository == null) {
            SupabaseApi supabaseApi = RetrofitClient.getClient().create(SupabaseApi.class);
            orderRepository = new OrderRepositoryImpl(supabaseApi);
        }
        return orderRepository;
    }

    public static GetProductsUseCase provideGetProductsUseCase() {
        return new GetProductsUseCase(provideProductRepository());
    }

    public static LoginUseCase provideLoginUseCase() {
        return new LoginUseCase(provideAuthRepository());
    }

    public static RegisterUseCase provideRegisterUseCase() {
        return new RegisterUseCase(provideAuthRepository());
    }

    public static RecoverPasswordUseCase provideRecoverPasswordUseCase() {
        return new RecoverPasswordUseCase(provideAuthRepository());
    }

    public static VerifyOtpUseCase provideVerifyOtpUseCase() {
        return new VerifyOtpUseCase(provideAuthRepository());
    }

    public static UpdatePasswordUseCase provideUpdatePasswordUseCase() {
        return new UpdatePasswordUseCase(provideAuthRepository());
    }

    public static UpdateProfileUseCase provideUpdateProfileUseCase() {
        return new UpdateProfileUseCase(provideAuthRepository());
    }
}
