package com.greymatter.grozzer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.greymatter.grozzer.CartActivity;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Cart;
import com.greymatter.grozzer.Model.Product;
import com.greymatter.grozzer.ProductDetailsActivity;
import com.greymatter.grozzer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    ArrayList<Cart> itemslist;
    String type;

    public CartAdapter(Context context, ArrayList<Cart> itemslist, String check) {
        this.context = context;
        this.itemslist = itemslist;
        this.type = check;
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

        if (type.equals("check")){
            holder.cancel.setVisibility(View.INVISIBLE);
        }else {
            holder.cancel.setVisibility(View.VISIBLE);
        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = context.getSharedPreferences("pref",MODE_PRIVATE);
                String user = sp.getString("user","null");
                //remove item from cart
                FirebaseDatabase.getInstance().getReference("Cart").child(user).child(model.getPid()).removeValue();
                context.startActivity(new Intent(context, CartActivity.class));
                ((Activity)context).finish();
            }
        });

    }

    private void getProductDetails(Cart model, ViewHolder holder) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Products");
        dref.child(model.getPid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Product product = snapshot.getValue(Product.class);

                    //Picasso.get().load(product.getPimage()).into(holder.pimage);
                    Glide.with(context).load(product.getPimage()).into(holder.pimage);
                    holder.pname.setText(product.getPname());
                    holder.pqty.setText(product.getPqty());

                    //calculate total
                    int needed = Integer.parseInt(model.getNeeded());
                    int price = Integer.parseInt(product.getPprice());
                    int total = needed * price;
                    if (total != Integer.parseInt(model.getTotal()) );{
                        storeTo(model.getPid(),String.valueOf(total));
                    }
                    holder.pprice.setText(model.getNeeded() + " x "+product.getPprice() +" = " + constants.Rs+total);


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, ProductDetailsActivity.class);
                            i.putExtra(constants.id,product.getPid());
                            i.putExtra(constants.name,product.getPname());
                            i.putExtra(constants.image,product.getPimage());
                            i.putExtra(constants.desc,product.getPdesc());
                            i.putExtra(constants.qty,product.getPqty());
                            i.putExtra(constants.price,product.getPprice());
                            context.startActivity(i);
                        }
                    });

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

    private void storeTo(String pid,String total) {
        SharedPreferences sp = context.getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Cart");
        HashMap<String,Object> map = new HashMap<>();
        map.put("total",total);
        dref.child(user).child(pid).updateChildren(map);
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
