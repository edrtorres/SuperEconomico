package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.domain.entities.Producto;
import com.uth.supereconomico.domain.repositories.ProductRepository;
import com.uth.supereconomico.domain.usecases.GetProductsUseCase;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final GetProductsUseCase getProductsUseCase;

    private final MutableLiveData<List<Producto>> _products = new MutableLiveData<>();
    public LiveData<List<Producto>> products = _products;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public HomeViewModel(GetProductsUseCase getProductsUseCase) {
        this.getProductsUseCase = getProductsUseCase;
    }

    public void loadProducts() {
        getProductsUseCase.execute(new ProductRepository.Callback<List<Producto>>() {
            @Override
            public void onSuccess(List<Producto> result) {
                _products.postValue(result);
            }

            @Override
            public void onError(String message) {
                _error.postValue(message);
            }
        });
    }
}
