
package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.CartAdapter;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Cart;

import java.util.ArrayList;
import java.util.Collections;

public class CartActivity extends AppCompatActivity {
    ImageView emptycart;
    TextView total;
    Button proceed;
    RecyclerView rv;
    ArrayList<Cart> cartArrayList = new ArrayList<>();
    CartAdapter adapter;
    int totalamount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");

        setId();

        rv.setHasFixedSize(true);
        adapter = new CartAdapter(this,cartArrayList, "cart");
        rv.setAdapter(adapter);

        if (!user.equals("guest")){
            getCartItems(user);
        }

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlaceOrderActivity.class);
                i.putExtra("total",String.valueOf(totalamount));
                startActivity(i);
            }
        });
    }

    private void getCartItems(String user) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Cart");
        dref.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Cart model = ds.getValue(Cart.class);
                    cartArrayList.add(model);
                }
                Collections.reverse(cartArrayList);
                adapter.notifyDataSetChanged();
                updateCart(cartArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCart(ArrayList<Cart> cartArrayList) {
        if (cartArrayList.isEmpty()){
            emptycart.setVisibility(View.VISIBLE);
        }else {
            emptycart.setVisibility(View.INVISIBLE);
            proceed.setVisibility(View.VISIBLE);
            totalamount=0;
            for (Cart model : cartArrayList){
                totalamount = totalamount + Integer.parseInt(model.getTotal());
            }
            total.setText(constants.Rs + totalamount + "\n Total");
        }
    }

    private void setId() {
        emptycart = findViewById(R.id.emptycart);
        total = findViewById(R.id.total);
        proceed = findViewById(R.id.proceed);
        rv = findViewById(R.id.rv);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}