package com.pinnackl.shoppinglist.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.pinnackl.shoppinglist.CustomAdapterItem;
import com.pinnackl.shoppinglist.HttpRequest;
import com.pinnackl.shoppinglist.IHttpRequestListener;
import com.pinnackl.shoppinglist.R;
import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemActivity extends AppCompatActivity {
    private String id;
    private String name;
    private String token;

    ListView mListView;
    Context mContext;
    CustomAdapterItem mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.listView);
        mContext = getApplicationContext();

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        token = getIntent().getStringExtra("token");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddItemActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        mContext = getApplicationContext();

        try {
            String[] aProducts = new String[0];
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if(preferences.getString(id, "DEFAULT").equals("DEFAULT")) {
                SharedPreferences.Editor editor = preferences.edit();
                JSONArray jsonArray = new JSONArray(aProducts);
                editor.putString(id, jsonArray.toString());
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest request = new HttpRequest(mContext);
        request.setListener(new IHttpRequestListener() {
            @Override
            public void onFailure(String errorMessage) {
                Log.d("Plop", "Error: " + errorMessage);
            }

            @Override
            public void onSuccess(JSONObject result) {
                Log.d("Plop", "Success");
                try {
                    JSONArray items = result.getJSONArray("result");
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> ids = new ArrayList<>();
                    ArrayList<String> nbProducts = new ArrayList<>();

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject object = items.getJSONObject(i);
                        String itemName = object.getString("name");
                        names.add(itemName);
                        String itemId = object.getString("id");
                        ids.add(itemId);
                        String quantity = object.getString("quantity");
                        nbProducts.add(quantity);

                        mAdapter = new CustomAdapterItem(ItemActivity.this, names, ids, nbProducts, id);
                        mListView.setAdapter(mAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // get token in shared preferences
        UserUtil userUtil = new UserUtil();
        String token = userUtil.getToken(mContext);

        RequestFactory requestFactory = new RequestFactory();
        Request requestObject = requestFactory.createRequest();
        requestObject.setParameters("token", token);
        requestObject.setParameters("shopping_list_id", id);
        requestObject.setEndpoint("/product/list.php");

        request.execute(requestObject);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(ItemActivity.this, ProductActivity.class));
        return true;
    }
}
