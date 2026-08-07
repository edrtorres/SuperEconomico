package com.uth.supereconomico.presentation.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.uth.supereconomico.R;
import com.uth.supereconomico.di.Injection;
import com.uth.supereconomico.domain.entities.Categoria;
import com.uth.supereconomico.domain.entities.Producto;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.presentation.ui.adapters.CategoryAdapter;
import com.uth.supereconomico.presentation.ui.adapters.ProductAdapter;
import com.uth.supereconomico.presentation.viewmodel.HomeViewModel;
import com.uth.supereconomico.presentation.viewmodel.OrderViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.uth.supereconomico.utils.UserFriendlyError;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment implements 
        CategoryAdapter.OnCategoryClickListener, 
        ProductAdapter.OnProductClickListener {

    private HomeViewModel viewModel;
    private OrderViewModel orderViewModel;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private TextView tvGreeting;
    private EditText etSearch;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private View skeletonCategories;
    private View skeletonProducts;
    
    private boolean isAhorroActive = false;
    private View toggleThumb;
    private RelativeLayout customToggle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
        orderViewModel = new ViewModelProvider(requireActivity(), factory).get(OrderViewModel.class);

        tvGreeting = view.findViewById(R.id.tv_home_greeting);
        etSearch = view.findViewById(R.id.et_home_search);
        swipeRefresh = view.findViewById(R.id.swipeRefreshHome);
        customToggle = view.findViewById(R.id.customToggle);
        toggleThumb = view.findViewById(R.id.toggleThumb);
        skeletonCategories = view.findViewById(R.id.skeleton_categories);
        skeletonProducts = view.findViewById(R.id.skeleton_products);
        
        setupGreeting();
        setupSearch();
        setupToggle();
        setupCategories(view);
        setupProducts(view);
        observeViewModel();

        view.findViewById(R.id.tv_home_view_all).setOnClickListener(v -> viewModel.loadProducts());

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadData());
        
        showSkeletonLoading(true);
        viewModel.loadData();
        
        return view;
    }

    private void setupToggle() {
        customToggle.setOnClickListener(v -> {
            isAhorroActive = !isAhorroActive;
            
            float finalPos = isAhorroActive ? convertDpToPixel(24) : 0f;
            
            SpringAnimation springAnim = new SpringAnimation(toggleThumb, DynamicAnimation.TRANSLATION_X);
            SpringForce springForce = new SpringForce(finalPos);
            springForce.setDampingRatio(0.4f); // Rebote premium
            springForce.setStiffness(SpringForce.STIFFNESS_LOW);
            
            springAnim.setSpring(springForce);
            springAnim.start();

            customToggle.setBackgroundResource(isAhorroActive ? 
                    R.drawable.bg_toggle_active : R.drawable.bg_toggle_track);
            
            viewModel.setAhorroEnabled(isAhorroActive);
            
            Toast.makeText(getContext(), isAhorroActive ? "Modo Ahorro Activado" : "Modo Ahorro Desactivado", Toast.LENGTH_SHORT).show();
        });
    }

    private float convertDpToPixel(float dp) {
        return dp * (getResources().getDisplayMetrics().densityDpi / 160f);
    }

    private void applyPillButtonClickEffect(View btn) {
        btn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                            .setInterpolator(new OvershootInterpolator()).start();
                    break;
            }
            return false;
        });
    }

    private void setupGreeting() {
        Usuario user = Injection.provideAuthRepository().getCurrentUser();
        if (user != null && user.getNombreCompleto() != null) {
            String firstName = user.getNombreCompleto().split(" ")[0];
            tvGreeting.setText("¡Hola " + firstName + "!");
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchProducts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategories(View view) {
        rvCategories = view.findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupProducts(View view) {
        rvProducts = view.findViewById(R.id.rv_products);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        rvProducts.setAdapter(productAdapter);
    }

    private void showSkeletonLoading(boolean loading) {
        if (rvCategories == null || rvProducts == null || skeletonCategories == null || skeletonProducts == null) return;
        rvCategories.setVisibility(loading ? View.GONE : View.VISIBLE);
        rvProducts.setVisibility(loading ? View.GONE : View.VISIBLE);
        skeletonCategories.setVisibility(loading ? View.VISIBLE : View.GONE);
        skeletonProducts.setVisibility(loading ? View.VISIBLE : View.GONE);

        if (loading) {
            startSkeletonPulse(skeletonCategories);
            startSkeletonPulse(skeletonProducts);
        } else {
            skeletonCategories.clearAnimation();
            skeletonProducts.clearAnimation();
        }
    }

    private void startSkeletonPulse(View view) {
        AlphaAnimation pulse = new AlphaAnimation(0.45f, 1f);
        pulse.setDuration(650);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(Animation.INFINITE);
        view.startAnimation(pulse);
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), this::showSkeletonLoading);

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.updateCategories(categories);
            swipeRefresh.setRefreshing(false);
        });

        viewModel.products.observe(getViewLifecycleOwner(), products -> {
            productAdapter.updateProducts(products);
            swipeRefresh.setRefreshing(false);
        });

        viewModel.error.observe(getViewLifecycleOwner(), message -> {
            swipeRefresh.setRefreshing(false);
            if (message != null) Toast.makeText(getContext(), UserFriendlyError.fromMessage(message), Toast.LENGTH_SHORT).show();
        });

        orderViewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "¡Pedido creado exitosamente!", Toast.LENGTH_LONG).show();
                orderViewModel.clearSuccessState();
            }
        });
    }

    @Override
    public void onCategoryClick(Categoria categoria) {
        viewModel.loadProductsByCategory(categoria.getId());
    }

    @Override
    public void onProductClick(Producto producto) {
        mostrarDialogoCantidad(producto);
    }

    private void mostrarDialogoCantidad(Producto producto) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_to_cart, null);
        
        TextView tvName = dialogView.findViewById(R.id.tv_dialog_product_name);
        TextView tvPrice = dialogView.findViewById(R.id.tv_dialog_price);
        TextView tvQty = dialogView.findViewById(R.id.tv_dialog_quantity);
        ImageView ivProduct = dialogView.findViewById(R.id.iv_dialog_product_image);
        
        com.google.android.material.button.MaterialButton btnMinus = dialogView.findViewById(R.id.btn_dialog_minus);
        com.google.android.material.button.MaterialButton btnPlus = dialogView.findViewById(R.id.btn_dialog_plus);
        com.google.android.material.button.MaterialButton btnAdd = dialogView.findViewById(R.id.btn_dialog_add);
        com.google.android.material.button.MaterialButton btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);

        tvName.setText(producto.getNombre());
        tvPrice.setText(String.format(Locale.US, "L. %,.2f", producto.getPrecio()));
        
        // Cargar imagen del producto
        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            String url = producto.getImagenUrl();
            if (url.length() > 200) {
                try {
                    byte[] decodedString = android.util.Base64.decode(url, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ivProduct.setImageBitmap(bitmap);
                } catch (Exception e) {
                    com.bumptech.glide.Glide.with(this).load(url).into(ivProduct);
                }
            } else {
                com.bumptech.glide.Glide.with(this).load(url).into(ivProduct);
            }
        }

        final int[] cantidad = {1};
        tvQty.setText(String.valueOf(cantidad[0]));

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            
            // Forzar un ancho mayor para que no se vea angosto
            android.view.WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9); // 90% del ancho de pantalla
            dialog.getWindow().setAttributes(lp);
        }

        btnMinus.setOnClickListener(v -> {
            if (cantidad[0] > 1) {
                cantidad[0]--;
                tvQty.setText(String.valueOf(cantidad[0]));
            }
        });

        btnPlus.setOnClickListener(v -> {
            cantidad[0]++;
            tvQty.setText(String.valueOf(cantidad[0]));
        });

        btnAdd.setOnClickListener(v -> {
            orderViewModel.addToCart(producto.getId(), cantidad[0], producto.getPrecio(), producto.getNombre(), producto.getImagenUrl());
            Toast.makeText(getContext(), "Agregado al carrito", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoCheckout() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_checkout, null);
        RadioGroup rgPayment = view.findViewById(R.id.rgPaymentMethods);

        new AlertDialog.Builder(getContext())
                .setTitle("Finalizar Pedido")
                .setView(view)
                .setPositiveButton("Confirmar Pago", (dialog, which) -> {
                    int checkedId = rgPayment.getCheckedRadioButtonId();
                    String method = "Efectivo";
                    if (checkedId == R.id.rbCard) method = "Tarjeta";
                    
                    orderViewModel.checkout(method);
                })
                .setNegativeButton("Seguir comprando", null)
                .show();
    }
}
