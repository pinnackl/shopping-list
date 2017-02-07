package com.pinnackl.shoppinglist.list;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alaixys on 07/02/2017.
 */

public class ListUtil {
    public void save(JSONObject oList, List list) throws JSONException {
        String id = oList.getString("id");
        String name = oList.getString("name");

        list.setId(id);
        list.setName(name);
    }
}
