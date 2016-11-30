package com.pinnackl.shoppinglist.request;

import java.util.HashMap;

/**
 * Created by htutuaku on 30/11/2016.
 */

public class Request {
    private String endpoint;
    private HashMap<String, String> parameters = new HashMap<>();

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(String key, String value) {
        this.parameters.put(key, value);
    }
}
