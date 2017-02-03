package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

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

                        mAdapter = new CustomAdapterItem(ItemActivity.this, names, ids, nbProducts);
                        mListView.setAdapter(mAdapter);
                        Log.d("Plop", "name : " + itemName);
                        Log.d("Plop", "id : " + itemId);
                        Log.d("Plop", "quantity : " + quantity);
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
}
