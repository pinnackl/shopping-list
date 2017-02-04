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
import com.pinnackl.shoppinglist.request.Request;
import com.pinnackl.shoppinglist.request.RequestFactory;
import com.pinnackl.shoppinglist.user.UserUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by htutuaku on 02/12/2016.
 */

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> Title;
    private ArrayList<String> idList;
    private ArrayList<String> datesList;
    private boolean isEditing;
    private boolean activeEdit;

    public CustomAdapter(Context context, ArrayList<String> names, ArrayList<String> ids, ArrayList<String> dates) {
        mContext = context;
        Title = names;
        idList = ids;
        datesList = dates;
        isEditing = false;
        activeEdit = false;
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
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row, parent, false);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open item activity
                // get token in shared preferences
                UserUtil userUtil = new UserUtil();
                String token = userUtil.getToken(mContext);

                Intent intent = new Intent(mContext, ItemActivity.class);
                intent.putExtra("id", idList.get(position));
                intent.putExtra("token", token);
                intent.putExtra("name", Title.get(position));
                mContext.startActivity(intent);
            }
        });

        final TextView title;
        TextView editTitle;
        ImageView imgButton;
        ImageView editButton;

        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(Title.get(position));

        editTitle = (TextView) row.findViewById(R.id.editTitle);
        editTitle.setText(Title.get(position));

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
                                idList.remove(plop);
                                notifyDataSetChanged();
                            }
                        });
                        // get token in shared preferences
                        UserUtil userUtil = new UserUtil();
                        String token = userUtil.getToken(mContext);

                        RequestFactory requestFactory = new RequestFactory();
                        Request requestObject = requestFactory.createRequest();
                        requestObject.setParameters("token", token);
                        requestObject.setParameters("id", idList.get(position));
                        requestObject.setEndpoint("/shopping_list/remove.php");
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

        editButton = (ImageView) row.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int DRAWABLE_RIGHT = 2;
                final EditText editText = (EditText) row.findViewById(R.id.editTitle);
                final ViewSwitcher switcher = (ViewSwitcher) row.findViewById(R.id.switcher);
                final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);

                if(!activeEdit) {
                    switcher.showNext();
                }
                activeEdit = true;

                if(isEditing == true) {
                    isEditing = false;
                } else {
                    editText.setOnTouchListener(new View.OnTouchListener() {
                          public boolean onTouch(View v, MotionEvent event) {
                              if(event.getAction() == MotionEvent.ACTION_UP) {
                                  if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                      final String listName = editText.getText().toString();
                                      View focusView = null;
                                      boolean cancel = false;

                                      if(TextUtils.isEmpty(listName)) {
                                          editText.setError(mContext.getString(R.string.error_field_required));
                                          focusView = editText;
                                          cancel = true;
                                      }

                                      if (cancel) {
                                          focusView.requestFocus();
                                      } else {
                                          HttpRequest request = new HttpRequest(mContext);
                                          request.setListener(new IHttpRequestListener() {
                                              @Override
                                              public void onFailure(String errorMessage) {
                                                  Log.d("Plop", "Error: " + errorMessage);
                                              }

                                              @Override
                                              public void onSuccess(JSONObject result) {
                                                  title.setText(listName);
                                                  activeEdit = false;
                                                  isEditing = false;
                                                  imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                                  switcher.showNext();
                                              }
                                          });
                                          UserUtil userUtil = new UserUtil();
                                          String token = userUtil.getToken(mContext);

                                          RequestFactory requestFactory = new RequestFactory();
                                          Request requestObject = requestFactory.createRequest();
                                          requestObject.setParameters("name", listName);
                                          requestObject.setParameters("token", token);
                                          requestObject.setParameters("id", idList.get(position));
                                          requestObject.setEndpoint("/shopping_list/edit.php");

                                          request.execute(requestObject);
                                      }
                                      return true;
                                  }
                              }
                              return false;
                          }
                    });
                    editText.requestFocus();
                    editText.setSelection(editText.getText().length());
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    isEditing = true;
                }
            }
        });
        return (row);
    }
}
