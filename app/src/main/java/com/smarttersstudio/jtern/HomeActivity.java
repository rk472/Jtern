package com.smarttersstudio.jtern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth=FirebaseAuth.getInstance();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
            dRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String status = dataSnapshot.child("status").getValue().toString();
                    if(!status.equals("none")){
                        Intent i=new Intent(HomeActivity.this,VulnerableActivity.class);
                        startActivity(i);
                        dRef.removeEventListener(this);
                        finishAffinity();
                        finish();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void gotoApply(View view) {
        startActivity(new Intent(this,ApplyActivity.class));
    }
}
