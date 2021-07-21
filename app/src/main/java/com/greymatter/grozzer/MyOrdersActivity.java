package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.CartAdapter;
import com.greymatter.grozzer.Adapter.OrderAdapter;
import com.greymatter.grozzer.Model.Order;

import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity {
    ImageView emptycart;
    RecyclerView rv;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");

        setId();

        rv.setHasFixedSize(true);
        adapter = new OrderAdapter(this,orderArrayList);
        rv.setAdapter(adapter);

        if (!user.equals("guest")){
            getAllOrders(user);
        }

    }

    private void getAllOrders(String user) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
        dref.child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Order order  = ds.getValue(Order.class);
                    if (order.getUser().equals(user)){
                        orderArrayList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setId() {
        emptycart = findViewById(R.id.emptycart);
        rv = findViewById(R.id.rv);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

}