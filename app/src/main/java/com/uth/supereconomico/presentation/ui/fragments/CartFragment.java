package com.uth.supereconomico.presentation.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.di.Injection;
import com.uth.supereconomico.domain.entities.Pedido;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.entities.MetodoPago;
import com.uth.supereconomico.presentation.ui.adapters.CartAdapter;
import com.uth.supereconomico.presentation.viewmodel.OrderViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.uth.supereconomico.utils.UserFriendlyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.OnCartClickListener {

    private OrderViewModel viewModel;
    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvSubtotal, tvEnvio, tvTotal;
    private MaterialButton btnCheckout;
    private LinearLayout llEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), factory).get(OrderViewModel.class);

        rvCart = view.findViewById(R.id.rvCartItems);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvEnvio = view.findViewById(R.id.tvShipping);
        tvTotal = view.findViewById(R.id.tvCartTotal);
        btnCheckout = view.findViewById(R.id.btnFinalizeOrder);
        llEmpty = view.findViewById(R.id.llEmptyCart);

        setupRecyclerView();
        observeViewModel();

        btnCheckout.setOnClickListener(v -> mostrarDialogoCheckout());

        return view;
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.cart.observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            if (items.isEmpty()) {
                rvCart.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
                btnCheckout.setEnabled(false);
            } else {
                rvCart.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
                btnCheckout.setEnabled(true);
            }
            calculateSummary(items);
        });

        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show();
                viewModel.clearSuccessState();
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), UserFriendlyError.fromMessage(error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateSummary(List<Pedido.Item> items) {
        double subtotal = 0;
        for (Pedido.Item item : items) {
            subtotal += item.getPrecioUnitario() * item.getCantidad();
        }
        double envio = subtotal > 0 ? 50.0 : 0;
        double total = subtotal + envio;

        tvSubtotal.setText(String.format(Locale.US, "L. %,.2f", subtotal));
        tvEnvio.setText(String.format(Locale.US, "L. %,.2f", envio));
        tvTotal.setText(String.format(Locale.US, "L. %,.2f", total));
    }

    private void mostrarDialogoCheckout() {
        com.uth.supereconomico.domain.entities.Usuario user = Injection.provideAuthRepository().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Inicia sesión para finalizar el pedido", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRepository authRepository = Injection.provideAuthRepository();
        authRepository.getAddresses(user.getId(), new AuthRepository.Callback<List<DireccionRequest>>() {
            @Override
            public void onSuccess(List<DireccionRequest> addresses) {
                authRepository.getPaymentMethods(user.getId(), new AuthRepository.Callback<List<MetodoPago>>() {
                    @Override
                    public void onSuccess(List<MetodoPago> payments) {
                        mostrarDialogoCheckout(addresses, payments);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getContext(), UserFriendlyError.fromMessage(message), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), UserFriendlyError.fromMessage(message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCheckout(List<DireccionRequest> addresses, List<MetodoPago> payments) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_checkout, null);
        RadioGroup rgPayment = view.findViewById(R.id.rgPaymentMethods);
        Spinner spDeliveryAddress = view.findViewById(R.id.spDeliveryAddress);
        TextView tvAddressHint = view.findViewById(R.id.tvAddressHint);
        TextView tvCardRequirement = view.findViewById(R.id.tvCardRequirement);

        List<DireccionRequest> validAddresses = new ArrayList<>();
        if (addresses != null) {
            for (DireccionRequest address : addresses) {
                if (address != null && address.id != null) validAddresses.add(address);
            }
        }

        List<String> addressLabels = new ArrayList<>();
        for (DireccionRequest address : validAddresses) {
            String label = address.etiqueta != null && !address.etiqueta.trim().isEmpty()
                    ? address.etiqueta.trim()
                    : "Dirección";
            String detail = address.direccion != null ? address.direccion : "";
            addressLabels.add(label + (detail.trim().isEmpty() ? "" : " - " + detail));
        }
        ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                addressLabels
        );
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDeliveryAddress.setAdapter(addressAdapter);
        spDeliveryAddress.setEnabled(!validAddresses.isEmpty());

        if (!validAddresses.isEmpty()) {
            tvAddressHint.setVisibility(View.GONE);
        } else {
            tvAddressHint.setVisibility(View.VISIBLE);
        }

        boolean hasCard = false;
        if (payments != null) {
            for (MetodoPago payment : payments) {
                if (payment != null && "tarjeta".equalsIgnoreCase(payment.getTipo())) {
                    hasCard = true;
                    break;
                }
            }
        }
        tvCardRequirement.setText(hasCard
                ? "Tarjeta registrada disponible para este pedido."
                : "Para pagar con tarjeta, primero registra una tarjeta en Perfil > Métodos de pago.");

        AlertDialog checkoutDialog = new AlertDialog.Builder(getContext())
                .setTitle("Finalizar Pedido")
                .setView(view)
                .setPositiveButton("Confirmar Pago", null)
                .setNegativeButton("Seguir comprando", null)
                .create();

        checkoutDialog.setOnShowListener(dialog -> checkoutDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(button -> {
                    int selectedAddressPosition = spDeliveryAddress.getSelectedItemPosition();
                    if (selectedAddressPosition < 0 || selectedAddressPosition >= validAddresses.size()) {
                        Toast.makeText(getContext(), "Selecciona una dirección de entrega", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Long direccionId = validAddresses.get(selectedAddressPosition).id;

                    int checkedId = rgPayment.getCheckedRadioButtonId();
                    String method = "Efectivo";
                    if (checkedId == R.id.rbCard) method = "Tarjeta";
                    if (checkedId == R.id.rbCard) {
                        boolean cardAvailable = false;
                        if (payments != null) {
                            for (MetodoPago payment : payments) {
                                if (payment != null && "tarjeta".equalsIgnoreCase(payment.getTipo())) {
                                    cardAvailable = true;
                                    break;
                                }
                            }
                        }
                        if (!cardAvailable) {
                            Toast.makeText(getContext(), "Registra una tarjeta en Perfil > Métodos de pago", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    viewModel.checkout(direccionId, method);
                    checkoutDialog.dismiss();
                }));

        checkoutDialog.show();
    }

    @Override
    public void onUpdateQuantity(Pedido.Item item, int newQuantity) {
        viewModel.updateCartItemQuantity(item, newQuantity);
    }

    @Override
    public void onDeleteItem(Pedido.Item item) {
        viewModel.removeFromCart(item);
    }
}
