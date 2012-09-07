/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.input.jsonmodels;

import health.database.models.Users;

/**
 *
 * @author Leon
 */
public class JsonFollower {

    private String loginid;
    private String follower_id;
    private String note;
    private String screenname;
    private String status;
    private JsonUserInfo following_info;
    private JsonUserInfo follower_info;
    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }

   
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getScreenname() {
        return screenname;
    }

    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JsonUserInfo getFollowing_info() {
        return following_info;
    }

    public void setFollowing_info(JsonUserInfo following_info) {
        this.following_info = following_info;
    }

    public JsonUserInfo getFollower_info() {
        return follower_info;
    }

    public void setFollower_info(JsonUserInfo follower_info) {
        this.follower_info = follower_info;
    }
    
    
}
