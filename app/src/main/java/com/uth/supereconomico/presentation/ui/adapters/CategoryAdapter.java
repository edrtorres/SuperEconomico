package com.uth.supereconomico.presentation.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uth.supereconomico.R;
import com.uth.supereconomico.domain.entities.Categoria;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final List<Categoria> categorias;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Categoria categoria);
    }

    public CategoryAdapter(List<Categoria> categorias, OnCategoryClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    public void updateCategories(List<Categoria> newCategories) {
        this.categorias.clear();
        if (newCategories != null) {
            this.categorias.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.tvName.setText(categoria.getNombre());
        
        // Aplicar gradientes dinámicos según posición (Ahora en cuadros redondeados)
        int resId = R.drawable.bg_category_gradient_1;
        switch (position % 4) {
            case 1: resId = R.drawable.bg_category_gradient_2; break;
            case 2: resId = R.drawable.bg_category_gradient_3; break;
            case 3: resId = R.drawable.bg_category_gradient_4; break;
        }
        
        holder.cvIcon.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
        holder.cvIcon.setBackgroundResource(resId);

        String iconSource = categoria.getIconoUrl();
        if (iconSource != null && !iconSource.isEmpty()) {
            holder.ivIcon.setColorFilter(null); 
            if (iconSource.length() > 100) {
                try {
                    byte[] decodedString = Base64.decode(iconSource, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivIcon.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    Glide.with(holder.itemView.getContext()).load(iconSource).into(holder.ivIcon);
                }
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(iconSource)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivIcon);
            }
        } else {
            holder.ivIcon.setImageResource(android.R.drawable.ic_menu_gallery);
            holder.ivIcon.setColorFilter(android.graphics.Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(categoria));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.card.MaterialCardView cvIcon;
        ImageView ivIcon;
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvIcon = itemView.findViewById(R.id.cvCategoryIcon);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
