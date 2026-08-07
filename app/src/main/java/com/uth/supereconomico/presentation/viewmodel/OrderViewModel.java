package com.uth.supereconomico.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.uth.supereconomico.domain.entities.Pedido;
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

    private final MutableLiveData<List<Pedido.Item>> _cart = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Pedido.Item>> cart = _cart;

    public void addToCart(long productoId, int cantidad, double precioUnitario, String nombre, String imagenUrl) {
        List<Pedido.Item> currentCart = _cart.getValue();
        if (currentCart == null) currentCart = new ArrayList<>();

        List<Pedido.Item> newCart = new ArrayList<>(currentCart);

        boolean found = false;
        for (Pedido.Item item : newCart) {
            if (item.getProductoId() != null && item.getProductoId() == productoId) {
                // Reemplazamos el item con uno nuevo con la cantidad actualizada ya que son inmutables
                newCart.remove(item);
                newCart.add(new Pedido.Item(item.getId(), item.getPedidoId(), item.getProductoId(),
                        item.getCantidad() + cantidad, item.getPrecioUnitario(), item.getNombre(), item.getImagenUrl()));
                found = true;
                break;
            }
        }

        if (!found) {
            newCart.add(new Pedido.Item(null, null, productoId, cantidad, precioUnitario, nombre, imagenUrl));
        }

        _cart.setValue(newCart);
        CartPersistence.saveCart(newCart);
    }

    public void removeFromCart(Pedido.Item item) {
        List<Pedido.Item> currentCart = _cart.getValue();
        if (currentCart != null) {
            List<Pedido.Item> newCart = new ArrayList<>(currentCart);
            newCart.remove(item);
            _cart.setValue(newCart);
            CartPersistence.saveCart(newCart);
        }
    }

    public void updateCartItemQuantity(Pedido.Item item, int quantity) {
        List<Pedido.Item> currentCart = _cart.getValue();
        if (currentCart != null) {
            List<Pedido.Item> newCart = new ArrayList<>(currentCart);
            for (int i = 0; i < newCart.size(); i++) {
                if (newCart.get(i).equals(item)) {
                    newCart.set(i, new Pedido.Item(item.getId(), item.getPedidoId(), item.getProductoId(),
                            quantity, item.getPrecioUnitario(), item.getNombre(), item.getImagenUrl()));
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

    private final MutableLiveData<List<Pedido>> _orders = new MutableLiveData<>();
    public LiveData<List<Pedido>> orders = _orders;

    public void loadOrders() {
        String userId = com.uth.supereconomico.data.remote.SesionSupabase.obtenerIdUsuario();

        if (userId == null) {
            Usuario user = authRepository.getCurrentUser();
            if (user != null) {
                userId = user.getId();
            } else {
                return;
            }
        }

        _isLoading.setValue(true);
        orderRepository.getOrders(userId, new OrderRepository.Callback<List<Pedido>>() {
            @Override
            public void onSuccess(List<Pedido> result) {
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

    private final MutableLiveData<List<Pedido.Item>> _orderItems = new MutableLiveData<>();
    public LiveData<List<Pedido.Item>> orderItems = _orderItems;

    public void loadOrderItems(Long orderId) {
        _isLoading.setValue(true);
        orderRepository.getOrderItems(orderId, new OrderRepository.Callback<List<Pedido.Item>>() {
            @Override
            public void onSuccess(List<Pedido.Item> result) {
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
                loadOrders();
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
                loadOrders();
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
                List<Pedido.Item> currentItems = _orderItems.getValue();
                if (currentItems != null && currentItems.size() <= 1) {
                    deleteOrder(orderId);
                } else {
                    loadOrderItems(orderId);
                    loadOrders();
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
        checkout(null, method);
    }

    public void checkout(Long direccionId, String method) {
        Usuario user = authRepository.getCurrentUser();
        if (user == null) {
            _error.setValue("Inicia sesión para finalizar el pedido");
            return;
        }

        List<Pedido.Item> items = _cart.getValue();
        if (items == null || items.isEmpty()) {
            _error.setValue("El carrito está vacío");
            return;
        }

        double total = 0;
        for (Pedido.Item item : items) {
            total += item.getPrecioUnitario() * item.getCantidad();
        }

        _isLoading.setValue(true);
        orderRepository.createOrder(user.getId(), direccionId, method, total, items, new OrderRepository.Callback<Void>() {
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
        orderRepository.getOrderItems(orderId, new OrderRepository.Callback<List<Pedido.Item>>() {
            @Override
            public void onSuccess(List<Pedido.Item> result) {
                if (result != null && !result.isEmpty()) {
                    List<Pedido.Item> currentCart = _cart.getValue();
                    if (currentCart == null) currentCart = new ArrayList<>();

                    List<Pedido.Item> newCart = new ArrayList<>(currentCart);

                    for (Pedido.Item item : result) {
                        boolean found = false;
                        for (Pedido.Item cartItem : newCart) {
                            if (cartItem.getProductoId() != null && cartItem.getProductoId().equals(item.getProductoId())) {
                                newCart.remove(cartItem);
                                newCart.add(new Pedido.Item(cartItem.getId(), cartItem.getPedidoId(), cartItem.getProductoId(),
                                        cartItem.getCantidad() + item.getCantidad(), cartItem.getPrecioUnitario(), cartItem.getNombre(), cartItem.getImagenUrl()));
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            newCart.add(new Pedido.Item(
                                null,
                                null,
                                item.getProductoId(),
                                item.getCantidad(),
                                item.getPrecioUnitario(),
                                item.getNombre(),
                                item.getImagenUrl()
                            ));
                        }
                    }

                    _cart.setValue(newCart);
                    CartPersistence.saveCart(newCart);

                    _isLoading.setValue(false);
                    _isSuccess.setValue(true);
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
