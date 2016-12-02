package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class AddListActivity extends AppCompatActivity {
    private EditText mListNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        mListNameView = (EditText) findViewById(R.id.listName);


        Button createListButton = (Button) findViewById(R.id.button);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createList();
            }
        });
    }

    public boolean createList() {
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
                    startActivity(new Intent(AddListActivity.this, ProductActivity.class));
                }
            });
            // get token in shared preferences
            UserUtil userUtil = new UserUtil();
            String token = userUtil.getToken(context);

            RequestFactory requestFactory = new RequestFactory();
            Request requestObject = requestFactory.createRequest();
            requestObject.setParameters("name", listName);
            requestObject.setParameters("token", token);
            requestObject.setEndpoint("/shopping_list/create.php");

            request.execute(requestObject);
        }

        return true;
    }
}
