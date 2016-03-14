package cs442.com.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Handles the database version
    public static final int DATABASE_VERSION = 1;

    // Handles the database name
    public static final String DATABASE_NAME = "pulse";

    // Handles the tables
    public static final String TABLE_ADDRESS = "address";
    public static final String TABLE_ALERTS = "alerts";

    // Handles the fields

    // ADDRESS
    public static final String KEY_ADDRESS_ID = "addressID";
    public static final String KEY_ADDRESS_STREET = "addressStreet";
    public static final String KEY_ADDRESS_CITY = "addressCity";
    public static final String KEY_ADDRESS_STATE = "addressState";
    public static final String KEY_ADDRESS_ZIP_CODE = "addressZipCode";
    public static final String KEY_ADDRESS_LATITUDE = "addressLatitude";
    public static final String KEY_ADDRESS_LONGITUDE = "addressLongitude";

    // ALERTS
    public static final String KEY_ALERTS_ID = "alertID";
    public static final String KEY_ALERTS_ADDRESS_ID = "alertAddressID";
    public static final String KEY_ALERTS_TITLE = "alertTitle";
    public static final String KEY_ALERTS_DESCRIPTION = "alertDescription";
    public static final String KEY_ALERTS_RADIUS = "alertRadius";
    public static final String KEY_ALERTS_STATUS = "alertStatus";

    // Handles the queries to create the tables
    private static final String CREATE_TABLE_ADDRESS = "CREATE TABLE "+TABLE_ADDRESS+" ("+KEY_ADDRESS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_ADDRESS_STREET+" TEXT,"+KEY_ADDRESS_CITY+" TEXT,"+KEY_ADDRESS_STATE+" TEXT,"+KEY_ADDRESS_ZIP_CODE+" INTEGER,"+KEY_ADDRESS_LATITUDE+" REAL,"+KEY_ADDRESS_LONGITUDE+" REAL);";
    private static final String CREATE_TABLE_ALERTS = "CREATE TABLE "+TABLE_ALERTS+" ("+KEY_ALERTS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+KEY_ALERTS_ADDRESS_ID+" INTEGER,"+KEY_ALERTS_TITLE+" TEXT NOT NULL UNIQUE,"+KEY_ALERTS_DESCRIPTION+" TEXT,"+KEY_ALERTS_RADIUS+" REAL,"+KEY_ALERTS_STATUS+" INTEGER);";

    // Handles the queries to drop the tables 
    private static final String DROP_TABLE_ADDRESS = "DROP TABLE IF EXISTS "+TABLE_ADDRESS;
    private static final String DROP_TABLE_ALERTS = "DROP TABLE IF EXISTS "+TABLE_ALERTS;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ADDRESS);
        db.execSQL(CREATE_TABLE_ALERTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_ADDRESS);
        db.execSQL(DROP_TABLE_ALERTS);
        onCreate(db);
    }
}