package com.example.neurosense;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import database.ImageAdapter;
import database.ImageStorageManager;

public class ImageDatabaseActivity extends AppCompatActivity {

    private ImageStorageManager imageStorageManager;
    private List<String> imageFiles;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_database);

        // Initialize ImageStorageManager
        imageStorageManager = new ImageStorageManager(this);
        imageFiles = imageStorageManager.getImageFiles();

        // Find the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Set up the RecyclerView and adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ImageAdapter adapter = new ImageAdapter(imageFiles, imageStorageManager);
        recyclerView.setAdapter(adapter);
    }
}