package bd.ac.pstu.rezwan12cse.bounduley12.managers;

import android.content.Context;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.ac.pstu.rezwan12cse.bounduley12.ApplicationHelper;


public class FirebaseListenersManager {
    Map<Context, List<ValueEventListener>> activeListeners = new HashMap<>();

    void addListenerToMap(Context context, ValueEventListener valueEventListener) {
        if (activeListeners.containsKey(context)) {
            activeListeners.get(context).add(valueEventListener);
        } else {
            List<ValueEventListener> valueEventListeners = new ArrayList<>();
            valueEventListeners.add(valueEventListener);
            activeListeners.put(context, valueEventListeners);
        }
    }

    public void closeListeners(Context context) {
        DatabaseHelper databaseHelper = ApplicationHelper.getDatabaseHelper();
        if (activeListeners.containsKey(context)) {
            for (ValueEventListener listener : activeListeners.get(context)) {
                databaseHelper.closeListener(listener);
            }
            activeListeners.remove(context);
        }
    }
}
