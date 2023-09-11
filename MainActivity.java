package com.example.iskeduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicMarkableReference;

import android.text.TextUtils;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private NoteAdapter adapter;
    private ArrayList<Note> dataList;
    private SearchView Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileUsername = findViewById(R.id.profileUsername);

        // Call showUsername to set the username
        showUsername();

        recycler = findViewById(R.id.recycler);

        // Create sample data
        dataList = new ArrayList<>();

        // Set up RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(this, dataList);
        recycler.setAdapter(adapter);

        // Initialize Firebase
        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Button addNoteButton = findViewById(R.id.addNote);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_note, null);
                TextInputLayout TitleLayout, ContentLayout;
                TitleLayout = view1.findViewById(R.id.TitleLayout);
                ContentLayout = view1.findViewById(R.id.ContentLayout);
                TextInputEditText Title, Content;
                Title = view1.findViewById(R.id.Title);
                Content = view1.findViewById(R.id.Content);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add new Task")
                        .setView(view1)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Objects.requireNonNull(Title.getText()).toString().isEmpty()) {
                                    TitleLayout.setError("This field is required!");
                                } else if (Objects.requireNonNull(Content.getText()).toString().isEmpty()) {
                                    ContentLayout.setError("This field is required!");
                                } else {
                                    ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                                    dialog.setMessage("Storing in Database...");
                                    dialog.show();
                                    Note note = new Note();
                                    note.setTitle(Title.getText().toString());
                                    note.setContent(Content.getText().toString());
                                    database.getReference().child("notes").push().setValue(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(MainActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        TextView empty = findViewById(R.id.empty);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        database.getReference().child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Note note = dataSnapshot.getValue(Note.class);
                    Objects.requireNonNull(note).setKey(dataSnapshot.getKey());
                    dataList.add(note);
                }

                adapter.notifyDataSetChanged();

                if (dataList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors here
            }
        });

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(Note note) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_note, null);
                TextInputLayout TitleLayout, ContentLayout;
                TextInputEditText Title, Content;

                Title = view.findViewById(R.id.Title);
                Content = view.findViewById(R.id.Content);
                TitleLayout = view.findViewById(R.id.TitleLayout);
                ContentLayout = view.findViewById(R.id.ContentLayout);

                Title.setText(note.getTitle());
                Content.setText(note.getContent());

                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Edit")
                        .setView(view)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Objects.requireNonNull(Title.getText()).toString().isEmpty()) {
                                    TitleLayout.setError("This field is required!");
                                } else if (Objects.requireNonNull(Content.getText()).toString().isEmpty()) {
                                    ContentLayout.setError("This field is required!");
                                } else {
                                    progressDialog.setMessage("Saving...");
                                    progressDialog.show();
                                    Note note1 = new Note();
                                    note1.setTitle(Title.getText().toString());
                                    note1.setContent(Content.getText().toString());
                                    database.getReference().child("notes").child(note.getKey()).setValue(note1)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(MainActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setTitle("Deleting...");
                                progressDialog.show();
                                database.getReference().child("notes").child(note.getKey()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }
    TextView profileUsername;


    public void showUsername(){
        Intent intent = getIntent();

        String usernameUser = intent.getStringExtra("username");

        Log.d("UserProfileActivity", "Username from Intent: " + usernameUser);

        profileUsername.setText(usernameUser);

    }
}