package com.pinnackl.shoppinglist.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pinnackl.shoppinglist.R;

public class AddListActivity extends AppCompatActivity {
    private EditText mListNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mListNameView = (EditText) findViewById(R.id.listName);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

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

        }

        return true;
    }
}
