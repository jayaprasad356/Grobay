package com.greymatter.grozzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.CategoryAdapter;
import com.greymatter.grozzer.Adapter.DiscountAdapter;
import com.greymatter.grozzer.Adapter.ProductAdapter;
import com.greymatter.grozzer.Adapter.SliderAdapter;
import com.greymatter.grozzer.Category.AllCategoryActivity;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Category;
import com.greymatter.grozzer.Model.Product;
import com.greymatter.grozzer.Model.SliderData;
import com.greymatter.grozzer.Model.User;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private long backpressedtime;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    ArrayList<Product> productsList = new ArrayList<>();
    DiscountAdapter discountAdapter;
    RecyclerView discountedRecycler;
    RecyclerView rv;
    ArrayList<Category> categoryArrayListList = new ArrayList<>();
    CategoryAdapter adapter;
    private ProgressBar progressbar;
    RelativeLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,  drawerLayout, toolbar ,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        View headerview = nav_view.getHeaderView(0);
        TextView username = (TextView) headerview.findViewById(R.id.user);
        TextView number = (TextView) headerview.findViewById(R.id.number);
        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");
        if (user.equals("guest")){
            username.setText("Guest");
            number.setText("");
        }else {
           getUserDetails(username,number,user);
        }

        setupCart(user);

        slider();

        findViewById(R.id.allProducts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AllCategoryActivity.class));
            }
        });
        findViewById(R.id.allcategoryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AllCategoryActivity.class));
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProductsActivity.class));
            }
        });

        discountedRecycler = findViewById(R.id.discountedRecycler);
        discountedRecycler.setHasFixedSize(true);
        discountAdapter = new DiscountAdapter(this,productsList);
        discountedRecycler.setAdapter(discountAdapter);
        getOffers();


        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        adapter = new CategoryAdapter(this,categoryArrayListList,"null");
        rv.setAdapter(adapter);
        getCategories();

    }

    private void getCategories() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Category");
        dref.addValueEventListener(new ValueEventListener() {
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

    private void getUserDetails(TextView username, TextView number, String user) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
        dref.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User model = snapshot.getValue(User.class);
                username.setText(model.getName());
                number.setText(model.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void slider() {
        SliderView sliderView = findViewById(R.id.slider);
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

        FirebaseDatabase.getInstance().getReference("Slider").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderDataArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    SliderData model = ds.getValue(SliderData.class);
                    sliderDataArrayList.add(model);
                }
                adapter.notifyDataSetChanged();
                progressbar = findViewById(R.id.progressBar3);
                content = findViewById(R.id.content);
                progressbar.setVisibility(View.INVISIBLE);
                content.setVisibility(View.VISIBLE);
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

    private void getOffers() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Products");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Product model = ds.getValue(Product.class);
                    if (model.getPtype().equals(constants.offer)){
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

    @Override
    public void onBackPressed() {
        if (backpressedtime + 2000 > System.currentTimeMillis()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();
        }
        backpressedtime = System.currentTimeMillis();
    }

    public void openCart(View view) {
        startActivity(new Intent(getApplicationContext(),CartActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId()) {
            case R.id.orders:
                startActivity(new Intent(getApplicationContext(),MyOrdersActivity.class));
                break;
            case R.id.address:
                startActivity(new Intent(getApplicationContext(),AddressActivity.class));
                break;
            case R.id.feedback:
                startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));
                break;
            case R.id.contact:
                startActivity(new Intent(getApplicationContext(),ContactActivity.class));
                break;
            case R.id.aboutus:
                startActivity(new Intent(getApplicationContext(),AboutUsActivity.class));
                break;
            case  R.id.exit:
                logout();
                break;
            default:
                Toast.makeText(this, "coming soon...", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void logout() {
        if (!(FirebaseAuth.getInstance().getCurrentUser() == null)){
            FirebaseAuth.getInstance().signOut();
        }

        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user","null");
        editor.apply();

        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
        finish();
    }

}