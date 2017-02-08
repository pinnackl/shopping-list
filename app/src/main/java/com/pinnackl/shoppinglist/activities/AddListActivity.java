package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pinnackl.shoppinglist.HttpRequest;
import com.pinnackl.shoppinglist.IHttpRequestListener;
import com.pinnackl.shoppinglist.R;
import com.pinnackl.shoppinglist.list.List;
import com.pinnackl.shoppinglist.list.ListFactory;
import com.pinnackl.shoppinglist.list.ListUtil;
import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class AddListActivity extends AppCompatActivity {
    private EditText mListNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    Toast toast = Toast.makeText(AddListActivity.this, "Error", Toast.LENGTH_SHORT);
                    toast.show();
                }

                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        JSONObject oList = result.getJSONObject("result");
                        ListFactory factory = new ListFactory();
                        List list = factory.createList();
                        ListUtil util = new ListUtil();
                        util.save(oList, list);

                        Toast toast = Toast.makeText(AddListActivity.this, "List created", Toast.LENGTH_SHORT);
                        toast.show();
                        startActivity(new Intent(AddListActivity.this, ProductActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(AddListActivity.this, ProductActivity.class);
        AddListActivity.this.startActivity(intent);
        return true;
    }
}
