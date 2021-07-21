package com.greymatter.grozzer.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greymatter.grozzer.MainActivity;
import com.greymatter.grozzer.OtpActivity;
import com.greymatter.grozzer.R;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SignupFragment extends Fragment {
    EditText name,mail,phone,pass,confirm;
    Button signup;
    private ProgressBar progressBar;


    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupId(view);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String strname = name.getText().toString().trim();
                String strmail = mail.getText().toString().trim();
                String strphone = phone.getText().toString().trim();
                String strpass = pass.getText().toString().trim();
                String strconfirm = confirm.getText().toString().trim();

                if (isValidFields(strname,strphone,strpass,strconfirm)){
                    Intent intent = new Intent(getActivity(), OtpActivity.class);
                    intent.putExtra("phonenumber","+91"+strphone);
                    intent.putExtra("name",strname);
                    intent.putExtra("mail",strmail);
                    intent.putExtra("phone",strphone);
                    intent.putExtra("password",strpass);
                    intent.putExtra("activity","register");
                    startActivity(intent);

                }else {
                    signup.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
   /* private void storeUser(String strname, String strmail, String strphone, String strpass) {
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
                    Toast.makeText(getActivity(), "Registration Successfull", Toast.LENGTH_SHORT).show();
                    loginAs(strphone);
                    signup.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    signup.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }*/



    private boolean isValidFields(String strname,String strphone, String strpass, String strconfirm) {
        boolean valid=true,invalid=false;
        if (strname.isEmpty() || strphone.isEmpty() || strpass.isEmpty() || strconfirm.isEmpty()){
            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
            return invalid;
        }else if (strphone.length() != 10){
            phone.setError("Invalid");
            return invalid;
        }else if (!strpass.equals(strconfirm)){
            confirm.setError("Passord Does not match");
            return invalid;
        }else {
            return valid;
        }
    }

    private void setupId(View view) {
        name = view.findViewById(R.id.name);
        mail = view.findViewById(R.id.mail);
        phone = view.findViewById(R.id.phone);
        pass = view.findViewById(R.id.password);
        confirm = view.findViewById(R.id.confirm);
        signup = view.findViewById(R.id.signup);
        progressBar = view.findViewById(R.id.progressBar);
    }
}