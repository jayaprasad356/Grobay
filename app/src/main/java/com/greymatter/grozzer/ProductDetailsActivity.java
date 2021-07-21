package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.DiscountAdapter;
import com.greymatter.grozzer.Adapter.SliderAdapter;
import com.greymatter.grozzer.Auth.LoginActivity;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Product;
import com.greymatter.grozzer.Model.SliderData;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    String pid,pname,pimage,pdesc,pqty,pprice,poffer="",pmrp="";
    ImageView product_image,plus,minus;
    TextView name,desc,qty,price,needed,offer,mrp;
    Button addtocart,buynow;
    int totalprice;
    LinearLayout offerContainer;
    ArrayList<Product> productsList = new ArrayList<>();
    DiscountAdapter discountAdapter;
    RecyclerView discountedRecycler;
    private String category;
    private SliderView sliderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        pid = getIntent().getStringExtra(constants.id);
        pname = getIntent().getStringExtra(constants.name);
        pdesc = getIntent().getStringExtra(constants.desc);
        pimage = getIntent().getStringExtra(constants.image);
        pqty = getIntent().getStringExtra(constants.qty);
        pprice = getIntent().getStringExtra(constants.price);


        setId();
        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");

        setupCart(user);
        setDatas();
        getProductDetails(pid);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(needed.getText().toString());
                int newQty  = current +1;
                needed.setText(String.valueOf(newQty));
                calculatePrice();
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(needed.getText().toString());
                if (current > 1){
                    int newQty  = current -1;
                    needed.setText(String.valueOf(newQty));
                    calculatePrice();
                }

            }
        });

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.equals("guest")){
                    Toast.makeText(ProductDetailsActivity.this, "Need to Login First", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }else {
                    storetocart(user,needed.getText().toString());
                }
            }
        });

        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.equals("guest")){
                    Toast.makeText(ProductDetailsActivity.this, "Need to Login First", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }else {
                    storetocart(user,needed.getText().toString());
                }
            }
        });

        discountedRecycler = findViewById(R.id.discountedRecycler);
        discountedRecycler.setHasFixedSize(true);
        discountAdapter = new DiscountAdapter(this,productsList);
        discountedRecycler.setAdapter(discountAdapter);
        getRelatedProducts();

    }

    private void slider() {
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

        FirebaseDatabase.getInstance().getReference("Products").child(pid).child("Slider").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderDataArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    SliderData model = ds.getValue(SliderData.class);
                    sliderDataArrayList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
    }

    private void getRelatedProducts() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Products");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Product model = ds.getValue(Product.class);
                    if (model.getPcategory().equals(category) && !model.getPid().equals(pid)){
                        productsList.add(model);
                    }
                }
                discountAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDatas() {
        name.setText(pname);
        desc.setText(pdesc);
        qty.setText(pqty);
        price.setText(constants.Rs + pprice);
        Picasso.get().load(pimage).into(product_image);

        if (poffer.equals("")){
            offerContainer.setVisibility(View.GONE);
        }else {
            offerContainer.setVisibility(View.VISIBLE);
            offer.setText(poffer + "% OFF");
            mrp.setText("Product MRP: "+constants.Rs+pmrp);
            mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void getProductDetails(String pid) {
        FirebaseDatabase.getInstance().getReference("Products").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product model = snapshot.getValue(Product.class);

                pname = model.getPname();
                pdesc = model.getPdesc();
                pqty = model.getPqty();
                pprice = model.getPprice();
                pimage = model.getPimage();
                poffer = model.getPoffer();
                pmrp = model.getPmrp();
                category = model.getPcategory();

                setDatas();

                if(snapshot.child("Slider").exists()){
                    product_image.setVisibility(View.GONE);
                    sliderView.setVisibility(View.VISIBLE);
                    slider();
                }else {
                    product_image.setVisibility(View.VISIBLE);
                    sliderView.setVisibility(View.GONE);
                }
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

    private void storetocart(String user,String needed) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Cart");
        HashMap<String ,Object> map = new HashMap<>();
        map.put("pid",pid);
        map.put("needed",needed);
        map.put("total",String.valueOf(totalprice));
        dref.child(user).child(pid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProductDetailsActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),CartActivity.class));
                }else {
                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculatePrice() {
        int qty = Integer.parseInt(needed.getText().toString());
        totalprice = qty * Integer.parseInt(pprice);
        price.setText(constants.Rs + totalprice);
    }

    private void setId() {
        product_image = findViewById(R.id.big_image);
        name = findViewById(R.id.productName);
        desc = findViewById(R.id.prodDesc);
        price = findViewById(R.id.prodPrice);
        qty = findViewById(R.id.qty);
        needed = findViewById(R.id.needed);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        addtocart = findViewById(R.id.textButton);
        buynow = findViewById(R.id.button);
        offer = findViewById(R.id.tvOffer);
        mrp = findViewById(R.id.tvMrp);
        offerContainer = findViewById(R.id.offerContainer);
        sliderView = findViewById(R.id.slider);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}