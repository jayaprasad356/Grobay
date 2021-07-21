package com.greymatter.grozzer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Cart;
import com.greymatter.grozzer.Model.Product;
import com.greymatter.grozzer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {
    Context context;
    ArrayList<Cart> itemslist;

    public OrderItemsAdapter(Context context, ArrayList<Cart> itemslist) {
        this.context = context;
        this.itemslist = itemslist;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.cart_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart model = itemslist.get(position);

        getProductDetails(model,holder);

        holder.cancel.setVisibility(View.INVISIBLE);

    }

    private void getProductDetails(Cart model, ViewHolder holder) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Products");
        dref.child(model.getPid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Product product = snapshot.getValue(Product.class);

                   // Picasso.get().load(product.getPimage()).into(holder.pimage);
                    Glide.with(context).load(product.getPimage()).into(holder.pimage);
                    holder.pname.setText(product.getPname());
                    holder.pqty.setText(product.getPqty() + " x " + model.getNeeded());
                    holder.pprice.setText(constants.Rs+model.getTotal());

                }else {
                    //Out of stock
                    holder.pname.setText("Out of Stock");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return itemslist.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pimage,cancel;
        TextView pname,pqty,pprice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pimage = itemView.findViewById(R.id.pImage);
            pname = itemView.findViewById(R.id.productName);
            pqty = itemView.findViewById(R.id.qty);
            pprice = itemView.findViewById(R.id.prodPrice);
            cancel = itemView.findViewById(R.id.cancel);
        }
    }
}
