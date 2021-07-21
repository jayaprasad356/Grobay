package com.greymatter.grozzer.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greymatter.grozzer.MainActivity;
import com.greymatter.grozzer.OtpActivity;
import com.greymatter.grozzer.R;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText Password,ConfirmPassword;
    Button Confirm;
    String strphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        strphone = getIntent().getStringExtra("phone");
        Password = findViewById(R.id.password);
        ConfirmPassword = findViewById(R.id.confirmpassword);
        Confirm = findViewById(R.id.confirm);
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Password.getText().toString().isEmpty() || ConfirmPassword.getText().toString().isEmpty()){
                    Toast.makeText(ChangePasswordActivity.this, "Fill Password", Toast.LENGTH_SHORT).show();

                }
                else if (!Password.getText().toString().trim().equals(ConfirmPassword.getText().toString().trim())){
                    Toast.makeText(ChangePasswordActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("phone",strphone);
                    map.put("password",Password.getText().toString().trim());
                    DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
                    dref.child(strphone).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ChangePasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                loginAs();

                            }else {
                                Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            }
        });
    }
    private void loginAs() {
        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user",strphone);
        editor.apply();
        startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
        finish();
    }
}