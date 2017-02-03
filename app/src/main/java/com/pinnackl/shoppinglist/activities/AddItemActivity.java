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
import android.widget.NumberPicker;

import com.pinnackl.shoppinglist.HttpRequest;
import com.pinnackl.shoppinglist.IHttpRequestListener;
import com.pinnackl.shoppinglist.R;
import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONObject;

public class AddItemActivity extends AppCompatActivity {
    private EditText mItemNameView;
    private NumberPicker mNumberPicker;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mItemNameView = (EditText) findViewById(R.id.itemName);
        mNumberPicker = (NumberPicker) findViewById(R.id.numberPicker);

        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(100);
        mNumberPicker.setWrapSelectorWheel(false);

        id = getIntent().getStringExtra("id");

        Button createListButton = (Button) findViewById(R.id.button);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createItem();
            }
        });
    }

    public boolean createItem() {
        String listName = mItemNameView.getText().toString();
        Integer nbProducts = mNumberPicker.getValue();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(listName)) {
            mItemNameView.setError(getString(R.string.error_field_required));
            focusView = mItemNameView;
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
                    startActivity(new Intent(AddItemActivity.this, ItemActivity.class));
                }
            });
            // get token in shared preferences
            UserUtil userUtil = new UserUtil();
            String token = userUtil.getToken(context);

            RequestFactory requestFactory = new RequestFactory();
            Request requestObject = requestFactory.createRequest();
            requestObject.setParameters("name", listName);
            requestObject.setParameters("token", token);
            requestObject.setParameters("shopping_list_id", id);
            requestObject.setParameters("quantity", String.valueOf(nbProducts));
            requestObject.setEndpoint("/product/create.php");

            request.execute(requestObject);
        }

        return true;
    }
}
