package com.smarttersstudio.jtern;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameText,emailText,passText,phoneText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private int n=0,e=0,pa=0,ph=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameText=findViewById(R.id.register_name);
        emailText=findViewById(R.id.register_email);
        passText=findViewById(R.id.register_pass);
        phoneText=findViewById(R.id.register_phone);
        registerButton=findViewById(R.id.register_button);
        mAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("users");
        registerButton.setEnabled(false);
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s))  n=0;  else n=1;
                if(n==1 && ph==1 && pa==1 && e==1) registerButton.setEnabled(true);
                else                               registerButton.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s))  e=0;  else e=1;
                if(n==1 && ph==1 && pa==1 && e==1) registerButton.setEnabled(true);
                else                               registerButton.setEnabled(false);
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
                if(TextUtils.isEmpty(s))  pa=0;  else pa=1;
                if(n==1 && ph==1 && pa==1 && e==1) registerButton.setEnabled(true);
                else                               registerButton.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s))  ph=0;  else ph=1;
                if(n==1 && ph==1 && pa==1 && e==1) registerButton.setEnabled(true);
                else                               registerButton.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    public void register(View view) {
        registerButton.setEnabled(false);
        registerButton.setText("please Wait...");
        final String name=nameText.getText().toString();
        final String mail=emailText.getText().toString();
        String pass=passText.getText().toString();
        final String phone=phoneText.getText().toString();
        mAuth.createUserWithEmailAndPassword(mail,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(final AuthResult authResult) {
                Map m = new HashMap();
                m.put("email", mail);
                m.put("name", name);
                m.put("phone", phone);
                userRef.child(authResult.getUser().getUid()).updateChildren(m).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(RegisterActivity.this, "SuccessFully Registered...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        registerButton.setText("login");
                        registerButton.setEnabled(true);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                registerButton.setText("login");
                registerButton.setEnabled(true);
            }
        });
    }
}