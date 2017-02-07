package com.pinnackl.shoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by htutuaku on 02/12/2016.
 */

public class CustomAdapterItem extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> Title;
    private ArrayList<String> idItem;
    private ArrayList<String> quantity;
    private String listId;

    public CustomAdapterItem(Context context, ArrayList<String> names, ArrayList<String> ids, ArrayList<String> nbProducts, String id) {
        mContext = context;
        Title = names;
        idItem = ids;
        quantity = nbProducts;
        listId = id;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return Title.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final int plop = position;
        final View row;
        final TextView title;
        final TextView nbProducts;
        ImageView imgButton;
        CheckBox checkBox;

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row_item, parent, false);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(Title.get(position));

        nbProducts = (TextView) row.findViewById(R.id.quantity);
        nbProducts.setText(quantity.get(position));

        checkBox = (CheckBox) row.findViewById(R.id.checkBox);

        // if we found product id in shared preferences, we disable the row
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

            String jsonProductList = preferences.getString(listId, "DEFAULT");
            JSONArray aJsonProductList = new JSONArray(jsonProductList);
            for (int i = 0; i < aJsonProductList.length(); i++) {
                if(aJsonProductList.getString(i).equals(idItem.get(position))) {
                    title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    title.setTextColor(Color.GRAY);
                    checkBox.setChecked(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    Log.d("checked:", "true");
                    title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    title.setTextColor(Color.GRAY);

                    try {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = preferences.edit();

                        String jsonProductList = preferences.getString(listId, "DEFAULT");
                        JSONArray aJsonProductList = new JSONArray(jsonProductList);
                        aJsonProductList.put(idItem.get(position));

                        jsonProductList = aJsonProductList.toString();
                        editor.putString(listId, jsonProductList);
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("checked:", "false");
                    title.setPaintFlags(title.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    title.setTextColor(Color.BLACK);

                    try {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = preferences.edit();

                        String jsonProductList = preferences.getString(listId, "DEFAULT");
                        JSONArray aJsonProductList = new JSONArray(jsonProductList);
                        for (int i = 0; i < aJsonProductList.length(); i++) {
                            if(aJsonProductList.getString(i).equals(idItem.get(position))) {
                                aJsonProductList.remove(i);
                            }
                        }
                        jsonProductList = aJsonProductList.toString();
                        editor.putString(listId, jsonProductList);
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        imgButton = (ImageView) row.findViewById(R.id.imageButton);
        imgButton.setImageDrawable(mContext.getDrawable((R.drawable.ic_delete)));
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.setTheme(R.style.AppTheme);
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Deletion");
                alert.setMessage("Are you sure you want to delete this product ?");
                alert.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpRequest request = new HttpRequest(mContext);
                        request.setListener(new IHttpRequestListener() {
                            @Override
                            public void onFailure(String errorMessage) {
                                Toast toast = Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            @Override
                            public void onSuccess(JSONObject result) {
                                Log.d("Plop", "Success");
                                Title.remove(plop);
                                idItem.remove(plop);
                                notifyDataSetChanged();
                                Toast toast = Toast.makeText(mContext, "Product deleted", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                        // get token in shared preferences
                        UserUtil userUtil = new UserUtil();
                        String token = userUtil.getToken(mContext);

                        RequestFactory requestFactory = new RequestFactory();
                        Request requestObject = requestFactory.createRequest();
                        requestObject.setParameters("token", token);
                        requestObject.setParameters("id", idItem.get(position));
                        requestObject.setEndpoint("/product/remove.php");
                        request.execute(requestObject);

                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NON", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        return (row);
    }
}
