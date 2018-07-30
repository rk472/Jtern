package com.smarttersstudio.jtern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText emailText,passText;
    private int e=0,p=0;
    private Button loginButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText=findViewById(R.id.login_email);
        passText=findViewById(R.id.login_password);
        loginButton=findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        mAuth=FirebaseAuth.getInstance();
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)) e=0;
                else                     e=1;
                if(e==1 && p==1) loginButton.setEnabled(true);
                else             loginButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)) p=0;
                else                     p=1;
                if(e==1 && p==1) loginButton.setEnabled(true);
                else             loginButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void login(View view) {
        view.setEnabled(false);
        loginButton.setText("Please Wait..");
        String email=emailText.getText().toString();
        String pass=passText.getText().toString();
        mAuth.signInWithEmailAndPassword(email,pass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginButton.setEnabled(true);
                loginButton.setText("login");
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setTitle("Please Wait");
                pd.setMessage("Checking Your Eligibility Status ... ");
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(false);
                pd.show();
                final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                dRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.child("status").getValue().toString();
                        if(status.equals("none")){
                            pd.dismiss();
                            Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(i);
                            dRef.removeEventListener(this);
                            finish();
                        }else{
                            pd.dismiss();
                            Intent i=new Intent(LoginActivity.this,VulnerableActivity.class);
                            startActivity(i);
                            dRef.removeEventListener(this);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            Intent i=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void goToRegister(View view) {
        Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }
}
