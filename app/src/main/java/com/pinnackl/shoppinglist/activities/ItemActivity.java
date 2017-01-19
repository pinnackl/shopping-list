package com.pinnackl.shoppinglist.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pinnackl.shoppinglist.R;

public class ItemActivity extends AppCompatActivity {
    private String id;
    private String name;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        token = getIntent().getStringExtra("token");

        Log.d("id", getIntent().getStringExtra("id"));
        Log.d("name", getIntent().getStringExtra("name"));
        Log.d("token", getIntent().getStringExtra("token"));
    }
}
