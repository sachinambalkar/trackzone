package cs442.com.ActionClass;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sachin on 03-04-2015.
 */
public class SharedPreferenceAction
{
    String Facebook_ID=new String("facebook_id");
    public void save(Context context, String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(Facebook_ID, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString(Facebook_ID, text); //3
        editor.commit(); //4
    }

    public String getValue(Context context) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(Facebook_ID, Context.MODE_PRIVATE);
        text = settings.getString(Facebook_ID, "Guest");
        return text;
    }

/*
    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(Facebook_ID, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(Facebook_ID, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(Facebook_ID);
        editor.commit();
    }
*/
}
