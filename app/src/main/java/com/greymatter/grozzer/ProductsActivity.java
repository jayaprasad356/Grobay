package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.ProductAdapter;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Product;

import java.util.ArrayList;
import java.util.Collections;

public class ProductsActivity extends AppCompatActivity {
    EditText search;
    RecyclerView rv;
    ArrayList<Product> productsList = new ArrayList<>();
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");
        setupCart(user);

        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        productAdapter = new ProductAdapter(this,productsList);
        rv.setAdapter(productAdapter);

        getProducts();

    }

    private void filter(String key) {
        ArrayList<Product> filterdItems = new ArrayList<>();
        for (Product model : productsList){
            if (model.getPname().toLowerCase().contains(key.toLowerCase())){
                filterdItems.add(model);
            }

        }

        productAdapter.filterList(filterdItems);
    }

    private void setupCart(String user) {
        RelativeLayout cart = findViewById(R.id.cartBtn1);
        TextView cart_count = findViewById(R.id.cart_count);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CartActivity.class));
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

    private void getProducts() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Products");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Product model = ds.getValue(Product.class);
                    productsList.add(model);
                }
                Collections.reverse(productsList);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}