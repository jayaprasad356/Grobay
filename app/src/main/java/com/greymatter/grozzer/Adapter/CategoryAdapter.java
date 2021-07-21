package com.greymatter.grozzer.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Category.SubCategoryActivity;
import com.greymatter.grozzer.CategoryActivity;
import com.greymatter.grozzer.Model.Category;
import com.greymatter.grozzer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    ArrayList<Category> itemslist;
    String parent;

    public CategoryAdapter(Context context, ArrayList<Category> itemslist, String parent) {
        this.context = context;
        this.itemslist = itemslist;
        this.parent = parent;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category model = itemslist.get(position);

       holder.name.setText(model.getName());
     //  Picasso.get().load(model.getUrl()).into(holder.image);
        Glide.with(context).load(model.getUrl()).placeholder(R.drawable.loading).into(holder.image);

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (parent.equals("null")){
                   //from all
                   FirebaseDatabase.getInstance().getReference("Category").child(model.getName()).child("Sub").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if (snapshot.exists()){
                               openSub(model.getName());
                           }else {
                              openProduct(model.getName());
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
               }else {
                   //from sub
                   openProduct(model.getName());
               }
           }
       });


    }

    private void openSub(String name) {
        Intent i = new Intent(context, SubCategoryActivity.class);
        i.putExtra("name",name);
        context.startActivity(i);
    }

    private void openProduct(String name) {
        Intent i = new Intent(context, CategoryActivity.class);
        i.putExtra("category",name);
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return itemslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }
    }
}
