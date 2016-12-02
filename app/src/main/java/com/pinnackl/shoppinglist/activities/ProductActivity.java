package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mListView = (ListView) findViewById(R.id.listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    for (int i = 0; i < lists.length(); i++) {
                        JSONObject object = lists.getJSONObject(i);
                        String listName = object.getString("name");
                        names.add(listName);
                        String listId = object.getString("id");
                        String listDateCreation = object.getString("created_date");
                        String listCompleted = object.getString("completed");

                        CustomAdapter adapter = new CustomAdapter(mContext, names);
                        mListView.setAdapter(adapter);
                        Log.d("Plop", "value : " + listName);
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
