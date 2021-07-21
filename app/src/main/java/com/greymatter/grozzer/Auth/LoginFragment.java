package com.greymatter.grozzer.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.MainActivity;
import com.greymatter.grozzer.Model.User;
import com.greymatter.grozzer.OtpActivity;
import com.greymatter.grozzer.R;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    EditText mail,password;
    Button login;
    ProgressBar progressBar;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient googleApiClient;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.findViewById(R.id.guest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAs("guest","Guest");
            }
        });
        view.findViewById(R.id.gmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gmailLogin();
            }
        });
        view.findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //forgot
                String strphone = mail.getText().toString().trim();
                if (strphone.length() != 10){
                    Toast.makeText(getActivity(), "Invalid Mobile number", Toast.LENGTH_SHORT).show();
                }else {
                    forgot(strphone);
                }
            }
        });

        setId(view);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String strmail = mail.getText().toString().trim();
                String strpass = password.getText().toString().trim();
                if (isValidField(strmail,strpass)){
                    signIn(strmail,strpass);
                }else {
                    login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void forgot(String strphone) {
        Intent intent = new Intent(getActivity(), OtpActivity.class);
        intent.putExtra("phonenumber","+91"+strphone);
        intent.putExtra("phone",strphone);
        intent.putExtra("activity","forgot");
        startActivity(intent);
    }

    private void gmailLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient= new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth = FirebaseAuth.getInstance();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void Disconnect_google() {
        try {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        googleApiClient.disconnect();
                    }
                });

            }
        } catch (Exception e) {
            Log.d("DISCONNECT ERROR", e.toString());
        }
    }

    private void signIn(String strmail, String strpass) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found=false;
                for (DataSnapshot ds : snapshot.getChildren()){
                    User model = ds.getValue(User.class);
                    if (model.getPhone().equals(strmail) && model.getPassword().equals(strpass)){
                        loginAs(model.getPhone(),model.getName());
                        found=true;
                    }else{
                        found=false;
                    }
                }

                if (!found){
                    Toast.makeText(getActivity(), "User not Found", Toast.LENGTH_SHORT).show();
                    login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                /*mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(strmail,strpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                User model = ds.getValue(User.class);
                                assert model != null;
                                if (strmail.equals(model.getMail())){
                                    loginAs(model.getPhone(),model.getName());
                                }


                            }


                        }
                        else {
                            login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "User Not Found", Toast.LENGTH_SHORT).show();
                        }

                    }
                });*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isValidField(String strmail, String strpass) {
        if (strmail.isEmpty() || strpass.isEmpty()){
            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private void setId(View view) {
        mail = view.findViewById(R.id.mail);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void loginAs(String guest,String name) {
        SharedPreferences sp = getActivity().getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user",guest);
        editor.apply();

        Toast.makeText(getActivity(),"Logged in as "+name, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount user) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Disconnect_google();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            storeUser(user.getDisplayName(),user.getEmail(),user.getId(),"12345");
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(),  task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void storeUser(String strname, String strmail, String strphone, String strpass) {
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
                    loginAs(strphone,strname);
                }else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GoogleActivity", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken(),account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GoogleActivity", "Google sign in failed", e);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

}