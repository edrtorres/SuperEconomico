package com.uth.supereconomico.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;

public final class FormateadorTelefono {

    private FormateadorTelefono() {
    }

    public static void aplicar(TextInputEditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            private boolean editando;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editando) {
                    return;
                }

                String valor = editable.toString();
                if (!pareceTelefono(valor)) {
                    return;
                }

                String formateado = formatear(valor);
                if (valor.equals(formateado)) {
                    return;
                }

                editando = true;
                campo.setText(formateado);
                campo.setSelection(formateado.length());
                editando = false;
            }
        });
    }

    public static String limpiar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[^0-9]", "");
    }

    public static String formatear(String valor) {
        String limpio = limpiar(valor);
        if (limpio.length() > 8) {
            limpio = limpio.substring(0, 8);
        }
        if (limpio.length() <= 4) {
            return limpio;
        }
        return limpio.substring(0, 4) + "-" + limpio.substring(4);
    }

    public static boolean esTelefonoValido(String valor) {
        return limpiar(valor).length() == 8;
    }

    private static boolean pareceTelefono(String valor) {
        if (valor == null || valor.contains("@")) {
            return false;
        }
        return valor.matches("[0-9\\- ]{1,10}");
    }
}
