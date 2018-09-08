package bd.ac.pstu.rezwan12cse.bounduley12;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Rezwan on 17-04-18.
 */

public class Constants1 {

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Mobile validation pattern
    //public static  final String mobregEx="^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$";
    public static  final String mobregEx="^";

//

    public static boolean isOnline(Context applicationContext) {

        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else
            return false;

    }

}