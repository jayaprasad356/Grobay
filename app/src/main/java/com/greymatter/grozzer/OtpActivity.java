package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greymatter.grozzer.Auth.ChangePasswordActivity;
import com.greymatter.grozzer.Auth.LoginActivity;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    private String verificationid;
    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    TextView OtpDisplayNo;
    String phonenumber;
    EditText et1,et2,et3,et4,et5,et6;
    String strname,strmail,strphone,strpass,activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        progressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        OtpDisplayNo = findViewById(R.id.displayotpno);
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);

        phonenumber = getIntent().getStringExtra("phonenumber");

        strname = getIntent().getStringExtra("name");
        strmail = getIntent().getStringExtra("mail");
        strphone = getIntent().getStringExtra("phone");
        strpass = getIntent().getStringExtra("password");
        activity = getIntent().getStringExtra("activity");

        OtpDisplayNo.setText("Enter OTP sent to "+phonenumber);
        sendVerificationCode(phonenumber);

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((et1.length() + et2.length() + et3.length() + et4.length() + et5.length() + et6.length()) == 6){
                    String et1_,et2_,et3_,et4_,et5_,et6_;

                    et1_ = et1.getText().toString().trim();
                    et2_ = et2.getText().toString().trim();
                    et3_ = et3.getText().toString().trim();
                    et4_ = et4.getText().toString().trim();
                    et5_ = et5.getText().toString().trim();
                    et6_ = et6.getText().toString().trim();
                    String code =  et1_ + et2_ + et3_ + et4_ + et5_+ et6_;
                    progressDialog.setTitle("OTP Detecting");
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    verifyCode(code);

                }
                else {
                    Toast.makeText(OtpActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }


            }
        });
        Edittextfocus();
    }
    private void Edittextfocus()
    {

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1)
                {
                    et2.requestFocus();
                }
                else if(s.length()==0)
                {
                    et1.clearFocus();

                }
            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1)
                {
                    et3.requestFocus();
                }
                else if(s.length()==0)
                {
                    et1.requestFocus();

                }
            }
        });

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1)
                {
                    et4.requestFocus();
                }
                else if(s.length()==0)
                {
                    et2.requestFocus();

                }
            }
        });

        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1)
                {
                    et5.requestFocus();
                }
                else if(s.length()==0)
                {
                    et3.requestFocus();
                }
            }
        });

        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1)
                {
                    et6.requestFocus();
                }
                else if(s.length()==0)
                {
                    et4.requestFocus();

                }
            }
        });

        et6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1)
                {
                    et6.clearFocus();

                }
                else if(s.length()==0)
                {
                    et5.requestFocus();

                }
            }
        });
    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        if (activity.equals("forgot")){
            Intent intent = new Intent(OtpActivity.this, ChangePasswordActivity.class);
            intent.putExtra("phone",strphone);
            startActivity(intent);
            finish();

        }
        else {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                storeUser();

                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(OtpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void storeUser()
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("name",strname);
        map.put("mail",strmail);
        map.put("phone",strphone);
        map.put("password",strpass);
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
        dref.child(strphone).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(OtpActivity.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                    loginAs();

                }else {
                    Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
    private void loginAs() {
        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user",strphone);
        editor.apply();
        startActivity(new Intent(OtpActivity.this, MainActivity.class));
        finish();
    }

    private void sendVerificationCode(String number){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                progressDialog.setTitle("OTP Detecting");
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressDialog.dismiss();
            Toast.makeText(OtpActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };


}