package cs442.com.ActionClass;

import android.app.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cs442.com.fragment.NewAlertFragment;
import cs442.com.Activity.MainActivity;
import cs442.com.fragment.NavigationDrawerFragment;
import cs442.com.pulse.R;

/**
 * Created by Sachin on 24-03-2015.
 */

public class HomeScreenButton extends MainActivity implements MainActivity.ButtonActions
{


    public void showPopup(final Activity context, final Marker marker,final FragmentManager fm)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int popupWidth =displaymetrics.widthPixels;
        Point p=new Point();
        p.x=10;
        p.y=50;
        final PopupWindow popup = new PopupWindow(context);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.popup_pulse);
        popup.setBackgroundDrawable(new BitmapDrawable(context.getResources(),b));
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup, viewGroup);

        TextView tv_title=(TextView)layout.findViewById(R.id.tv_marker_title);
        tv_title.setText("Name : "+marker.getTitle());

       TextView tv_snippet=(TextView)layout.findViewById(R.id.tv_marker_snippet);
       tv_snippet.setText("Time : "+marker.getSnippet());

        String completeAddess="";
        Geocoder geocoder;
        List<Address> addresses=null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size()>0){
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                completeAddess=""+address+", "+city+", "+state+", "+country+", "+postalCode;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        TextView tv_position=(TextView)layout.findViewById(R.id.tv_marker_position);
        tv_position.setText("Address : "+completeAddess);

        Button ok=(Button)layout.findViewById(R.id.button_popup);
        final String finalCompleteAddess = completeAddess;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();

                Bundle args = new Bundle();
                args.putLong(getResources().getString(R.string.alertId), -9999);
                args.putString(getResources().getString(R.string.title_sp),marker.getTitle());
                args.putString(getResources().getString(R.string.addresss_sp), finalCompleteAddess);
                args.putDouble(getResources().getString(R.string.latitude_sp), marker.getPosition().latitude);
                args.putDouble(getResources().getString(R.string.longitude_sp), marker.getPosition().longitude);
                Fragment addRemainder = new NewAlertFragment();
                addRemainder.setArguments(args);
                FragmentTransaction fragmentTransaction=  fm.beginTransaction();
                fragmentTransaction.replace(R.id.container, addRemainder);
                fragmentTransaction.commit();

        context.findViewById(R.id.HomeScreenMap_linearlayout).setVisibility(View.INVISIBLE);            }
        });
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.showAtLocation(layout, Gravity.CENTER, p.x, p.y);
    }
}
