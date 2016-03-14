package cs442.com.fragment;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cs442.com.pulse.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment_SelectPlace extends Fragment {


    public MapFragment_SelectPlace() {
        // Required empty public constructor
    }

    static View view;
    String para_address;
    double para_latitude=9999;
    double para_longitude=9999;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view=inflater.inflate(R.layout.fragment_map_fragment__select_place, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }
        final ImageButton imageButton=(ImageButton)view.findViewById(R.id.ImageButton_selectLocation);
        final TextView selectedAddressTV=(TextView)view.findViewById(R.id.TextViewSetLocation);
        final GoogleMap gmap;//=(GoogleMap)popupView.findViewById(R.id.mapPopup);
        gmap = ((com.google.android.gms.maps.MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.mapSelectPlace)).getMap();
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.getUiSettings().setCompassEnabled(true);
        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(latLng);
                String completeAddess ="";
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
               // if(ad)
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if(addresses.size()>0){
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                        if(address!=null)
                        completeAddess=""+address;
                        else
                        completeAddess="";
                        if(city!=null)
                        completeAddess=completeAddess+", "+city;
                        if(state!=null)
                            completeAddess=completeAddess+", "+state;
                        if(country!=null)
                            completeAddess=completeAddess+", "+country;
                         if(postalCode!=null)
                             completeAddess=completeAddess+", "+postalCode;



                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
                markerOptions.title(completeAddess);
                // Clears the previously touched position
                gmap.clear();
                // Animating to the touched position
                gmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                // Placing a marker on the touched position
                gmap.addMarker(markerOptions);

                para_address=completeAddess;
                para_latitude=latLng.latitude;
                para_longitude=latLng.longitude;

                selectedAddressTV.setText(completeAddess);
                //selectedAddressTV.setVisibility(View.VISIBLE);
                //imageButton.setVisibility(View.VISIBLE);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if((para_address!=null)&&para_longitude!=9999&&para_latitude!=9999) {

                    NewAlertFragment alertFrag = new NewAlertFragment();
                    Bundle args = new Bundle();
                    args.putLong("alertID", -9999);
                    args.putString("addresss_sp", para_address);
                    args.putDouble("latitude_sp", para_latitude);
                    args.putDouble("longitude_sp", para_longitude);
                    alertFrag.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.container, alertFrag).commit();
                    gmap.clear();
                    selectedAddressTV.setText("");
                }
                else
                    Toast.makeText(getActivity(),"Please select location...!!!",Toast.LENGTH_SHORT).show();
//                selectedAddressTV.setVisibility(View.INVISIBLE);
  //              imageButton.setVisibility(View.INVISIBLE);

            }
        });

        return view;
    }


}
