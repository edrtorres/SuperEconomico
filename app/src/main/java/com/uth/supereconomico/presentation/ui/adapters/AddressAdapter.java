package com.uth.supereconomico.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final List<DireccionRequest> addresses = new ArrayList<>();
    private final OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onDeleteClick(DireccionRequest address);
    }

    public AddressAdapter(OnAddressClickListener listener) {
        this.listener = listener;
    }

    public void setAddresses(List<DireccionRequest> newAddresses) {
        this.addresses.clear();
        if (newAddresses != null) {
            this.addresses.addAll(newAddresses);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DireccionRequest address = addresses.get(position);
        holder.tvLabel.setText(address.etiqueta);
        holder.tvAddress.setText(address.direccion);
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(address));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvAddress;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvItemAddressLabel);
            tvAddress = itemView.findViewById(R.id.tvItemAddressText);
            btnDelete = itemView.findViewById(R.id.btnDeleteAddress);
        }
    }
}
