package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.domain.entities.Categoria;
import com.uth.supereconomico.domain.entities.Producto;
import com.uth.supereconomico.domain.repositories.ProductRepository;
import com.uth.supereconomico.domain.usecases.GetProductsUseCase;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final GetProductsUseCase getProductsUseCase;
    
    private List<Producto> allProducts = new ArrayList<>();
    private boolean isAhorroEnabled = false;
    private String currentQuery = "";

    private final MutableLiveData<List<Categoria>> _categories = new MutableLiveData<>();
    public LiveData<List<Categoria>> categories = _categories;

    private final MutableLiveData<List<Producto>> _products = new MutableLiveData<>();
    public LiveData<List<Producto>> products = _products;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public HomeViewModel(ProductRepository productRepository, GetProductsUseCase getProductsUseCase) {
        this.productRepository = productRepository;
        this.getProductsUseCase = getProductsUseCase;
    }

    public void loadData() {
        _isLoading.setValue(true);
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        productRepository.getCategories(new ProductRepository.Callback<List<Categoria>>() {
            @Override
            public void onSuccess(List<Categoria> result) {
                _categories.postValue(result);
            }
            @Override
            public void onError(String message) {
                _error.postValue(message);
            }
        });
    }

    public void loadProducts() {
        getProductsUseCase.execute(new ProductRepository.Callback<List<Producto>>() {
            @Override
            public void onSuccess(List<Producto> result) {
                _isLoading.postValue(false);
                allProducts = result;
                applyFilters();
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void loadProductsByCategory(long categoryId) {
        _isLoading.setValue(true);
        productRepository.getProductsByCategory(categoryId, new ProductRepository.Callback<List<Producto>>() {
            @Override
            public void onSuccess(List<Producto> result) {
                _isLoading.postValue(false);
                allProducts = result;
                applyFilters();
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void setAhorroEnabled(boolean enabled) {
        this.isAhorroEnabled = enabled;
        applyFilters();
    }

    public void searchProducts(String query) {
        this.currentQuery = query != null ? query : "";
        applyFilters();
    }

    private void applyFilters() {
        List<Producto> filtered = new ArrayList<>();
        String lowerQuery = currentQuery.toLowerCase().trim();

        for (Producto p : allProducts) {
            boolean matchesQuery = lowerQuery.isEmpty() || p.getNombre().toLowerCase().contains(lowerQuery);
            boolean matchesAhorro = !isAhorroEnabled || p.isEsOferta();

            if (matchesQuery && matchesAhorro) {
                filtered.add(p);
            }
        }
        _products.setValue(filtered);
    }
}
