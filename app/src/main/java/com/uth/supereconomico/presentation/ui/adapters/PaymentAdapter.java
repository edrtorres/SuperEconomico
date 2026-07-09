package com.uth.supereconomico.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uth.supereconomico.R;
import com.uth.supereconomico.domain.entities.MetodoPago;
import java.util.ArrayList;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private final List<MetodoPago> payments = new ArrayList<>();
    private final OnPaymentClickListener listener;

    public interface OnPaymentClickListener {
        void onDeleteClick(MetodoPago payment);
    }

    public PaymentAdapter(OnPaymentClickListener listener) {
        this.listener = listener;
    }

    public void setPayments(List<MetodoPago> newPayments) {
        this.payments.clear();
        if (newPayments != null) this.payments.addAll(newPayments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MetodoPago payment = payments.get(position);
        holder.tvNumber.setText(payment.getNumeroEnmascarado());
        holder.tvTitular.setText(payment.getTitular());
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(payment));
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvTitular;
        View btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvCardNumber);
            tvTitular = itemView.findViewById(R.id.tvCardTitular);
            btnDelete = itemView.findViewById(R.id.btnDeletePayment);
        }
    }
}
