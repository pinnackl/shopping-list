package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.pinnackl.shoppinglist.CustomAdapter;
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

public class ProductActivity extends AppCompatActivity {
    ListView mListView;
    Context mContext;
    CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mListView = (ListView) findViewById(R.id.listView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductActivity.this, AddListActivity.class));
            }
        });

        // GET ALL LISTS
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
                    JSONArray lists = result.getJSONArray("result");
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> ids = new ArrayList<>();
                    ArrayList<String> dates = new ArrayList<>();
                    for (int i = 0; i < lists.length(); i++) {
                        JSONObject object = lists.getJSONObject(i);
                        String listName = object.getString("name");
                        names.add(listName);
                        String listId = object.getString("id");
                        ids.add(listId);
                        String listDateCreation = object.getString("created_date");
                        dates.add(listDateCreation);
                        String listCompleted = object.getString("completed");

                        mAdapter = new CustomAdapter(ProductActivity.this, names, ids, dates);
                        mListView.setAdapter(mAdapter);
                        Log.d("Plop", "name : " + listName);
                        Log.d("Plop", "id : " + listId);
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
        requestObject.setEndpoint("/shopping_list/list.php");

        request.execute(requestObject);
    }
}
