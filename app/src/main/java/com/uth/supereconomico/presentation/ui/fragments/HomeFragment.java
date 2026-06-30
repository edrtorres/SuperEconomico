package com.uth.supereconomico.presentation.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.uth.supereconomico.R;
import com.uth.supereconomico.domain.entities.Categoria;
import com.uth.supereconomico.presentation.ui.adapters.CategoryAdapter;
import com.uth.supereconomico.presentation.ui.adapters.ProductAdapter;
import com.uth.supereconomico.presentation.viewmodel.HomeViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        setupCategories(view);
        setupProducts(view);
        observeViewModel();

        viewModel.loadProducts();
        
        return view;
    }

    private void observeViewModel() {
        viewModel.products.observe(getViewLifecycleOwner(), products -> {
            productAdapter.updateProducts(products);
        });

        viewModel.error.observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCategories(View view) {
        RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
        List<Categoria> demoCategories = new ArrayList<>();
        demoCategories.add(new Categoria(1, "Frutas", ""));
        demoCategories.add(new Categoria(2, "Lácteos", ""));
        demoCategories.add(new Categoria(3, "Carnes", ""));
        demoCategories.add(new Categoria(4, "Bebidas", ""));
        demoCategories.add(new Categoria(5, "Limpieza", ""));
        
        CategoryAdapter adapter = new CategoryAdapter(demoCategories);
        rvCategories.setAdapter(adapter);
    }

    private void setupProducts(View view) {
        RecyclerView rvProducts = view.findViewById(R.id.rv_products);
        productAdapter = new ProductAdapter(new ArrayList<>());
        rvProducts.setAdapter(productAdapter);
    }
}
