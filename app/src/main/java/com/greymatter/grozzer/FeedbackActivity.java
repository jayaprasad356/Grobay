package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Model.Extra;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    EditText etFeed;
    Button submit;
    private String mobile,whatsapp,strMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        setId();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = etFeed.getText().toString().trim();
                if (str.isEmpty()){
                    Toast.makeText(FeedbackActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFeedback(str);
                }
            }
        });


    }

    private void setId() {

        etFeed = findViewById(R.id.etFeed);
        submit = findViewById(R.id.submit);
    }



    private void uploadFeedback(String str) {
        String id = String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> map = new HashMap<>();
        map.put("details",str);
        FirebaseDatabase.getInstance().getReference("Feedback").child(id).updateChildren(map);
        Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
        etFeed.setText("");
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

}