package com.uth.supereconomico.presentation.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import com.uth.supereconomico.presentation.ui.adapters.CartAdapter;
import com.uth.supereconomico.presentation.viewmodel.OrderViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;

import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemClickListener {

    private OrderViewModel viewModel;
    private RecyclerView rvItems;
    private CartAdapter adapter;
    private TextView tvSubtotal, tvShipping, tvDiscount, tvTotal, tvSavings;
    private LinearLayout llEmpty;
    private View cvSummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), factory).get(OrderViewModel.class);

        rvItems = view.findViewById(R.id.rvCartItems);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvShipping = view.findViewById(R.id.tvShipping);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvTotal = view.findViewById(R.id.tvCartTotal);
        tvSavings = view.findViewById(R.id.tvSavingsText);
        llEmpty = view.findViewById(R.id.llEmptyCart);
        cvSummary = view.findViewById(R.id.cvCheckoutSummary);

        setupRecyclerView();
        observeViewModel();

        view.findViewById(R.id.btnFinalizeOrder).setOnClickListener(v -> mostrarDialogoCheckout());

        return view;
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItems.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.cart.observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                rvItems.setVisibility(View.GONE);
                cvSummary.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
            } else {
                rvItems.setVisibility(View.VISIBLE);
                cvSummary.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
                adapter.setItems(items);
                calculateSummary(items);
            }
        });

        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "¡Pedido creado exitosamente!", Toast.LENGTH_LONG).show();
                viewModel.clearSuccessState();
                Navigation.findNavController(requireView()).navigate(R.id.navigation_orders);
            }
        });
    }

    private void calculateSummary(List<OrderRequest.Item> items) {
        double subtotal = 0;
        double savings = 0;
        
        for (OrderRequest.Item item : items) {
            subtotal += item.precioUnitario * item.cantidad;
            if (item.productoId != null && item.productoId == 2) {
                savings += 50.0 * item.cantidad;
            }
        }

        double shipping = 150.0;
        double total = (subtotal + shipping) - savings;

        tvSubtotal.setText(String.format(Locale.US, "L. %,.2f", subtotal));
        tvShipping.setText(String.format(Locale.US, "L. %,.2f", shipping));
        tvDiscount.setText(String.format(Locale.US, "-L. %,.2f", savings));
        tvTotal.setText(String.format(Locale.US, "L. %,.2f", total));
        tvSavings.setText(String.format(Locale.US, "Ahorras L. %,.2f", savings));
        tvSavings.setVisibility(savings > 0 ? View.VISIBLE : View.GONE);
    }

    private void mostrarDialogoCheckout() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_checkout, null);
        RadioGroup rgPayment = dialogView.findViewById(R.id.rgPaymentMethods);

        new AlertDialog.Builder(getContext())
                .setTitle("Finalizar Pedido")
                .setView(dialogView)
                .setPositiveButton("Confirmar Pago", (dialog, which) -> {
                    int checkedId = rgPayment.getCheckedRadioButtonId();
                    String method = "Efectivo";
                    if (checkedId == R.id.rbCard) method = "Tarjeta";
                    viewModel.checkout(method);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onUpdateQuantity(OrderRequest.Item item, int newQuantity) {
        if (newQuantity <= 0) {
            onDeleteItem(item);
        } else {
            viewModel.updateCartItemQuantity(item, newQuantity);
        }
    }

    @Override
    public void onDeleteItem(OrderRequest.Item item) {
        viewModel.removeFromCart(item);
    }
}
