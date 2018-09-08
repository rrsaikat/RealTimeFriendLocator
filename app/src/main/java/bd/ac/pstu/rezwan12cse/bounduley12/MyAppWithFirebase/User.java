package bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rezwan on 30-04-18.
 */
@IgnoreExtraProperties
public class User {
    public String name;
    public String email;
    public Double mobile;
    //public String password;
    public Double latitude;
    public Double longitude;

    public User() {
        //required for calls to datasnapshot.getvalue(user.class)
    }

    public User(String name, String email, Double mobile, Double latitude, Double longitude) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        //this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email",email);
        result.put("mobile",mobile);
        return result;
    }


}
