package com.uth.supereconomico.presentation.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.tabs.TabLayout;
import com.uth.supereconomico.R;
import com.uth.supereconomico.domain.entities.Pedido;
import com.uth.supereconomico.presentation.ui.adapters.OrderAdapter;
import com.uth.supereconomico.presentation.ui.adapters.OrderItemAdapter;
import com.uth.supereconomico.presentation.viewmodel.OrderViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.uth.supereconomico.utils.UserFriendlyError;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements 
        OrderAdapter.OnOrderClickListener, 
        OrderItemAdapter.OnOrderItemClickListener {

    private OrderViewModel viewModel;
    private RecyclerView rvOrders;
    private LinearLayout llEmpty;
    private OrderAdapter adapter;
    private OrderItemAdapter itemsAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private TabLayout tabLayout;
    private AlertDialog detailDialog;
    
    private List<Pedido> allOrders = new ArrayList<>();
    private boolean showingHistory = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ViewModelFactory factory = new ViewModelFactory();
        // Cambiamos 'this' por 'requireActivity()' para compartir el ViewModel con el Carrito
        viewModel = new ViewModelProvider(requireActivity(), factory).get(OrderViewModel.class);

        rvOrders = view.findViewById(R.id.rvOrders);
        llEmpty = view.findViewById(R.id.llEmptyOrders);
        swipeRefresh = view.findViewById(R.id.swipeRefreshOrders);
        tabLayout = view.findViewById(R.id.tabOrders);

        setupRecyclerView();
        setupTabs();
        observeViewModel();

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadOrders());

        return view;
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showingHistory = tab.getPosition() == 1;
                filterAndDisplayOrders();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterAndDisplayOrders() {
        if (allOrders == null) return;

        // Si venimos de una notificación, verificar si el pedido es de historial
        Bundle args = getArguments();
        if (args != null && args.containsKey("order_id")) {
            long targetId = args.getLong("order_id");
            for (Pedido o : allOrders) {
                if (o.getId() != null && o.getId() == targetId) {
                    boolean isEntregado = "entregado".equalsIgnoreCase(o.getEstado());
                    // Si el pedido está entregado y estamos en la pestaña "En Curso", cambiamos de pestaña
                    if (isEntregado && !showingHistory) {
                        tabLayout.selectTab(tabLayout.getTabAt(1));
                        return; // selectTab disparará este método de nuevo con showingHistory = true
                    }
                    // Si el pedido NO está entregado y estamos en "Historial", cambiamos a "En Curso"
                    if (!isEntregado && showingHistory) {
                        tabLayout.selectTab(tabLayout.getTabAt(0));
                        return;
                    }
                    break;
                }
            }
        }
        
        List<Pedido> filtered = new ArrayList<>();
        for (Pedido o : allOrders) {
            boolean isEntregado = "entregado".equalsIgnoreCase(o.getEstado());
            if (showingHistory) {
                if (isEntregado) filtered.add(o);
            } else {
                if (!isEntregado) filtered.add(o);
            }
        }
        
        adapter.setOrders(filtered);

        // Expandir el pedido después de filtrar
        if (args != null && args.containsKey("order_id")) {
            long targetId = args.getLong("order_id");
            adapter.expandOrderById(targetId);
            // Limpiar el argumento para que no se re-expanda en cada refresh manual
            args.remove("order_id");
        }
        
        if (filtered.isEmpty()) {
            rvOrders.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            rvOrders.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadOrders();
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter(this);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);

        itemsAdapter = new OrderItemAdapter(this);
    }

    private void observeViewModel() {
        viewModel.orders.observe(getViewLifecycleOwner(), orders -> {
            swipeRefresh.setRefreshing(false);
            allOrders = orders;
            filterAndDisplayOrders();
        });

        viewModel.orderItems.observe(getViewLifecycleOwner(), items -> {
            if (itemsAdapter != null) {
                itemsAdapter.setItems(items != null ? items : new ArrayList<>());
            }
        });

        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                viewModel.clearSuccessState();
                
                // Navegación inmediata y segura
                try {
                    androidx.navigation.fragment.NavHostFragment.findNavController(this)
                            .navigate(R.id.navigation_cart);
                    Toast.makeText(getContext(), "¡Pedido repetido! Revisa tu carrito", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    // Fallback si el NavController no está listo
                    android.util.Log.e("Navigation", "Error al navegar al carrito: " + e.getMessage());
                }
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), message -> {
            swipeRefresh.setRefreshing(false);
            if (message != null) {
                Toast.makeText(getContext(), UserFriendlyError.fromMessage(message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(Pedido order) {
        mostrarDialogoDetallePedido(order);
    }

    @Override
    public void onRepeatClick(Pedido order) {
        // No mostramos el Toast aquí, lo mostrará el observer al confirmar el éxito
        viewModel.repeatOrder(order.getId());
    }

    private void mostrarDialogoDetallePedido(Pedido order) {
        boolean esEditable = !"entregado".equalsIgnoreCase(order.getEstado());
        itemsAdapter.setEditable(esEditable);
        itemsAdapter.setItems(null);
        viewModel.loadOrderItems(order.getId());
        
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_detail, null);
        RecyclerView rvItems = dialogView.findViewById(R.id.rvOrderDetailItems);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItems.setAdapter(itemsAdapter);
        
        detailDialog = new AlertDialog.Builder(getContext())
                .setTitle("Detalle del Pedido #" + order.getId())
                .setView(dialogView)
                .setPositiveButton("Cerrar", (dialog, which) -> detailDialog = null)
                .setOnDismissListener(dialog -> detailDialog = null)
                .show();
    }

    @Override
    public void onUpdateQuantity(Pedido.Item item, int newQuantity) {
        // Funcionalidad deshabilitada por requerimiento: solo lectura en detalles de pedido
    }

    @Override
    public void onDeleteItem(Pedido.Item item) {
        // Funcionalidad deshabilitada por requerimiento: solo lectura en detalles de pedido
    }
}
