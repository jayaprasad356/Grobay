package com.greymatter.grozzer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greymatter.grozzer.Auth.LoginActivity;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Product;
import com.greymatter.grozzer.ProductDetailsActivity;
import com.greymatter.grozzer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context context;
    ArrayList<Product> itemslist;
    String user;


    public ProductAdapter(Context context, ArrayList<Product> itemslist) {
        this.context = context;
        this.itemslist = itemslist;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product model = itemslist.get(position);

        String id = model.getPid();
        String name = model.getPname();
        String desc = model.getPdesc();
        String image = model.getPimage();
        String qty = model.getPqty();
        String price = model.getPprice();
        String poffer = model.getPoffer();
        String pmrp = model.getPmrp();

       // Picasso.get().load(model.getPimage()).into(holder.pimage);
        Glide.with(context).load(model.getPimage()).placeholder(R.drawable.loading).into(holder.pimage);

        holder.pname.setText(name);
        holder.pqty.setText(qty);
        holder.pprice.setText(constants.Rs+price);

        if (poffer.equals("")){
            holder.offerContainer.setVisibility(View.GONE);
        }else {
            holder.offerContainer.setVisibility(View.VISIBLE);
            holder.offer.setText(poffer + "% OFF");
            holder.mrp.setText("MRP: "+ pmrp);
            holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProductDetailsActivity.class);
                i.putExtra(constants.id,id);
                i.putExtra(constants.name,name);
                i.putExtra(constants.image,image);
                i.putExtra(constants.desc,desc);
                i.putExtra(constants.qty,qty);
                i.putExtra(constants.price,price);
                context.startActivity(i);
            }
        });

        SharedPreferences sp = context.getSharedPreferences("pref",MODE_PRIVATE);
        user = sp.getString("user","null");


        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.addBtn.getTag().equals("close")){
                    holder.addBtn.setTag("open");
                    holder.cartContainer.setVisibility(View.VISIBLE);
                }else {
                    holder.addBtn.setTag("close");
                    holder.cartContainer.setVisibility(View.GONE);
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(holder.needed.getText().toString());
                int newQty  = current +1;
                holder.needed.setText(String.valueOf(newQty));
                calculatePrice(holder,price);
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(holder.needed.getText().toString());
                if (current > 1){
                    int newQty  = current -1;
                    holder.needed.setText(String.valueOf(newQty));
                    calculatePrice(holder,price);
                }

            }
        });

        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.equals("guest")){
                    Toast.makeText(context, "Need to Login First", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, LoginActivity.class));
                    ((Activity)context).finish();
                }else {
                    int qty = Integer.parseInt(holder.needed.getText().toString());
                    int totalprice = qty * Integer.parseInt(price);
                    storetocart(user,holder.needed.getText().toString(),id,totalprice);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemslist.size();
    }

    public void filterList(ArrayList<Product> filterdItems) {
        this.itemslist = filterdItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pimage,plus,minus;
        TextView pname,pqty,pprice,offer,mrp,needed;
        LinearLayout offerContainer;
        RelativeLayout cartContainer;
        Button addtocart,addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pimage = itemView.findViewById(R.id.pImage);
            pname = itemView.findViewById(R.id.productName);
            pqty = itemView.findViewById(R.id.qty);
            pprice = itemView.findViewById(R.id.prodPrice);
            offer = itemView.findViewById(R.id.tvOffer);
            mrp = itemView.findViewById(R.id.tvMrp);
            offerContainer = itemView.findViewById(R.id.offerContainer);
            addBtn = itemView.findViewById(R.id.addBtn);
            cartContainer = itemView.findViewById(R.id.cartContainer);
            needed = itemView.findViewById(R.id.needed);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            addtocart = itemView.findViewById(R.id.textButton);
        }
    }

    private void calculatePrice(ViewHolder holder, String price) {
        int qty = Integer.parseInt(holder.needed.getText().toString());
        int totalprice = qty * Integer.parseInt(price);
        holder.pprice.setText(constants.Rs + totalprice);
    }

    private void storetocart(String user,String needed,String pid,int totalprice) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Cart");
        HashMap<String ,Object> map = new HashMap<>();
        map.put("pid",pid);
        map.put("needed",needed);
        map.put("total",String.valueOf(totalprice));
        dref.child(user).child(pid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
