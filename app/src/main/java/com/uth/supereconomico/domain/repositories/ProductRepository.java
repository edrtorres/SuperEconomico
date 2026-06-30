package com.uth.supereconomico.domain.repositories;

import com.uth.supereconomico.domain.entities.Producto;
import java.util.List;

public interface ProductRepository {
    interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    void getProducts(Callback<List<Producto>> callback);
}
