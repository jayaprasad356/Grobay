package com.greymatter.grozzer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Product;
import com.greymatter.grozzer.ProductDetailsActivity;
import com.greymatter.grozzer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    Context context;
    ArrayList<Product> itemslist;

    public DiscountAdapter(Context context, ArrayList<Product> itemslist) {
        this.context = context;
        this.itemslist = itemslist;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.discounted_row_items, parent, false);
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

        
        Glide.with(context).load(model.getPimage()).placeholder(R.drawable.loading)
                .into(holder.pimage);

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

    }

    @Override
    public int getItemCount() {
        return itemslist.size();
    }

    public void filterList(ArrayList<Product> filterdItems) {
        this.itemslist = filterdItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pimage;
        TextView pname,pqty,pprice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pimage = itemView.findViewById(R.id.pImage);
            pname = itemView.findViewById(R.id.productName);
            pqty = itemView.findViewById(R.id.qty);
            pprice = itemView.findViewById(R.id.prodPrice);
        }
    }
}
