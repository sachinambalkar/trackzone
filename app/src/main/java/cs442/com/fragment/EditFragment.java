package cs442.com.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import cs442.com.Activity.MainActivity;
import cs442.com.database.Alert;
import cs442.com.pulse.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {
    Long alertId;
    GoogleMap gmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Edit Alert");
    }catch (Exception e){
        e.printStackTrace();
    }

}


    public EditFragment() throws Exception{
        // Required empty public constructor
    }

    private static View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view=inflater.inflate(R.layout.fragment_edit, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


        try {
            gmap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapEdit)).getMap();
            gmap.setMyLocationEnabled(true);
            gmap.getUiSettings().setMyLocationButtonEnabled(true);
            gmap.getUiSettings().setCompassEnabled(true);
            gmap.setOnInfoWindowClickListener((MainActivity) getActivity());
        }catch (Exception e){
        e.printStackTrace();
        }

        alertId=getArguments().getLong("alertID");

        Alert alertData= MainActivity.datasource.readAlertByID(alertId+1);



        TextView tv_alertTitle=(TextView)view.findViewById(R.id.tv_alertTitle);
        TextView tv_alertDescription=(TextView)view.findViewById(R.id.tv_alertDescription);
        TextView tv_alertRadius=(TextView)view.findViewById(R.id.tv_alertRadius);
        TextView tv_alertStatus=(TextView)view.findViewById(R.id.tv_alertStatus);

        tv_alertTitle.setText("Title: "+alertData.getAlertTitle());
        tv_alertRadius.setText("Radius: "+alertData.getAlertRadius());
        tv_alertDescription.setText("Description:\n "+alertData.getAlertDescription());
        tv_alertStatus.setText("Status: "+alertData.getAlertStatus());

        ImageButton edit=(ImageButton)view.findViewById(R.id.b_editAction);
        ImageButton delete=(ImageButton)view.findViewById(R.id.b_deleteAction);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        LatLng currentPosition = new LatLng(alertData.getAlertAddress().getAddressLatitude(),alertData.getAlertAddress().getAddressLongitude());
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 6));
        gmap.addMarker(new MarkerOptions().position(currentPosition).snippet("").title(alertData.getAlertTitle()));

        return view;
    }


    public void editAction() throws Exception
    {
        Fragment mfrag=new NewAlertFragment();
        Bundle data=new Bundle();
        data.putLong("alertID", alertId);
        mfrag.setArguments(data);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container,mfrag)
                .commit();
    }

    public void deleteAction() throws Exception
    {
        showPopup(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();

    }



    public void showPopup(final Activity context) throws Exception
    {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int popupWidth =displaymetrics.widthPixels;
        int popupHeight = (displaymetrics.heightPixels)/5;
        Point p=new Point();
        p.x=30;
        p.y=popupHeight;
        final PopupWindow popup = new PopupWindow(context);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.popup_pulse);
        popup.setBackgroundDrawable(new BitmapDrawable(context.getResources(),b));
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popupDelete);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_delete, viewGroup);


        Button ok=(Button)layout.findViewById(R.id.popupbutton_delete_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                MainActivity.datasource.deleteAlert(alertId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ViewAllAlertsFragment())
                        .commit();
            }
        });

        Button cancel=(Button)layout.findViewById(R.id.popupbutton_delete_cancel);
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

}

