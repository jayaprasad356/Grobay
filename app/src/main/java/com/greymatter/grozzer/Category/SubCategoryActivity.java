package com.greymatter.grozzer.Category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.CategoryAdapter;
import com.greymatter.grozzer.CartActivity;
import com.greymatter.grozzer.Model.Category;
import com.greymatter.grozzer.R;

import java.util.ArrayList;

public class SubCategoryActivity extends AppCompatActivity {

    RecyclerView rv;
    ArrayList<Category> categoryArrayListList = new ArrayList<>();
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        String strname = getIntent().getStringExtra("name");
        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");
        setupCart(user);

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        adapter = new CategoryAdapter(this,categoryArrayListList, strname);
        rv.setAdapter(adapter);

        getSub(strname);
    }
    private void getSub(String strname) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Category");
        dref.child(strname).child("Sub").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayListList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Category model = ds.getValue(Category.class);
                    categoryArrayListList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupCart(String user) {
        RelativeLayout cart = findViewById(R.id.cartBtn1);
        TextView cart_count = findViewById(R.id.cart_count);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });

        FirebaseDatabase.getInstance().getReference("Cart").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    cart_count.setText(String.valueOf(snapshot.getChildrenCount()));
                    cart_count.setVisibility(View.VISIBLE);
                }else {
                    cart_count.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void openCart(View view) {
        startActivity(new Intent(getApplicationContext(),CartActivity.class));
    }
}