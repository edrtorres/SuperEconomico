package com.uth.supereconomico.data.repositories;

import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.ProductDTO;
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
    public void getProducts(Callback<List<Producto>> callback) {
        supabaseApi.getProductos("true", "*").enqueue(new retrofit2.Callback<List<ProductDTO>>() {
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
}
