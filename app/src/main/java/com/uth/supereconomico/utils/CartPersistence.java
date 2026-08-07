package com.uth.supereconomico.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uth.supereconomico.domain.entities.Pedido;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartPersistence {
    private static final String PREF_NAME = "cart_prefs";
    private static final String KEY_CART_ITEMS = "cart_items";
    private static SharedPreferences preferences;
    private static final Gson gson = new Gson();

    public static void init(Context context) {
        if (preferences == null) {
            preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveCart(List<Pedido.Item> items) {
        if (preferences == null) return;
        String json = gson.toJson(items);
        preferences.edit().putString(KEY_CART_ITEMS, json).apply();
    }

    public static List<Pedido.Item> loadCart() {
        if (preferences == null) return new ArrayList<>();
        String json = preferences.getString(KEY_CART_ITEMS, null);
        if (json == null || json.isEmpty()) return new ArrayList<>();
        
        Type type = new TypeToken<ArrayList<Pedido.Item>>() {}.getType();
        List<Pedido.Item> items = gson.fromJson(json, type);
        return items != null ? items : new ArrayList<>();
    }

    public static void clearCart() {
        if (preferences == null) return;
        preferences.edit().remove(KEY_CART_ITEMS).apply();
    }
}
