package cs442.com.fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import cs442.com.ActionClass.GooglePlacesAutocompleteAdapter;
import cs442.com.Activity.MainActivity;
import cs442.com.database.Address;
import cs442.com.database.Alert;
import cs442.com.pulse.R;
public class NewAlertFragment extends Fragment implements AdapterView.OnItemClickListener,MainActivity.SampleAlert
{
    public static View rootView;
    public Button  saveBtn, cancelBtn;
    public Button curLocBtn;
    String inputName, inputDesc, inputRange, inputStreet, inputCity, inputState = "";
    int inputRangeValue, inputZipCode = 0;
    double inputLatitude, inputLongitude = 0;
    Long alertId=null;
    Alert alertData=null;
    String geocoderLocation;
    final int ADDRESS_PARAMS = 4;
    final int INTSTREET=1;
    final int INTCITY =2;
    final int INTSTATE =3;
    final int INTZIP =4;
    public NewAlertFragment(){}
    EditText etv_alertTitle;
    AutoCompleteTextView etv_alertAddress;
    EditText etv_lat;
    EditText etv_log;
    HoloCircleSeekBar holoCircleSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) throws RuntimeException
    {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_add_alert_new, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }
        etv_alertTitle=(EditText)rootView.findViewById(R.id.etv_alertTitle);
        etv_alertAddress=(AutoCompleteTextView)rootView.findViewById(R.id.etv_alertAddress);
        Button imageButtonMap=(Button)rootView.findViewById(R.id.editBtnMap);
        etv_lat=(EditText)rootView.findViewById(R.id.editLat);
        etv_log=(EditText)rootView.findViewById(R.id.editLong);

        if(getArguments()!=null) {
            try {
                String addressAlert;
                if(getArguments().get("addresss_sp").toString()!=null)
                    addressAlert=getArguments().get("addresss_sp").toString();
                else
                    addressAlert="Error";

                double lat=getArguments().getDouble("latitude_sp");
                double lng=getArguments().getDouble("longitude_sp");
                etv_alertAddress.setText(addressAlert);
                etv_lat.setText(String.valueOf(lat));
                etv_log.setText(String.valueOf(lng));

                if((getArguments().get("title_sp")!=null)&&(getArguments().get("title_sp")!=""))
                {
                    String title=getArguments().get("title_sp").toString();
                    etv_alertTitle.setText(title + "'s Location");
                }
            } catch (Exception e) {
                etv_alertAddress.setText("");
                etv_lat.setText("");
                etv_log.setText("");
            }

        }


        try {

         alertId = getArguments().getLong("alertID");
            if (alertId==-9999)
                alertId=null;
       }catch(Exception e){
           alertId=null;
       }

        if(alertId!=null)
        {
            alertData = MainActivity.datasource.readAlertByID(alertId);
            etv_alertTitle.setText(alertData.getAlertTitle());
            etv_alertAddress.setText(alertData.getAlertDescription());
            etv_lat.setText(""+alertData.getAlertAddress().getAddressLatitude());
            etv_log.setText(""+alertData.getAlertAddress().getAddressLongitude());
        }else{
            if(getArguments()!=null)
            if(getArguments().getString("title")!=null)
            {
//                        ((EditText) rootView.findViewById(R.id.editDescription)).setText(ss);
                etv_alertTitle.setText(getArguments().getString("title"));
                etv_lat.setText(""+getArguments().getDouble("latitude"));
                etv_log.setText(""+getArguments().getDouble("longitude"));
            }
        }

        curLocBtn = (Button)rootView.findViewById(R.id.buttonLocation);
        curLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getCurrentAddress(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        saveBtn = (Button)rootView.findViewById(R.id.buttonSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateAlert(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((MainActivity)getActivity()).load_all_locations();
                Toast.makeText(getActivity(),"Alert Saved",Toast.LENGTH_SHORT).show();
             //   ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("View Alert");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new ViewAllAlertsFragment()).commit();

//

            }
        });

        cancelBtn = (Button)rootView.findViewById(R.id.buttonCancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ViewAllAlertsFragment())
                        .commit();

            }
        });

        etv_alertAddress.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        etv_alertAddress.setOnItemClickListener(this);
        imageButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container, new MapFragment_SelectPlace()).commit();
//                fragmentManager.beginTransaction().replace(R.id.container,new NewAlertFragment()).commit();
            }
        });

        holoCircleSeekBar=(HoloCircleSeekBar)rootView.findViewById(R.id.seekBar_HoloCircle);
        int value=holoCircleSeekBar.getValue();

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try{
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("New Alert");
        }catch (Exception e){
            e.printStackTrace();
        }

        PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, true);
        SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (setNoti.getBoolean("a_alert", true))
            try {
                showActivityOverlay();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    private void showActivityOverlay() throws Exception
    {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, true);
        SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setNoti.edit().putBoolean("a_alert", false).commit();

        LinearLayout layout;
        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_view_alert);
        layout = (LinearLayout) dialog.findViewById(R.id.overlay_view_alert_id);
        ImageView imgv=(ImageView)dialog.findViewById(R.id.ivOverlayActivity);
        imgv.setImageDrawable(getResources().getDrawable(R.drawable.a_alert));
//            layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    public void getCurrentAddress(Activity activity) throws Exception
    {
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)activity.getSystemService(svcName);
        String provider;
        Location location;

            provider = LocationManager.GPS_PROVIDER;
            location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                provider = LocationManager.NETWORK_PROVIDER;
                location = locationManager.getLastKnownLocation(provider);
            }
            if (location == null) {
                provider = LocationManager.PASSIVE_PROVIDER;
                location = locationManager.getLastKnownLocation(provider);
            }

        Context context = activity.getApplicationContext();
        if (location != null){
            inputLatitude = location.getLatitude();
            inputLongitude = location.getLongitude();
        }
        if(inputLatitude == 0 &&  inputLongitude == 0){
            Toast.makeText(context,"Please make sure that the location service is ON", Toast.LENGTH_LONG).show();
        }
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<android.location.Address> listAddresses = geocoder.getFromLocation(inputLatitude, inputLongitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                geocoderLocation = listAddresses.get(0).getAddressLine(0);
                int iLoop=1;
                String sValue;
                while(iLoop <= ADDRESS_PARAMS){
                    switch(iLoop) {
                        case INTSTREET:
                            sValue = listAddresses.get(0).getAddressLine(0);
                            createAddress(sValue, INTSTREET);
                            break;

                        case INTCITY:
                            sValue = listAddresses.get(0).getLocality();
                            createAddress(sValue, INTCITY);
                            break;

                        case INTSTATE:
                            sValue = listAddresses.get(0).getAdminArea();
                            createAddress(sValue, INTSTATE);
                            break;

                        case INTZIP:
                            sValue = listAddresses.get(0).getPostalCode();
                            createAddress(sValue, INTZIP);
                            break;
                    }
                    iLoop++;
                }
                geocoderLocation = inputStreet+","+inputCity+","+inputState+","+inputZipCode;
                //System.out.println("New Location " +currentLocation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((EditText)rootView.findViewById(R.id.editLat)).setText(String.valueOf(inputLatitude));
        ((EditText)rootView.findViewById(R.id.editLong)).setText(String.valueOf(inputLongitude));
        etv_alertAddress.setText(geocoderLocation);
        etv_alertTitle.setText("My Current Location \n( " + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + " )");
    }
    public void validateAlert(Activity activity) throws Exception {
        Alert alert = new Alert();
        Address address = new Address();

        boolean bContinue = true;
        while (bContinue) {
            inputName = etv_alertTitle.getText().toString();
            //No name for the alert
            if (inputName.isEmpty()) {
                Toast.makeText(activity.getApplicationContext(), "Please enter a name for the alert", Toast.LENGTH_LONG).show();
                break;
            }

            inputDesc = etv_alertAddress.getText().toString();
           // inputRange = ((Spinner) rootView.findViewById(R.id.SpinnerRange)).getSelectedItem().toString();
            inputRange=String.valueOf(holoCircleSeekBar.getValue());
            try {
                inputLatitude = Double.valueOf(etv_lat.getText().toString());
                inputLongitude = Double.valueOf(etv_log.getText().toString());
            } catch (Exception e) {
                inputLatitude = 0;
                inputLongitude = 0;
            }
            try {
                inputRangeValue = Integer.valueOf(inputRange);
            } catch (Exception e) {
                inputRangeValue = 1;
            }

            //No location for the alert
            if (inputLatitude == 0 && inputLongitude == 0) {
                Toast.makeText(activity.getApplicationContext(), "Please enter a valid location for the alert", Toast.LENGTH_LONG).show();
                break;
            }

            address.setAddressStreet(inputStreet);
            address.setAddressCity(inputCity);
            address.setAddressState(inputState);
            address.setAddressZipCode(inputZipCode);
            address.setAddressLatitude(inputLatitude);
            address.setAddressLongitude(inputLongitude);
            alert.setAlertTitle(inputName);
            alert.setAlertDescription(inputDesc);
            alert.setAlertRadius(inputRangeValue);
            alert.setAlertStatus(1);
            alert.setAlertAddress(address);
            createAlert(alert);
            break;
        }
    }
    public void createAlert(Alert alert) throws Exception
    {
        long AlertIdCreated;
      //  Toast.makeText(getActivity(), alert.getAlertTitle(), Toast.LENGTH_LONG).show();
        if(alertId==null)
        {
            AlertIdCreated=MainActivity.datasource.createBoth(alert.getAlertAddress().getAddressStreet()
                    , alert.getAlertAddress().getAddressCity()
                    , alert.getAlertAddress().getAddressState()
                    , alert.getAlertAddress().getAddressZipCode()
                    , alert.getAlertAddress().getAddressLatitude()
                    , alert.getAlertAddress().getAddressLongitude()
                    , alert.getAlertTitle()
                    , alert.getAlertDescription()
                    , alert.getAlertRadius()
                    , alert.getAlertStatus());
            if(AlertIdCreated!=-1)
                Toast.makeText(getActivity(),"Alert saved",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(),"Alert named "+alert.getAlertTitle()+" already exists"+"",Toast.LENGTH_SHORT).show();
        }
        else {
            MainActivity.datasource.updateBoth(alertData.getAlertAddress().getAddressID(),
                    alert.getAlertAddress().getAddressStreet()
                    , alert.getAlertAddress().getAddressCity()
                    , alert.getAlertAddress().getAddressState()
                    , alert.getAlertAddress().getAddressZipCode()
                    , alert.getAlertAddress().getAddressLatitude()
                    , alert.getAlertAddress().getAddressLongitude()
                    , alertId, alert.getAlertTitle()
                    , alert.getAlertDescription()
                    , alert.getAlertRadius()
                    , alert.getAlertStatus());
        }

    }
    @Override
    public void onItemClick(AdapterView adapterView, View view, int position, long id)
    {
        String str = (String) adapterView.getItemAtPosition(position);
        //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List addressList = geocoder.getFromLocationName(str, 1);
            if (addressList != null && addressList.size() > 0)
            {
                android.location.Address address = (android.location.Address) addressList.get(0);
                etv_lat.setText(String.valueOf(address.getLatitude()));
                etv_log.setText(String.valueOf(address.getLongitude()));
            }
        } catch (IOException e) {
            Log.e("Address", "Unable to connect to Geocoder", e);
    }

}
       public void createAddress(String sValue, int field) throws Exception
       {
        switch(field){

            case INTSTREET:
                if(sValue!=null)
                    inputStreet = sValue;
                else
                    inputStreet="";
                break;

            case INTCITY:
                if(sValue!=null)
                    inputCity= sValue;
                else
                    inputCity="";

                break;

            case INTSTATE:
                if(sValue!=null)
                    inputState = sValue;
                else
                    inputState="";

                break;

            case INTZIP:
                if(sValue!=null)
                    inputZipCode = Integer.valueOf(sValue);
                else
                    inputZipCode=0;

                break;
        }

    }

    @Override
    public void createOneSampleAlert(Activity activity) {
        try {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView =inflater.inflate(R.layout.fragment_add_alert_new, null);
            getCurrentAddress(activity);
            validateAlert(activity);
            ((MainActivity)activity).load_all_locations();
            rootView=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
