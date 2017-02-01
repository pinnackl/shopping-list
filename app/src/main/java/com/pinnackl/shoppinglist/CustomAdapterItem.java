package com.pinnackl.shoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.pinnackl.shoppinglist.activities.ItemActivity;
import com.pinnackl.shoppinglist.activities.LoginActivity;
import com.pinnackl.shoppinglist.activities.ProductActivity;
import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by htutuaku on 02/12/2016.
 */

public class CustomAdapterItem extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> Title;
    private ArrayList<String> idItem;

    public CustomAdapterItem(Context context, ArrayList<String> names, ArrayList<String> ids) {
        mContext = context;
        Title = names;
        idItem = ids;
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
        ImageView imgButton;

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row_item, parent, false);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(Title.get(position));

        imgButton = (ImageView) row.findViewById(R.id.imageButton);
        imgButton.setImageDrawable(mContext.getDrawable((R.drawable.ic_delete)));
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.setTheme(R.style.AppTheme);
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Suppression");
                alert.setMessage("Etes vous sur de vouloir supprimer cette liste ?");
                alert.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpRequest request = new HttpRequest(mContext);
                        request.setListener(new IHttpRequestListener() {
                            @Override
                            public void onFailure(String errorMessage) {
                                Log.d("Plop", "Error: " + errorMessage);
                            }

                            @Override
                            public void onSuccess(JSONObject result) {
                                Log.d("Plop", "Success");
                                Title.remove(plop);
                                idItem.remove(plop);
                                notifyDataSetChanged();
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
