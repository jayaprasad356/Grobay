package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.OrderItemsAdapter;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Cart;
import com.greymatter.grozzer.Model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {
    String oid;
    RecyclerView rv;
    ArrayList<Cart> cartArrayList = new ArrayList<>();
    OrderItemsAdapter adapter;
    TextView tvAddress,status,method,tvTotal;
    Button cancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        oid = getIntent().getStringExtra("oid");

        setId();

        rv.setHasFixedSize(true);
        adapter = new OrderItemsAdapter(this,cartArrayList);
        rv.setAdapter(adapter);

        getProducts(oid);
        getOrderDetails(oid);

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("To cancel the Order");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelOrder(oid);
                    }
                });
                builder.setNegativeButton("No", null );

                builder.create().show();
            }
        });
    }

    private void cancelOrder(String oid) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Orders");
        HashMap<String,Object> map = new HashMap<>();
        map.put("status", constants.cancelled);
        dref.child(oid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(OrderDetailsActivity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getOrderDetails(String oid) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Orders");
        dref.child(oid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order model = snapshot.getValue(Order.class);
                StringBuilder sb = new StringBuilder();
                sb.append(model.getName() + "\n");
                sb.append(model.getNumber() + "\n");
                sb.append(model.getAddress() + "\n");
                sb.append(model.getPincode());

                tvAddress.setText(sb.toString());

                status.setText("Order Status:- " + model.getStatus());
                switch (model.getStatus()) {
                    case constants.cancelled:
                        cancelOrder.setVisibility(View.GONE);
                        status.setTextColor(getResources().getColor(R.color.cancelled));
                        break;
                    case constants.delivered:
                        cancelOrder.setVisibility(View.GONE);
                        status.setTextColor(getResources().getColor(R.color.delivered));
                        break;
                    case constants.outfordelivery:
                        cancelOrder.setVisibility(View.GONE);
                        status.setTextColor(getResources().getColor(R.color.out));
                        break;
                    default:
                        status.setTextColor(getResources().getColor(R.color.pending));
                        cancelOrder.setVisibility(View.VISIBLE);
                        break;
                }

                method.setText("Payment Method:- " + model.getMethod());

                tvTotal.setText("Order Total " + constants.Rs + model.getOrdertotal());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProducts(String oid) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Orders");
        dref.child(oid).child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Cart model = ds.getValue(Cart.class);
                    cartArrayList.add(model);
                }
                Collections.reverse(cartArrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setId() {
        rv = findViewById(R.id.rv);
        tvAddress = findViewById(R.id.tvAddress);
        status = findViewById(R.id.status);
        method = findViewById(R.id.method);
        tvTotal = findViewById(R.id.tvTotal);
        cancelOrder = findViewById(R.id.cancelBtn);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

}