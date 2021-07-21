package com.greymatter.grozzer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Order;
import com.greymatter.grozzer.OrderDetailsActivity;
import com.greymatter.grozzer.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    Context context;
    ArrayList<Order> itemslist;

    public OrderAdapter(Context context, ArrayList<Order> itemslist) {
        this.context = context;
        this.itemslist = itemslist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order model = itemslist.get(position);

        holder.orderId.setText(constants.orderId + model.getOid());
        holder.total.setText(constants.orderTotal + constants.Rs +model.getOrdertotal());
        holder.status.setText(constants.orderStatus + model.getStatus());

        if (model.getStatus().equals(constants.cancelled)){
            holder.status.setTextColor(context.getResources().getColor(R.color.cancelled));
        }else if (model.getStatus().equals(constants.delivered)){
            holder.status.setTextColor(context.getResources().getColor(R.color.delivered));
        }else if (model.getStatus().equals(constants.outfordelivery)){
            holder.status.setTextColor(context.getResources().getColor(R.color.out));
        }else {
            holder.status.setTextColor(context.getResources().getColor(R.color.pending));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OrderDetailsActivity.class);
                i.putExtra("oid",model.getOid());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId,total,status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            total = itemView.findViewById(R.id.total);
            status = itemView.findViewById(R.id.status);
        }
    }
}
