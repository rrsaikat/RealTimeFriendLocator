package bd.ac.pstu.rezwan12cse.bounduley12;


import bd.ac.pstu.rezwan12cse.bounduley12.managers.DatabaseHelper;

public class ApplicationHelper {

    private static final String TAG = ApplicationHelper.class.getSimpleName();
    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public static void initDatabaseHelper(android.app.Application application) {
        databaseHelper = DatabaseHelper.getInstance(application);
        databaseHelper.init();
    }
}
