package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pinnackl.shoppinglist.HttpRequest;
import com.pinnackl.shoppinglist.IHttpRequestListener;
import com.pinnackl.shoppinglist.R;
import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.User;
import com.pinnackl.shoppinglist.user.UserFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mFirstnameView;
    private EditText mLastnameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mFirstnameView = (EditText) findViewById(R.id.firstname);
        mLastnameView = (EditText) findViewById(R.id.lastname);

        Button mRegisterButton = (Button) findViewById(R.id.email_register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        TextView register = (TextView) findViewById(R.id.login);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void attemptRegister() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstname = mFirstnameView.getText().toString();
        String lastname = mLastnameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check for a valid firstname
        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
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
                    try {
                        Context context = getApplicationContext();
                        JSONObject oUser = null;
                        oUser = result.getJSONObject("result");
                        UserFactory factory = new UserFactory();
                        User user = factory.createUser();
                        UserUtil util = new UserUtil();
                        util.save(oUser, user, context);

                        Toast toast = Toast.makeText(RegisterActivity.this, "Successful registration", Toast.LENGTH_SHORT);
                        toast.show();

                        startActivity(new Intent(RegisterActivity.this, ProductActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            RequestFactory requestFactory = new RequestFactory();
            Request requestObject = requestFactory.createRequest();
            requestObject.setParameters("email", email);
            requestObject.setParameters("password", password);
            requestObject.setParameters("firstname", firstname);
            requestObject.setParameters("lastname", lastname);
            requestObject.setEndpoint("/account/subscribe.php");

            request.execute(requestObject);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
