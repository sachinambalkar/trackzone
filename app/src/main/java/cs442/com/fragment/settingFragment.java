package cs442.com.fragment;


import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import cs442.com.pulse.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class settingFragment extends Fragment {


    public settingFragment() {
        // Required empty public constructor
    }


    public static View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view=inflater.inflate(R.layout.fragment_setting, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }


        Button alertTone=(Button)view.findViewById(R.id.ButtonalertTone);
        alertTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeAlertTone();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Button datasetting=(Button)view.findViewById(R.id.ButtonDataSetting);
        datasetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeDataSetting();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button locationsetting=(Button)view.findViewById(R.id.ButtonLocaitonSetting);
        locationsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeLocationSetting();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

/*
        Button modesetting=(Button)view.findViewById(R.id.ButtonMode);
        modesetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeSetting(true);
            }
        });


        Button modesettingFreq=(Button)view.findViewById(R.id.ButtonalertToneFrequency);
        modesettingFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeSetting(false);
            }
        });
*/


        return view;
    }

/*
    public void modeSetting(Boolean type)
    {
     showPopup(getActivity(),type);
    }


    public void showPopup(final Activity context, final boolean fbupdate)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int popupWidth =displaymetrics.widthPixels-60;
        final int popupHeight = (displaymetrics.heightPixels)/3;
        final Point p=new Point();
        p.x=30;
        p.y=popupHeight;
        final PopupWindow popup = new PopupWindow(context);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.popup_pulse);
        popup.setBackgroundDrawable(new BitmapDrawable(context.getResources(),b));
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup_mode);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popupmode, viewGroup);


        Button ok=(Button)layout.findViewById(R.id.popupbutton_mode_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
               String inputRange = ((Spinner) layout.findViewById(R.id.SpinnerModeRange)).getSelectedItem().toString();

                if(fbupdate)
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("updateTime",Integer.parseInt(inputRange)).commit();
                else
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("updateTimeFrequency",Integer.parseInt(inputRange)).commit();
           }
        });

        Button cancel=(Button)layout.findViewById(R.id.popupbutton_mode_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);
    }
*/




    public void changeAlertTone() throws Exception
    {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, 5);

    }

    public void changeDataSetting() throws Exception
    {
        Intent myIntent = new Intent( Settings.ACTION_WIFI_SETTINGS);
        getActivity().startActivity(myIntent);
    }

    public void changeLocationSetting() throws Exception
    {
        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        getActivity().startActivity(myIntent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        String chosenRingtone;
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null)
            {
                chosenRingtone = uri.toString();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("ringtone", chosenRingtone).commit();
            }
        }

    }
}
