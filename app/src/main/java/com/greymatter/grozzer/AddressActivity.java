package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Auth.LoginActivity;
import com.greymatter.grozzer.Model.Address;
import com.greymatter.grozzer.Model.User;

import java.util.HashMap;

public class AddressActivity extends AppCompatActivity {

    private TextView tvAddress;
    EditText name,number,address,pincode;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        String user = sp.getString("user","null");

        setId();

        if (!user.equals("guest")){
            getUserDetails(user);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!user.equals("guest")){
                    String strname = name.getText().toString().trim();
                    String strnumber = number.getText().toString().trim();
                    String straddress = address.getText().toString().trim();
                    String strpincode = pincode.getText().toString().trim();

                    if (isValidField(strname,strnumber,straddress,strpincode)){
                        storeData(user,strname,strnumber,straddress,strpincode);
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Need to Login First", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }


            }
        });

    }

    private void storeData(String user, String strname, String strnumber, String straddress, String strpincode) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("name",strname);
        map.put("contact",strnumber);
        map.put("address",straddress);
        map.put("pincode",strpincode);

        FirebaseDatabase.getInstance().getReference("Users").child(user).child("address").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddressActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddressActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        tvAddress = findViewById(R.id.tvAddress);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        update = findViewById(R.id.update);
    }

    private void getUserDetails(String user) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
        dref.child(user).child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Address addressmodel = snapshot.getValue(Address.class);
                    StringBuilder sb = new StringBuilder();
                    sb.append(addressmodel.getName() + "\n");
                    sb.append(addressmodel.getContact() + "\n");
                    sb.append(addressmodel.getAddress() + "\n");
                    sb.append(addressmodel.getPincode());

                    tvAddress.setText(sb.toString());

                    name.setText(addressmodel.getName());
                    number.setText(addressmodel.getContact());
                    address.setText(addressmodel.getAddress());
                    pincode.setText(addressmodel.getPincode());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

}