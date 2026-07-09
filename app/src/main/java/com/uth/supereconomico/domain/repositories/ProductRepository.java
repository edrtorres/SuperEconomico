package com.uth.supereconomico.domain.repositories;

import com.uth.supereconomico.domain.entities.Categoria;
import com.uth.supereconomico.domain.entities.Producto;
import java.util.List;

public interface ProductRepository {
    interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    void getCategories(Callback<List<Categoria>> callback);
    void getProducts(Callback<List<Producto>> callback);
    void getProductsByCategory(long categoryId, Callback<List<Producto>> callback);
}
