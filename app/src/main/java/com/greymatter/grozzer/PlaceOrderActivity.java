package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Adapter.CartAdapter;
import com.greymatter.grozzer.Config.constants;
import com.greymatter.grozzer.Model.Address;
import com.greymatter.grozzer.Model.Cart;
import com.greymatter.grozzer.Model.Extra;
import com.greymatter.grozzer.Model.User;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rtchagas.pingplacepicker.PingPlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PlaceOrderActivity extends AppCompatActivity implements PaymentResultListener {
    String totalamount,producttotal,deliverycharge;
    TextView tvTotal;
    EditText name,number,address,pincode;
    Chip cod,online;
    Button placeorder;
    ProgressBar progressBar;
    String strname,strnumber,straddress,strpincode;
    String user,charge="null";
    String strmail;
    boolean isChecked=false,isAvailable=false,added=false;
    ArrayList<Cart> cartArrayList = new ArrayList<>();
    CartAdapter adapter;
    private final static int PLACE_PICKER_REQUEST = 999;
    Button setlocation;
    Boolean locate = false;
    double latitude,longitude;

    private static final String TAG = MainActivity.class.getSimpleName();
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                //updateLocationUI();
            }
        };


        totalamount = getIntent().getStringExtra("total");
        producttotal= totalamount;
        setlocation = findViewById(R.id.setlocation);

        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        user = sp.getString("user","null");

        setId();
        getUserDetails(user);
        getUserMail(user);

        tvTotal.setText("Order Total " + constants.Rs + totalamount);

        findViewById(R.id.checkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeorder.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                checkpincode(pincode.getText().toString().trim());
            }
        });
        setlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionLocation();


            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeorder.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                strname = name.getText().toString().trim();
                strnumber = number.getText().toString().trim();
                straddress = address.getText().toString().trim();
                strpincode = pincode.getText().toString().trim();
                String strmethod;
                if (cod.isChecked()){
                    strmethod = constants.cod;
                }else {
                    strmethod = constants.online;
                }

                if (isValidField(strname,strnumber,straddress,strpincode)){
                    if (!isChecked){
                        placeorder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(PlaceOrderActivity.this, "Please! Enter Pincode and Check for Availability", Toast.LENGTH_SHORT).show();
                    }else if (isChecked&&isAvailable){
                        showAlertDialogButtonClicked(strmethod);
                        //placeOrder();
                    }
                }else {
                    placeorder.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void getUserMail(String user) {
        FirebaseDatabase.getInstance().getReference("Users").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User model  = snapshot.getValue(User.class);
                strmail = model.getMail();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(getApplicationContext(), data);

                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    String PlaceLatLng = String.valueOf(latitude)+" , "+String.valueOf(longitude);
                    //Toast.makeText(this, PlaceLatLng, Toast.LENGTH_SHORT).show();
                    locate = true;
                    setlocation.setText("Location selected");

            }
        }*/
        if ((requestCode == PLACE_PICKER_REQUEST) && (resultCode == RESULT_OK)) {
            Place place = PingPlacePicker.getPlace(data);
            if (place != null) {
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                String PlaceLatLng = String.valueOf(latitude)+" , "+String.valueOf(longitude);
                //Toast.makeText(this, PlaceLatLng, Toast.LENGTH_SHORT).show();
                locate = true;
                setlocation.setText("Location selected");
                //Toast.makeText(this, "You selected the place: " + place.getLatLng(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void browselocation() {
        LatLng bottomLeft = new LatLng(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude());
        LatLng topRight = new LatLng(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude());

        LatLngBounds bounds = new LatLngBounds(bottomLeft ,topRight);

        PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
        builder.setAndroidApiKey(String.valueOf(R.string.Android_key))
                .setMapsApiKey(String.valueOf(R.string.Map_API_key));

        // If you want to set a initial location rather then the current device location.
        // NOTE: enable_nearby_search MUST be true.
        // builder.setLatLng(new LatLng(37.4219999, -122.0862462))

        try {
            builder.setLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLatitude()));
            startActivityForResult(builder.build(PlaceOrderActivity.this), PLACE_PICKER_REQUEST);
        }
        catch (Exception ex) {
            // Google Play services is not available...
        }



        /*PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            builder.setLatLngBounds(bounds);
            startActivityForResult(builder.build(PlaceOrderActivity.this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        }*/


    }

    private void getUserDetails(String user) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
        dref.child(user).child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Address addressmodel = snapshot.getValue(Address.class);

                    name.setText(addressmodel.getName());
                    number.setText(addressmodel.getContact());
                    address.setText(addressmodel.getAddress());
                    pincode.setText(addressmodel.getPincode());
                    checkpincode(addressmodel.getPincode());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkpincode(String strpincode) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Pincode");
        dref.child(strpincode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    isChecked=true;
                    isAvailable=true;

                    Extra model = snapshot.getValue(Extra.class);
                    charge = model.getDeliverycharge();

                    if (!charge.equals("0")){
                        if (!added){
                            int total = Integer.parseInt(totalamount) + Integer.parseInt(charge);
                            tvTotal.setText("Order Total " + constants.Rs + total);
                            totalamount = String.valueOf(total);
                            deliverycharge = charge;
                            added = true;
                        }
                        Toast.makeText(PlaceOrderActivity.this, "Delivery Charge Rs:- "+ charge, Toast.LENGTH_SHORT).show();
                    }else {
                        added = true;
                        deliverycharge = "0";
                        Toast.makeText(PlaceOrderActivity.this, "Free Delivery for Your area", Toast.LENGTH_SHORT).show();
                    }

                    placeorder.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else {
                    isChecked=true;
                    isAvailable=false;
                    Toast.makeText(PlaceOrderActivity.this, "Sorry! we are not available for your area", Toast.LENGTH_SHORT).show();
                    placeorder.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void placeOrder() {
        if (locate){
            if (cod.isChecked()){
                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);
                builder.setTitle("Are you sure for?");
                builder.setMessage("Cash on Delivery");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkoutOrder(constants.cod);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        placeorder.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

                builder.create().show();

            }
            else {
                razorpay();
            }

        }
        else {
            Toast.makeText(this, "select location", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkoutOrder(String method) {

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Orders");
        String oid = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> map = new HashMap<>();
        map.put("oid",oid);
        map.put("user",user);
        map.put("name",strname);
        map.put("number",strnumber);
        map.put("address",straddress);
        map.put("pincode",strpincode);
        map.put("ordertotal",totalamount);
        map.put("method",method);
        map.put("status", constants.pending);
        map.put("latitude", String.valueOf(latitude));
        map.put("longtitude", String.valueOf(longitude));
        map.put("deliveryboy", "null");
        map.put("delivery_status", "null");

        dref.child(oid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //move items
                    FirebaseDatabase.getInstance().getReference("Cart").child(user)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    FirebaseDatabase.getInstance().getReference("Orders").child(oid).child("products").setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                FirebaseDatabase.getInstance().getReference("Cart").child(user).removeValue();
                                                placeorder.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                placed();
                                            }else {
                                                Toast.makeText(PlaceOrderActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                placeorder.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }else {
                    Toast.makeText(PlaceOrderActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    placeorder.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void placed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.successfully_placed, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        },2000);
        /* startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();*/
    }

    private void razorpay() {
        int amount = Math.round(Float.parseFloat(totalamount) * 100);
        String mail = strmail;
        String number = strnumber;

        Checkout checkout = new Checkout();
        checkout.setKeyID(getResources().getString(R.string.Razorpay_API_key));
        JSONObject object = new JSONObject();
        try {
            object.put("name", getResources().getString(R.string.app_name));
            object.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            object.put("description", "Pay for your Order");
            object.put("theme.color","#ff0036");
            object.put("currency", "INR");
            object.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", mail);
            preFill.put("contact", number);
            object.put("prefill", preFill);

            checkout.open(PlaceOrderActivity.this, object);
        } catch (JSONException e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private boolean isValidField(String strname,String strnumber, String straddress, String strpincode) {
        if (strname.isEmpty() || strnumber.isEmpty() || straddress.isEmpty() || strpincode.isEmpty()){
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private void setId() {
        tvTotal = findViewById(R.id.tvTotal);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        cod = findViewById(R.id.cod);
        online = findViewById(R.id.op);
        progressBar = findViewById(R.id.progressBar);
        placeorder = findViewById(R.id.placeorder);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
        checkoutOrder(constants.online);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show();
    }

    public void showAlertDialogButtonClicked(String strmethod) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.checkout_layout, null);
        builder.setView(customLayout);

        TextView tvAddress = customLayout.findViewById(R.id.tvAddress);
        TextView method = customLayout.findViewById(R.id.method);
        TextView tvTotal = customLayout.findViewById(R.id.tvTotal);
        RecyclerView rv = customLayout.findViewById(R.id.rv);

        StringBuilder sb = new StringBuilder();
        sb.append(strname + "\n");
        sb.append(strnumber + "\n");
        sb.append(straddress + "\n");
        sb.append(strpincode);
        tvAddress.setText(sb.toString());

        method.setText("Payment Method:- " + strmethod);
        tvTotal.setText("Product Total "+ constants.Rs + producttotal+"\n"+
                "Delivery Charge "+ constants.Rs + deliverycharge+"\n"+
                "Order Total " + constants.Rs + totalamount);

        rv.setHasFixedSize(true);
        adapter = new CartAdapter(this,cartArrayList,"check");
        rv.setAdapter(adapter);
        getCartItems(user);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                placeOrder();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                placeorder.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void PermissionLocation()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());


                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(PlaceOrderActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                //Toast.makeText(LocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        //updateLocationUI();
                    }
                });
    }

    private void updateLocationUI()
    {
        if (mCurrentLocation != null) {
            browselocation();
            //Toast.makeText(this, String.valueOf(mCurrentLocation.getLatitude()) + String.valueOf(mCurrentLocation.getLongitude()), Toast.LENGTH_SHORT).show();




        }

    }


}