package com.uth.supereconomico.presentation.ui.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.OrderRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<OrderRequest> orders = new ArrayList<>();
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderRequest order);
        void onRepeatClick(OrderRequest order);
    }

    public OrderAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    private int expandedPosition = -1;
    private Long highlightedOrderId = -1L;

    public void setOrders(List<OrderRequest> newOrders) {
        this.orders.clear();
        if (newOrders != null) {
            this.orders.addAll(newOrders);
        }
        expandedPosition = -1; // Reset al actualizar
        highlightedOrderId = -1L;
        notifyDataSetChanged();
    }

    public void expandOrderById(Long orderId) {
        if (orderId == null) return;
        this.highlightedOrderId = orderId;
        for (int i = 0; i < orders.size(); i++) {
            if (orderId.equals(orders.get(i).id)) {
                expandedPosition = i;
                notifyDataSetChanged(); // Forzamos redibujado completo para aplicar borde
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderRequest order = orders.get(position);
        holder.tvOrderNumber.setText("Orden #" + (order.id != null ? order.id : "---"));
        holder.tvOrderTotal.setText(String.format(Locale.US, "L. %,.2f", order.total));
        
        String estado = order.estado != null ? order.estado.toLowerCase() : "pendiente";
        boolean isExpanded = position == expandedPosition;
        boolean isHighlighted = order.id != null && order.id.equals(highlightedOrderId);

        // Aplicar resaltado especial con animación si es el pedido de la notificación
        if (isHighlighted) {
            animateHighlight(holder.cardView);
        } else {
            holder.cardView.setStrokeColor(android.graphics.Color.parseColor("#E2E8F0"));
            holder.cardView.setStrokeWidth(2);
            holder.cardView.setCardElevation(2f);
        }
        
        holder.llExpandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivChevron.setRotation(isExpanded ? 180 : 0);

        String fecha = (order.creadoAt != null && order.creadoAt.length() >= 10) 
                ? order.creadoAt.substring(0, 10) 
                : "Recién procesado";

        if ("entregado".equals(estado)) {
            holder.tvOrderDate.setText(fecha + " - Entregado");
        } else if ("cancelado".equals(estado)) {
            holder.tvOrderDate.setText(fecha + " - Cancelado");
        } else {
            holder.tvOrderDate.setText(fecha + " - Llegada estimada: 18:30 hrs");
        }
        
        // El botón de repetir ahora está visible para todos los estados (en curso e historial)
        holder.btnRepeat.setVisibility(View.VISIBLE);
        
        setupTimeline(holder, estado);

        holder.itemView.setOnClickListener(v -> {
            int previousExpanded = expandedPosition;
            if (isExpanded) {
                expandedPosition = -1;
            } else {
                expandedPosition = position;
            }
            notifyItemChanged(previousExpanded);
            notifyItemChanged(expandedPosition);
        });

        holder.btnRepeat.setOnClickListener(v -> listener.onRepeatClick(order));
        holder.btnViewDetails.setOnClickListener(v -> listener.onOrderClick(order));
    }

    private void setupTimeline(ViewHolder holder, String estado) {
        int primaryColor = holder.itemView.getContext().getResources().getColor(R.color.primary);
        int mutedColor = holder.itemView.getContext().getResources().getColor(R.color.text_muted);
        int neutralLine = android.graphics.Color.parseColor("#E2E8F0");

        // Reset
        holder.ivStep1.setColorFilter(mutedColor);
        holder.tvStep1.setTextColor(mutedColor);
        holder.arrow1.setColorFilter(neutralLine);
        
        holder.ivStep2.setColorFilter(mutedColor);
        holder.tvStep2.setTextColor(mutedColor);
        holder.arrow2.setColorFilter(neutralLine);
        
        holder.ivStep3.setColorFilter(mutedColor);
        holder.tvStep3.setTextColor(mutedColor);
        holder.arrow3.setColorFilter(neutralLine);
        
        holder.ivStep4.setColorFilter(mutedColor);
        holder.tvStep4.setTextColor(mutedColor);

        // Paso 1: Recibido (siempre activo si existe el pedido)
        holder.ivStep1.setColorFilter(primaryColor);
        holder.tvStep1.setTextColor(primaryColor);

        if (estado.equals("preparando") || estado.equals("en_camino") || estado.equals("entregado")) {
            holder.arrow1.setColorFilter(primaryColor);
            holder.ivStep2.setColorFilter(primaryColor);
            holder.tvStep2.setTextColor(primaryColor);
        }
        if (estado.equals("en_camino") || estado.equals("entregado")) {
            holder.arrow2.setColorFilter(primaryColor);
            holder.ivStep3.setColorFilter(primaryColor);
            holder.tvStep3.setTextColor(primaryColor);
        }
        if (estado.equals("entregado")) {
            holder.arrow3.setColorFilter(primaryColor);
            holder.ivStep4.setColorFilter(primaryColor);
            holder.tvStep4.setTextColor(primaryColor);
        }
    }

    private void animateHighlight(com.google.android.material.card.MaterialCardView card) {
        card.post(() -> {
            SleekTraceDrawable trace = new SleekTraceDrawable(card);
            card.setForeground(trace);

            // 1. Animación de la línea de luz (2 vueltas para asegurar que se vea la completación)
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 2f);
            animator.setDuration(2000); 
            animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                trace.setProgress((float) animation.getAnimatedValue());
            });

            // 2. Animación de Sacudida (Shaking)
            ValueAnimator shakeAnim = ValueAnimator.ofFloat(0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f);
            shakeAnim.setDuration(500);
            shakeAnim.addUpdateListener(animation -> {
                card.setTranslationX((float) animation.getAnimatedValue());
            });

            animator.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    card.setForeground(null);
                    // Dejar el borde verde institucional permanente
                    card.setStrokeColor(android.graphics.Color.parseColor("#317832"));
                    card.setStrokeWidth(4);
                    card.setCardElevation(10f);
                }
            });

            animator.start();
            shakeAnim.start();
        });
    }

    private static class SleekTraceDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final PathMeasure pathMeasure = new PathMeasure();
        private float progress = 0f;
        private final int color;

        public SleekTraceDrawable(com.google.android.material.card.MaterialCardView card) {
            // Usamos el verde institucional para el trazo
            this.color = android.graphics.Color.parseColor("#317832");
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            
            RectF rect = new RectF(5, 5, card.getWidth() - 5, card.getHeight() - 5);
            path.addRoundRect(rect, card.getRadius(), card.getRadius(), Path.Direction.CW);
            pathMeasure.setPath(path, true); // true para que sea un loop cerrado
        }

        public void setProgress(float progress) {
            this.progress = progress;
            invalidateSelf();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            float length = pathMeasure.getLength();
            if (length <= 0) return;

            float currentProgress = progress % 1.0f;
            float head = length * currentProgress;
            float tail = head - (length * 0.4f); // La línea mide el 40% del borde
            
            Path segment = new Path();
            if (tail < 0) {
                // Dibujar el trozo que viene desde el final (vuelta atrás)
                pathMeasure.getSegment(tail + length, length, segment, true);
                // Dibujar el trozo inicial
                pathMeasure.getSegment(0, head, segment, true);
            } else {
                pathMeasure.getSegment(tail, head, segment, true);
            }
            
            paint.setColor(color);
            canvas.drawPath(segment, paint);
        }

        @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); }
        @Override public void setColorFilter(@androidx.annotation.Nullable android.graphics.ColorFilter colorFilter) { paint.setColorFilter(colorFilter); }
        @Override public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.card.MaterialCardView cardView;
        TextView tvOrderNumber, tvOrderDate, tvOrderTotal;
        View btnRepeat, llTimeline, llExpandable, btnViewDetails;
        android.widget.ImageView ivStep1, ivStep2, ivStep3, ivStep4, ivChevron;
        android.widget.ImageView arrow1, arrow2, arrow3;
        TextView tvStep1, tvStep2, tvStep3, tvStep4;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (com.google.android.material.card.MaterialCardView) itemView;
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            btnRepeat = itemView.findViewById(R.id.btnRepeatOrder);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            llTimeline = itemView.findViewById(R.id.llTimeline);
            llExpandable = itemView.findViewById(R.id.llExpandableContent);
            ivChevron = itemView.findViewById(R.id.ivChevron);
            
            ivStep1 = itemView.findViewById(R.id.ivStep1);
            ivStep2 = itemView.findViewById(R.id.ivStep2);
            ivStep3 = itemView.findViewById(R.id.ivStep3);
            ivStep4 = itemView.findViewById(R.id.ivStep4);
            
            arrow1 = itemView.findViewById(R.id.arrow1);
            arrow2 = itemView.findViewById(R.id.arrow2);
            arrow3 = itemView.findViewById(R.id.arrow3);
            
            tvStep1 = itemView.findViewById(R.id.tvStep1);
            tvStep2 = itemView.findViewById(R.id.tvStep2);
            tvStep3 = itemView.findViewById(R.id.tvStep3);
            tvStep4 = itemView.findViewById(R.id.tvStep4);
        }
    }
}
