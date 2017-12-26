package wallet.mycoin.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Arun.Das on 26-12-2017.
 */

public class UserData implements Serializable{
    public String username;
    public String emailId;
    public String userid;
    public String photoUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
