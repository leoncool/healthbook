package com.fitbit.api.common.model.devices;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScaleInvite {

    private Long inviteId;
    private String email;
    private String scaleUserName;

    public ScaleInvite(JSONObject jsonObject) throws JSONException {
        inviteId = jsonObject.getLong("inviteId");
        email = jsonObject.getString("email");
        scaleUserName = jsonObject.getString("scaleUserName");
    }

    public static List<ScaleInvite> jsonArrayToScaleInvitesList(JSONArray array) throws JSONException {
        List<ScaleInvite> scaleInvitesList = new ArrayList<ScaleInvite>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject scaleInviteReference = array.getJSONObject(i);
            scaleInvitesList.add(new ScaleInvite(scaleInviteReference));
        }
        return scaleInvitesList;
    }


    public Long getInviteId() {
        return inviteId;
    }

    public String getEmail() {
        return email;
    }

    public String getScaleUserName() {
        return scaleUserName;
    }
}
