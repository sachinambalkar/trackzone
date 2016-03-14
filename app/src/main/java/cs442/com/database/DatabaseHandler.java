package cs442.com.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler {

    public SQLiteDatabase database;
    public DatabaseHelper databaseHelper;

    public long addressID;
    public long alertID;
    public int updateID;

    public DatabaseHandler(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    // CRUD Commands

    // BOTH COMMANDS
    public long createBoth(String street, String city, String state, int zip_code, double latitude, double longitude, String title, String description, double radius, int status) {
        addressID = insertIntoAddress(street, city, state, zip_code, latitude, longitude);
        alertID = insertIntoAlert(addressID, title, description, radius, status);

        Log.d("DB", "createBoth");

        return alertID;
    }

    public int updateBoth(long addressID, String street, String city, String state, int zip_code, double latitude, double longitude, long alertID, String title, String description, double radius, int status) {
        updateID = updateAddress(addressID, street, city, state, zip_code, latitude, longitude);
        updateID = updateAlert(alertID, addressID, title, description, radius, status);

        return updateID;
    }

    public void deleteBoth(long addressID, long alertID) {
        deleteAddress(addressID);
        deleteAlert(alertID);
    }

    // ADDRESS COMMANDS
    public long insertIntoAddress(String street, String city, String state, int zip_code, double latitude, double longitude) {
        ContentValues val = new ContentValues();
        val.put(DatabaseHelper.KEY_ADDRESS_STREET, street);
        val.put(DatabaseHelper.KEY_ADDRESS_CITY, city);
        val.put(DatabaseHelper.KEY_ADDRESS_STATE, state);
        val.put(DatabaseHelper.KEY_ADDRESS_ZIP_CODE, zip_code);
        val.put(DatabaseHelper.KEY_ADDRESS_LATITUDE, latitude);
        val.put(DatabaseHelper.KEY_ADDRESS_LONGITUDE, longitude);

        long addressID = database.insert(DatabaseHelper.TABLE_ADDRESS, null, val);

        Log.d("DB", "addressID: "+addressID);

        return addressID;
    }

    public int updateAddress(long addressID, String street, String city, String state, int zip_code, double latitude, double longitude) {
        ContentValues val = new ContentValues();
        val.put(DatabaseHelper.KEY_ADDRESS_STREET, street);
        val.put(DatabaseHelper.KEY_ADDRESS_CITY, city);
        val.put(DatabaseHelper.KEY_ADDRESS_STATE, state);
        val.put(DatabaseHelper.KEY_ADDRESS_ZIP_CODE, zip_code);
        val.put(DatabaseHelper.KEY_ADDRESS_LATITUDE, latitude);
        val.put(DatabaseHelper.KEY_ADDRESS_LONGITUDE, longitude);

        return database.update(DatabaseHelper.TABLE_ADDRESS, val, DatabaseHelper.KEY_ADDRESS_ID+" = ?", new String[] {String.valueOf(addressID)});
    }

    public void deleteAddress(long addressID) {
        database.delete(DatabaseHelper.TABLE_ADDRESS, DatabaseHelper.KEY_ADDRESS_ID+" = ?", new String[] {String.valueOf(addressID)});
    }

    // ALERT COMMANDS
    public long insertIntoAlert(long addressID, String title, String description, double radius, int status) {
        ContentValues val = new ContentValues();
        val.put(DatabaseHelper.KEY_ALERTS_ADDRESS_ID, addressID);
        val.put(DatabaseHelper.KEY_ALERTS_TITLE, title);
        val.put(DatabaseHelper.KEY_ALERTS_DESCRIPTION, description);
        val.put(DatabaseHelper.KEY_ALERTS_RADIUS, radius);
        val.put(DatabaseHelper.KEY_ALERTS_STATUS, status);

        try {
            alertID = database.insert(DatabaseHelper.TABLE_ALERTS, null, val);
        }catch(Exception e)
        {
            e.printStackTrace();
            alertID=-1;
        }
        Log.d("DB", "alertID: "+alertID);
        return alertID;

    }

    public int updateAlert(long alertID, long addressID, String title, String description, double radius, int status) {
        ContentValues val = new ContentValues();
        val.put(DatabaseHelper.KEY_ALERTS_ADDRESS_ID, addressID);
        val.put(DatabaseHelper.KEY_ALERTS_TITLE, title);
        val.put(DatabaseHelper.KEY_ALERTS_DESCRIPTION, description);
        val.put(DatabaseHelper.KEY_ALERTS_RADIUS, radius);
        val.put(DatabaseHelper.KEY_ALERTS_STATUS, status);

        return database.update(DatabaseHelper.TABLE_ALERTS, val, DatabaseHelper.KEY_ALERTS_ID+" = ?", new String[] {String.valueOf(alertID)});
    }

    public void deleteAlert(long alertID) {
        database.delete(DatabaseHelper.TABLE_ALERTS, DatabaseHelper.KEY_ALERTS_ID+" = ?", new String[] {String.valueOf(alertID)});
    }

    // READ COMMANDS
    public List<Alert> readAllAlerts() {
       String FACEBOOK_USER_ID="FACEBOOK_USERID";
        List<Alert> alerts = new ArrayList<Alert>();
        String selectQuery = "SELECT "+DatabaseHelper.KEY_ADDRESS_ID
                                    +","+DatabaseHelper.KEY_ADDRESS_STREET
                                    +","+DatabaseHelper.KEY_ADDRESS_CITY
                                    +","+DatabaseHelper.KEY_ADDRESS_STATE
                                    +","+DatabaseHelper.KEY_ADDRESS_ZIP_CODE
                                    +","+DatabaseHelper.KEY_ADDRESS_LATITUDE
                                    +","+DatabaseHelper.KEY_ADDRESS_LONGITUDE
                                    +","+DatabaseHelper.KEY_ALERTS_ID
                                    +","+DatabaseHelper.KEY_ALERTS_ADDRESS_ID
                                    +","+DatabaseHelper.KEY_ALERTS_TITLE
                                    +","+DatabaseHelper.KEY_ALERTS_DESCRIPTION
                                    +","+DatabaseHelper.KEY_ALERTS_RADIUS
                                    +","+DatabaseHelper.KEY_ALERTS_STATUS
                            +" FROM "+DatabaseHelper.TABLE_ADDRESS
                                    +","+DatabaseHelper.TABLE_ALERTS
                            +" WHERE "+DatabaseHelper.KEY_ADDRESS_ID+" = "+DatabaseHelper.KEY_ALERTS_ADDRESS_ID+
                            " AND "+DatabaseHelper.KEY_ALERTS_TITLE +" <> '"+FACEBOOK_USER_ID+"' ; ";

        Cursor cursor;
        if(!database.isOpen())
            open();
        cursor = database.rawQuery(selectQuery, null);
        Log.d("DB", "readAllAlerts: "+cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                alerts.add(cursorToAlert(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return alerts;
    }

    public List<Alert> readAllActiveAlerts() {
        List<Alert> alerts = new ArrayList<Alert>();
        String selectQuery = "SELECT "+DatabaseHelper.KEY_ADDRESS_ID
                +","+DatabaseHelper.KEY_ADDRESS_STREET
                +","+DatabaseHelper.KEY_ADDRESS_CITY
                +","+DatabaseHelper.KEY_ADDRESS_STATE
                +","+DatabaseHelper.KEY_ADDRESS_ZIP_CODE
                +","+DatabaseHelper.KEY_ADDRESS_LATITUDE
                +","+DatabaseHelper.KEY_ADDRESS_LONGITUDE
                +","+DatabaseHelper.KEY_ALERTS_ID
                +","+DatabaseHelper.KEY_ALERTS_ADDRESS_ID
                +","+DatabaseHelper.KEY_ALERTS_TITLE
                +","+DatabaseHelper.KEY_ALERTS_DESCRIPTION
                +","+DatabaseHelper.KEY_ALERTS_RADIUS
                +","+DatabaseHelper.KEY_ALERTS_STATUS
                +" FROM "+DatabaseHelper.TABLE_ADDRESS
                +","+DatabaseHelper.TABLE_ALERTS
                +" WHERE "+DatabaseHelper.KEY_ADDRESS_ID+" = "+DatabaseHelper.KEY_ALERTS_ADDRESS_ID
                  +" AND "+DatabaseHelper.KEY_ALERTS_STATUS+" = 1;";

        Cursor cursor = database.rawQuery(selectQuery, null);

        Log.d("DB", "readAllActiveAlerts: "+cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                alerts.add(cursorToAlert(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return alerts;
    }

    public Alert readAlertByID(long alertID) {
        String selectQuery = "SELECT "+DatabaseHelper.KEY_ADDRESS_ID
                                    +","+DatabaseHelper.KEY_ADDRESS_STREET
                                    +","+DatabaseHelper.KEY_ADDRESS_CITY
                                    +","+DatabaseHelper.KEY_ADDRESS_STATE
                                    +","+DatabaseHelper.KEY_ADDRESS_ZIP_CODE
                                    +","+DatabaseHelper.KEY_ADDRESS_LATITUDE
                                    +","+DatabaseHelper.KEY_ADDRESS_LONGITUDE
                                    +","+DatabaseHelper.KEY_ALERTS_ID
                                    +","+DatabaseHelper.KEY_ALERTS_ADDRESS_ID
                                    +","+DatabaseHelper.KEY_ALERTS_TITLE
                                    +","+DatabaseHelper.KEY_ALERTS_DESCRIPTION
                                    +","+DatabaseHelper.KEY_ALERTS_RADIUS
                                    +","+DatabaseHelper.KEY_ALERTS_STATUS
                            +" FROM "+DatabaseHelper.TABLE_ADDRESS
                                    +","+DatabaseHelper.TABLE_ALERTS
                            +" WHERE "+DatabaseHelper.KEY_ADDRESS_ID+" = "+DatabaseHelper.KEY_ALERTS_ADDRESS_ID
                              +" AND "+DatabaseHelper.KEY_ALERTS_ID+" = "+alertID+";";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor != null) {
            cursor.moveToFirst();
            return cursorToAlert(cursor); 
        }
        else {
            return null;
        }
    }


    public Alert readAlertByTitle(String Findtitle) {
        String selectQuery = "SELECT " + DatabaseHelper.KEY_ADDRESS_ID
                + "," + DatabaseHelper.KEY_ADDRESS_STREET
                + "," + DatabaseHelper.KEY_ADDRESS_CITY
                + "," + DatabaseHelper.KEY_ADDRESS_STATE
                + "," + DatabaseHelper.KEY_ADDRESS_ZIP_CODE
                + "," + DatabaseHelper.KEY_ADDRESS_LATITUDE
                + "," + DatabaseHelper.KEY_ADDRESS_LONGITUDE
                + "," + DatabaseHelper.KEY_ALERTS_ID
                + "," + DatabaseHelper.KEY_ALERTS_ADDRESS_ID
                + "," + DatabaseHelper.KEY_ALERTS_TITLE
                + "," + DatabaseHelper.KEY_ALERTS_DESCRIPTION
                + "," + DatabaseHelper.KEY_ALERTS_RADIUS
                + "," + DatabaseHelper.KEY_ALERTS_STATUS
                + " FROM " + DatabaseHelper.TABLE_ADDRESS
                + "," + DatabaseHelper.TABLE_ALERTS
                + " WHERE " + DatabaseHelper.KEY_ADDRESS_ID + " = " + DatabaseHelper.KEY_ALERTS_ADDRESS_ID
                + " AND " + DatabaseHelper.KEY_ALERTS_TITLE + " = " + Findtitle + ";";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursorToAlert(cursor);
        } else {
            return null;
        }
    }

        // CURSOR TO DATA STRUCTURE COMMANDS
    private Alert cursorToAlert(Cursor cursor) {
        Address address = new Address();
        Alert alert = new Alert();
        address.setAddressID(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ADDRESS_ID)));
        address.setAddressStreet(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ADDRESS_STREET)));
        address.setAddressCity(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ADDRESS_CITY)));
        address.setAddressState(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ADDRESS_STATE)));
        address.setAddressZipCode(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_ADDRESS_ZIP_CODE)));
        address.setAddressLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_ADDRESS_LATITUDE)));
        address.setAddressLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_ADDRESS_LONGITUDE)));
        alert.setAlertID(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ALERTS_ID)));
        alert.setAlertTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ALERTS_TITLE)));
        alert.setAlertDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ALERTS_DESCRIPTION)));
        alert.setAlertRadius(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_ALERTS_RADIUS)));
        alert.setAlertStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_ALERTS_STATUS)));
        alert.setAlertAddress(address);

       return alert;
    }

}