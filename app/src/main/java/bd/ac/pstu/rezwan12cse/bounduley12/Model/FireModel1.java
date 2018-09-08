package bd.ac.pstu.rezwan12cse.bounduley12.Model;

/**
 * Created by Rezwan on 03-04-18.
 */

public class FireModel1 {
    public String name,email,mobile,profileImageUrl;
    public String timestamp;
    public double latitude,longitude;
    public String currentStatusUrl;

    public FireModel1() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrentStatusUrl() {
        return currentStatusUrl;
    }

    public void setCurrentStatusUrl(String currentStatusUrl) {
        this.currentStatusUrl = currentStatusUrl;
    }



}
