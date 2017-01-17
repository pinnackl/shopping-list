package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pinnackl.shoppinglist.HttpRequest;
import com.pinnackl.shoppinglist.IHttpRequestListener;
import com.pinnackl.shoppinglist.R;
import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONObject;

public class EditListActivity extends AppCompatActivity {

    private EditText mListNameView;
    private String id;
    private String name;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        mListNameView = (EditText) findViewById(R.id.listName);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        token = getIntent().getStringExtra("token");

        mListNameView.setText(name);
        Log.d("id", getIntent().getStringExtra("id"));
        Log.d("name", getIntent().getStringExtra("name"));
        Log.d("token", getIntent().getStringExtra("token"));

        Button createListButton = (Button) findViewById(R.id.button);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editList();
            }
        });
    }

    public boolean editList() {
        String listName = mListNameView.getText().toString();
        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(listName)) {
            mListNameView.setError(getString(R.string.error_field_required));
            focusView = mListNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Context context = getApplicationContext();

            HttpRequest request = new HttpRequest(context);
            request.setListener(new IHttpRequestListener() {
                @Override
                public void onFailure(String errorMessage) {
                    Log.d("Plop", "Error: " + errorMessage);
                }

                @Override
                public void onSuccess(JSONObject result) {
                    Log.d("Plop", "Success");
                    startActivity(new Intent(EditListActivity.this, ProductActivity.class));
                }
            });

            RequestFactory requestFactory = new RequestFactory();
            Request requestObject = requestFactory.createRequest();
            requestObject.setParameters("name", listName);
            requestObject.setParameters("token", token);
            requestObject.setParameters("id", id);
            requestObject.setEndpoint("/shopping_list/edit.php");

            request.execute(requestObject);
        }

        return true;
    }
}
