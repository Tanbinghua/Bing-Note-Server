package com.info;

import net.sf.json.JSONObject;

public class ResInfo {
    private JSONObject resJson = new JSONObject();
    public ResInfo(int code, String msg, boolean status) {
        resJson.put("code", code);
        resJson.put("data", "");
        resJson.put("msg", msg);
        resJson.put("success", status);
    }

    public JSONObject getResJson() {
        return resJson;
    }
}
