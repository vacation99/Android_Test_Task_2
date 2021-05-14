package com.example.testapp_2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(recyclerViewAdapter);

        new SimpleAsyncTask(recyclerViewAdapter, this).execute();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.alert_dialog);

                Button button = dialog.findViewById(R.id.buttonDialog);
                EditText editText = dialog.findViewById(R.id.editTextDialog);
                RatingBar ratingBar = dialog.findViewById(R.id.ratingBarDialog);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = editText.getText().toString();
                        float rating = ratingBar.getRating();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }
}