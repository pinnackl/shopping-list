package com.pinnackl.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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

public class ProductActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView mListView;
    Context mContext;
    CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.listView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

                        mAdapter = new CustomAdapter(ProductActivity.this, names, ids, dates);
                        mListView.setAdapter(mAdapter);
                    }
                    if(lists.length() == 0) {
                        TextView textEmpty = (TextView)findViewById(R.id.empty);
                        textEmpty.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // get token in shared preferences
        UserUtil userUtil = new UserUtil();
        String token = userUtil.getToken(mContext);

        // set menu header values
        String email = userUtil.get(mContext, "email");
        String firstName = userUtil.get(mContext, "firstname");
        String lastName = userUtil.get(mContext, "lastname");

        View header = navigationView.getHeaderView(0);

        TextView emailHeader = (TextView)header.findViewById(R.id.email);
        TextView nameHeader = (TextView)header.findViewById(R.id.name);
        emailHeader.setText(email);
        nameHeader.setText(firstName + " " + lastName);

        // get shopping lists
        RequestFactory requestFactory = new RequestFactory();
        Request requestObject = requestFactory.createRequest();
        requestObject.setParameters("token", token);
        requestObject.setEndpoint("/shopping_list/list.php");

        request.execute(requestObject);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.log_out) {
            // Handle the camera action
            UserUtil userUtil = new UserUtil();
            userUtil.logout(ProductActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
