package com.greymatter.grozzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greymatter.grozzer.Model.Extra;
import com.squareup.picasso.Picasso;

public class AboutUsActivity extends AppCompatActivity {
    TextView tvDetails;
    ImageView imgview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        tvDetails = findViewById(R.id.tvDetails);
        imgview = findViewById(R.id.imgview);
        getAboutUs();
    }
    private void getAboutUs() {
        FirebaseDatabase.getInstance().getReference("About").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Extra model = snapshot.getValue(Extra.class);
                tvDetails.setText(model.getDetails());
                Picasso.get().load(model.getImgUrl()).into(imgview);
                /*mobile = model.getMobile();
                strMail = model.getMail();
                whatsapp = model.getWhatsapp();*/
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