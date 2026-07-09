package com.uth.supereconomico;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.uth.supereconomico.presentation.viewmodel.OrderViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.google.android.material.badge.BadgeDrawable;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import com.uth.supereconomico.utils.OrderSyncWorker;
import com.uth.supereconomico.data.remote.SesionSupabase;
import com.uth.supereconomico.di.Injection;
import java.util.concurrent.TimeUnit;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Asegurar inicialización de sesión
        SesionSupabase.inicializar(this);

        setContentView(R.layout.activity_main);

        // Pedir permiso de notificaciones para Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navView, navController);
            
            // Listener manual para garantizar que el click siempre lleve al destino correcto
            navView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                // Si ya estamos ahí, no hacer nada
                if (navController.getCurrentDestination() != null && 
                    id == navController.getCurrentDestination().getId()) {
                    return false;
                }
                
                // Navegar al ID del menú (que coincide con el ID del nav_graph)
                navController.navigate(id);
                return true;
            });

            // Sincronizar el BottomNav si navegamos programáticamente (ej. desde notificaciones)
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                android.view.MenuItem menuItem = navView.getMenu().findItem(id);
                if (menuItem != null) {
                    menuItem.setChecked(true);
                }
            });
        }

        setupCartBadge(navView);
        setupOrderSync();
        verificarSesionYServicio();
        manejarNotificacion(getIntent());
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        manejarNotificacion(intent);
    }

    private void manejarNotificacion(android.content.Intent intent) {
        if (intent != null && intent.hasExtra("order_id")) {
            long orderId = intent.getLongExtra("order_id", -1);
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_main);
            if (navHostFragment != null) {
                Bundle args = new Bundle();
                args.putLong("order_id", orderId);
                navHostFragment.getNavController().navigate(R.id.navigation_orders, args);
            }
        }
    }

    private void verificarSesionYServicio() {
        if (!SesionSupabase.haySesionActiva()) return;
        
        // Si no tenemos el ID de usuario pero sí el token, recuperamos el perfil
        if (SesionSupabase.obtenerIdUsuario() == null) {
            Injection.provideAuthRepository().login("edwin@test.com", "dummy", null); // Esto es para refrescar el currentUser local
        }

        iniciarServicioTiempoReal();
    }

    private void iniciarServicioTiempoReal() {
        android.content.Intent intent = new android.content.Intent(this, com.uth.supereconomico.utils.OrderRealtimeService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void setupOrderSync() {
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(OrderSyncWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "OrderSync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }

    private void setupCartBadge(BottomNavigationView navView) {
        // Usar requireActivity-like scoping para compartir el ViewModel
        OrderViewModel orderViewModel = new androidx.lifecycle.ViewModelProvider(this, new ViewModelFactory()).get(OrderViewModel.class);
        
        BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_cart);
        badge.setBackgroundColor(getResources().getColor(R.color.primary));
        badge.setBadgeTextColor(getResources().getColor(R.color.white));

        orderViewModel.cart.observe(this, items -> {
            if (items != null && !items.isEmpty()) {
                badge.setVisible(true);
                badge.setNumber(items.size());
            } else {
                badge.setVisible(false);
            }
        });
    }
}
