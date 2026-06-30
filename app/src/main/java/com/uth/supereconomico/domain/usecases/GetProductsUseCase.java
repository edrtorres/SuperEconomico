package com.uth.supereconomico.domain.usecases;

import com.uth.supereconomico.domain.entities.Producto;
import com.uth.supereconomico.domain.repositories.ProductRepository;
import java.util.List;

public class GetProductsUseCase {
    private final ProductRepository productRepository;

    public GetProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(ProductRepository.Callback<List<Producto>> callback) {
        productRepository.getProducts(callback);
    }
}
