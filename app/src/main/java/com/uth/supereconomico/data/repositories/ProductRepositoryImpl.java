package com.uth.supereconomico.data.repositories;

import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.CategoriaDTO;
import com.uth.supereconomico.data.remote.models.ProductDTO;
import com.uth.supereconomico.domain.entities.Categoria;
import com.uth.supereconomico.domain.entities.Producto;
import com.uth.supereconomico.domain.repositories.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

public class ProductRepositoryImpl implements ProductRepository {
    private final SupabaseApi supabaseApi;

    public ProductRepositoryImpl(SupabaseApi supabaseApi) {
        this.supabaseApi = supabaseApi;
    }

    @Override
    public void getCategories(Callback<List<Categoria>> callback) {
        supabaseApi.getCategorias("*").enqueue(new retrofit2.Callback<List<CategoriaDTO>>() {
            @Override
            public void onResponse(Call<List<CategoriaDTO>> call, Response<List<CategoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Categoria> domainCategories = new ArrayList<>();
                    for (CategoriaDTO dto : response.body()) {
                        domainCategories.add(dto.toDomain());
                    }
                    callback.onSuccess(domainCategories);
                } else {
                    callback.onError("Error al obtener categorías: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CategoriaDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void getProducts(Callback<List<Producto>> callback) {
        supabaseApi.getProductos("eq.true", "*").enqueue(new retrofit2.Callback<List<ProductDTO>>() {
            @Override
            public void onResponse(Call<List<ProductDTO>> call, Response<List<ProductDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> domainProducts = new ArrayList<>();
                    for (ProductDTO dto : response.body()) {
                        domainProducts.add(dto.toDomain());
                    }
                    callback.onSuccess(domainProducts);
                } else {
                    callback.onError("Error al obtener productos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProductDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void getProductsByCategory(long categoryId, Callback<List<Producto>> callback) {
        supabaseApi.getProductosPorCategoria("eq." + categoryId, "eq.true", "*").enqueue(new retrofit2.Callback<List<ProductDTO>>() {
            @Override
            public void onResponse(Call<List<ProductDTO>> call, Response<List<ProductDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> domainProducts = new ArrayList<>();
                    for (ProductDTO dto : response.body()) {
                        domainProducts.add(dto.toDomain());
                    }
                    callback.onSuccess(domainProducts);
                } else {
                    callback.onError("Error al obtener productos por categoría: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProductDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
