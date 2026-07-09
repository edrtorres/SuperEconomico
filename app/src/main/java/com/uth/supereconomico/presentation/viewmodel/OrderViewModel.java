package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.domain.repositories.OrderRepository;
import com.uth.supereconomico.utils.CartPersistence;
import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final OrderRepository orderRepository;

    private final MutableLiveData<Boolean> _isSuccess = new MutableLiveData<>();
    public LiveData<Boolean> isSuccess = _isSuccess;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public OrderViewModel(AuthRepository authRepository, OrderRepository orderRepository) {
        this.authRepository = authRepository;
        this.orderRepository = orderRepository;
        // Cargar carrito persistido al iniciar
        _cart.setValue(CartPersistence.loadCart());
    }

    private final MutableLiveData<List<OrderRequest.Item>> _cart = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<OrderRequest.Item>> cart = _cart;

    public void addToCart(long productoId, int cantidad, double precioUnitario, String nombre, String imagenUrl) {
        List<OrderRequest.Item> currentCart = _cart.getValue();
        if (currentCart == null) currentCart = new ArrayList<>();
        
        List<OrderRequest.Item> newCart = new ArrayList<>(currentCart);
        
        boolean found = false;
        for (OrderRequest.Item item : newCart) {
            if (item.productoId != null && item.productoId == productoId) {
                item.cantidad += cantidad;
                found = true;
                break;
            }
        }
        
        if (!found) {
            newCart.add(new OrderRequest.Item(productoId, cantidad, precioUnitario, nombre, imagenUrl));
        }

        _cart.setValue(newCart);
        CartPersistence.saveCart(newCart);
    }

    public void removeFromCart(OrderRequest.Item item) {
        List<OrderRequest.Item> currentCart = _cart.getValue();
        if (currentCart != null) {
            List<OrderRequest.Item> newCart = new ArrayList<>(currentCart);
            newCart.remove(item);
            _cart.setValue(newCart);
            CartPersistence.saveCart(newCart);
        }
    }

    public void updateCartItemQuantity(OrderRequest.Item item, int quantity) {
        List<OrderRequest.Item> currentCart = _cart.getValue();
        if (currentCart != null) {
            List<OrderRequest.Item> newCart = new ArrayList<>(currentCart);
            for (OrderRequest.Item i : newCart) {
                if (i.equals(item)) {
                    i.cantidad = quantity;
                    break;
                }
            }
            _cart.setValue(newCart);
            CartPersistence.saveCart(newCart);
        }
    }

    public void clearCart() {
        _cart.setValue(new ArrayList<>());
        CartPersistence.clearCart();
    }

    private final MutableLiveData<List<OrderRequest>> _orders = new MutableLiveData<>();
    public LiveData<List<OrderRequest>> orders = _orders;

    public void loadOrders() {
        String userId = com.uth.supereconomico.data.remote.SesionSupabase.obtenerIdUsuario();
        
        if (userId == null) {
            Usuario user = authRepository.getCurrentUser();
            if (user != null) {
                userId = user.getId();
            } else {
                // Si aún no tenemos usuario, no podemos cargar pedidos. 
                // Abortamos para evitar lista vacía.
                return;
            }
        }

        _isLoading.setValue(true);
        orderRepository.getOrders(userId, new OrderRepository.Callback<List<OrderRequest>>() {
            @Override
            public void onSuccess(List<OrderRequest> result) {
                _isLoading.postValue(false);
                _orders.postValue(result != null ? result : new ArrayList<>());
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void clearSuccessState() {
        _isSuccess.setValue(false);
    }

    private final MutableLiveData<List<OrderRequest.Item>> _orderItems = new MutableLiveData<>();
    public LiveData<List<OrderRequest.Item>> orderItems = _orderItems;

    public void loadOrderItems(Long orderId) {
        _isLoading.setValue(true);
        orderRepository.getOrderItems(orderId, new OrderRepository.Callback<List<OrderRequest.Item>>() {
            @Override
            public void onSuccess(List<OrderRequest.Item> result) {
                _isLoading.postValue(false);
                _orderItems.postValue(result);
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void deleteOrder(Long orderId) {
        _isLoading.setValue(true);
        orderRepository.deleteOrder(orderId, new OrderRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                _isLoading.postValue(false);
                loadOrders(); // Recargar lista
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void updateItemQuantity(Long itemId, Integer newQuantity, Long orderId) {
        if (newQuantity <= 0) {
            deleteOrderItem(itemId, orderId);
            return;
        }
        _isLoading.setValue(true);
        orderRepository.updateItemQuantity(itemId, newQuantity, new OrderRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadOrderItems(orderId);
                loadOrders(); // Actualizar la lista principal para reflejar el nuevo total
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void deleteOrderItem(Long itemId, Long orderId) {
        _isLoading.setValue(true);
        orderRepository.deleteOrderItem(itemId, new OrderRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Verificar si quedan items
                List<OrderRequest.Item> currentItems = _orderItems.getValue();
                if (currentItems != null && currentItems.size() <= 1) {
                    // Si era el último item, eliminar el pedido
                    deleteOrder(orderId);
                } else {
                    loadOrderItems(orderId);
                    loadOrders(); // Actualizar la lista principal para reflejar el nuevo total
                }
            }
            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void checkout(String method) {
        Usuario user = authRepository.getCurrentUser();
        if (user == null) {
            _error.setValue("Inicia sesión para finalizar el pedido");
            return;
        }

        List<OrderRequest.Item> items = _cart.getValue();
        if (items == null || items.isEmpty()) {
            _error.setValue("El carrito está vacío");
            return;
        }

        double total = 0;
        for (OrderRequest.Item item : items) {
            total += item.precioUnitario * item.cantidad;
        }

        _isLoading.setValue(true);
        orderRepository.createOrder(user.getId(), null, method, total, items, new OrderRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                clearCart();
                _isLoading.postValue(false);
                _isSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void repeatOrder(Long orderId) {
        _isLoading.setValue(true);
        orderRepository.getOrderItems(orderId, new OrderRepository.Callback<List<OrderRequest.Item>>() {
            @Override
            public void onSuccess(List<OrderRequest.Item> result) {
                if (result != null && !result.isEmpty()) {
                    List<OrderRequest.Item> currentCart = _cart.getValue();
                    if (currentCart == null) currentCart = new ArrayList<>();
                    
                    List<OrderRequest.Item> newCart = new ArrayList<>(currentCart);
                    
                    for (OrderRequest.Item item : result) {
                        boolean found = false;
                        for (OrderRequest.Item cartItem : newCart) {
                            if (cartItem.productoId != null && cartItem.productoId.equals(item.productoId)) {
                                cartItem.cantidad += item.cantidad;
                                found = true;
                                break;
                            }
                        }
                        
                        if (!found) {
                            String nombre = item.nombre;
                            String imagenUrl = item.imagenUrl;
                            
                            // Si los campos locales son nulos (vienen del historial), usar los del join
                            if (item.producto != null) {
                                if (nombre == null || nombre.isEmpty()) nombre = item.producto.nombre;
                                if (imagenUrl == null || imagenUrl.isEmpty()) imagenUrl = item.producto.imagenUrl;
                            }

                            newCart.add(new OrderRequest.Item(
                                item.productoId, 
                                item.cantidad, 
                                item.precioUnitario, 
                                nombre, 
                                imagenUrl
                            ));
                        }
                    }
                    
                    _cart.setValue(newCart);
                    CartPersistence.saveCart(newCart);
                    
                    _isLoading.setValue(false);
                    _isSuccess.setValue(true);
                    android.util.Log.d("RepeatOrder", "Productos añadidos: " + result.size());
                } else {
                    _isLoading.setValue(false);
                    _error.setValue("No se encontraron productos en este pedido");
                }
            }
            @Override
            public void onError(String message) {
                _isLoading.setValue(false);
                _error.setValue(message);
            }
        });
    }
}
