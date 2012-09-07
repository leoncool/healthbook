/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.models.merge;

import health.database.models.UserAvatar;
import health.database.models.Users;

/**
 *
 * @author Leon
 */
public class UserInfo {

    protected Users user;
    protected UserAvatar avatar;

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public UserAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(UserAvatar avatar) {
        this.avatar = avatar;
    }
    
}
