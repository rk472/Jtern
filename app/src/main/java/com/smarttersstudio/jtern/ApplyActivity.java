package com.smarttersstudio.jtern;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ApplyActivity extends AppCompatActivity {
    private Spinner subList;
    private Button uploadButton;
    private FirebaseAuth mAuth;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        subList=findViewById(R.id.sub_list);
        uploadButton=findViewById(R.id.upload_button);
        uploadButton.setEnabled(false);
        uploadButton.setText("Please Wait...");
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        subList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                uploadButton.setEnabled(false);
                uploadButton.setText("please wait...");
                String[] sub=getResources().getStringArray(R.array.sub);
                String subject=sub[position];
                DatabaseReference d= FirebaseDatabase.getInstance().getReference().child(subject);
                d.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(uid)){
                            uploadButton.setText("Submitted...");
                        }else{
                            uploadButton.setEnabled(true);
                            uploadButton.setText("Upload CV Now");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void upload(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Agreement")
                .setMessage("Only upload the CV in PDF format not in other formats. If any vulnerability found(any other file except CV) in uploaded files then your account will be disabled.\nDo you agree to these terms and conditions ?")
                .setPositiveButton("Yes, Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 1);
                    }
                }).setNegativeButton("No, Don't",null).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data != null && data.getData() != null){
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("please wait");
                progressDialog.setMessage("Please wait while we are uploading your document..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                final String sub = subList.getSelectedItem().toString();
                uploadButton.setEnabled(false);
                uploadButton.setText("Please Wait");
                Uri uri = data.getData();
                StorageReference pdfRef = FirebaseStorage.getInstance().getReference().child(uid + "_" + sub + ".pdf");
                pdfRef.putFile(uri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApplyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        uploadButton.setText("Upload CV Now");
                        progressDialog.dismiss();
                        uploadButton.setEnabled(true);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = taskSnapshot.getDownloadUrl().toString();
                        FirebaseDatabase.getInstance().getReference().child(sub).child(uid).child("url").setValue(url);
                        Toast.makeText(ApplyActivity.this, "CV Successfully submitted..", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                });
        }
    }
}
