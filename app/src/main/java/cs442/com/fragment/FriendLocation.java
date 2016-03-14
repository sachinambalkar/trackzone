package cs442.com.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import cs442.com.ActionClass.HomeScreenButton;
import cs442.com.ActionClass.MapHelper;
import cs442.com.ActionClass.TransparentProgressDialog;
import cs442.com.Activity.MainActivity;
import cs442.com.Adapter.FblistAdapter;
import cs442.com.pulse.R;
import static com.facebook.GraphRequest.executeBatchAsync;
/**
 * A simple {@link Fragment} subclass.
 */
public class FriendLocation extends Fragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    static View userInfoView=null;
    FblistAdapter fbAdapter;
    static    GoogleMap gmap;
    MainActivity.ButtonActions buttonActions;
    private MapHelper mhelper;
//    static    LinearLayout relativeLayout_hsv;
    private ListView  fb_listview;
    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;
    Boolean stopLoading=false;
    private CallbackManager callbackManager;
    AccessToken accessToken;
    private ArrayList<String> id;
    private int counter=0;
    private Firebase myFirebaseRef;
    String FB_USERID=null;
    String provider;
    Location location;
    LocationManager locationManager;
    Map<String, Object> globalRoot=null;
    ArrayList<String> globalsortedId=null;

    public FriendLocation() {
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
            view=inflater.inflate(R.layout.fragment_friend_location, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        setHasOptionsMenu(true);
        //if(gmap!=null)
        {
            gmap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.mapFacebook)).getMap();
            gmap.setMyLocationEnabled(true);
            gmap.getUiSettings().setMyLocationButtonEnabled(true);
            gmap.getUiSettings().setCompassEnabled(true);
            gmap.setOnInfoWindowClickListener((MainActivity) getActivity());
        }

        myFirebaseRef = new Firebase("https://pulsealert.firebaseio.com/");
        id=new ArrayList<>();

        callbackManager = CallbackManager.Factory.create();
        final com.facebook.login.widget.LoginButton loginButton = (com.facebook.login.widget.LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        stopLoading=true;

        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getActivity().getSystemService(svcName);

        provider=LocationManager.NETWORK_PROVIDER;
        location= locationManager.getLastKnownLocation(provider);
        if(location==null) {
            provider=LocationManager.GPS_PROVIDER;
            location= locationManager.getLastKnownLocation(provider);
        }
        if(location==null) {
            provider=LocationManager.PASSIVE_PROVIDER;
            location= locationManager.getLastKnownLocation(provider);
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(AccessToken.getCurrentAccessToken()==null)
                {
                    if(gmap!=null)
                        gmap.clear();

                    if(fb_listview!=null)
                    {
                        FB_USERID=null;
                        Map<String,Object> rootBlank=null;
                        ArrayList<String> idBlank=null;
                        fbAdapter = new FblistAdapter(getActivity(),rootBlank,idBlank);
                        fb_listview.setAdapter(fbAdapter);
                        hideListView();
                    }

/*
                    if(relativeLayout_hsv!=null)
                        relativeLayout_hsv.removeAllViews();
                        relativeLayout_hsv.removeAllViews();
                        relativeLayout_hsv.removeAllViews();
*/
                }
            }
        });



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(),"Logged In",Toast.LENGTH_LONG).show();
                stopLoading=false;
                pd.show();
                h.postDelayed(r,1000);
            }
            @Override
            public void onCancel() {
                Toast.makeText(getActivity(),"Canceled",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getActivity(),"Error while logging",Toast.LENGTH_LONG).show();
            }
        });


        com.facebook.ProfileTracker profileTracker;
        profileTracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                if(profile2!=null)
                {
                    try {
                        ((MainActivity) getActivity()).setUsername(profile2.getFirstName() + " " + profile2.getLastName());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };

        buttonActions = new HomeScreenButton();
        mhelper=new MapHelper();
        Button button_detail=(Button)view.findViewById(R.id.fb_buttonDetails);
        button_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mhelper.getStatus()) {
                    Marker marker=mhelper.getMarker();
                    buttonActions.showPopup(getActivity(), marker, getActivity().getSupportFragmentManager());
                }
                else
                    Toast.makeText(getActivity(),"Tab on friend name first",Toast.LENGTH_SHORT).show();
            }
        });
        Button button_getDirection=(Button)view.findViewById(R.id.fb_Button_GetDirection);
        button_getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mhelper.getStatus()){
                    Marker marker=mhelper.getMarker();
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%f,%f (%s)",marker.getPosition().latitude,marker.getPosition().longitude, 8,marker.getPosition().latitude,marker.getPosition().longitude,marker.getTitle()+"' Location");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    getActivity().startActivity(intent);
                }
                else
                    Toast.makeText(getActivity(),"Tab on friend name first",Toast.LENGTH_SHORT).show();

            }
        });

        h = new Handler();
        pd = new TransparentProgressDialog(getActivity(), R.drawable.loadouter,R.drawable.loadinner);
        r =new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(),"called",Toast.LENGTH_SHORT).show();
                if (pd.isShowing()) {

                    if(stopLoading)
                        pd.dismiss();
                    else
                        h.postDelayed(r, 1000);
                }
            }
        };
        //           findViewById(R.id.button1).setOnClickListener(this);
        pd.show();
        h.postDelayed(r,1000);


        hideListView();
        return  view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try{
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Locate Friend");
    }catch (Exception e){
        e.printStackTrace();
    }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
       accessToken= AccessToken.getCurrentAccessToken();
        try {
            FB_USERID = accessToken.getUserId();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(getString(R.string.facebook_Alert_TitleName),FB_USERID).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            myNewfriend();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(FB_USERID==null)
            hideListView();

    }

  public void myNewfriend() throws Exception
    {
        GraphRequest requestNew = GraphRequest.newGraphPathRequest(
                accessToken,"me/friends", null);
        requestNew.setCallback(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                //requestCompleted(response);
                try {
                    addResultsNew(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //id,name,link,
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        requestNew.setParameters(parameters);
        requestNew.executeAsync();
    }

    private void addResultsNew(GraphResponse response) throws Exception
    {
        JSONArray data=null;
        GraphRequest nextRequest;

        if(response!=null&&response.getJSONObject()!=null)
            data = response.getJSONObject().optJSONArray("data");
        boolean haveData=false;
        if(data!=null) {
            addRecord(data);
            haveData = data.length() > 0;
        }

        if (haveData)
            nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
        else
            nextRequest = null;

        if(nextRequest!=null)
        {
            nextRequest.setCallback(new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    //requestCompleted(response);
                    try {
                        addResultsNew(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            executeBatchAsync(new GraphRequestBatch(nextRequest));
        }
        else
            if (counter > 0) {
                callFireBaseRetrieveService();

            }

    }


/*

    public class setDP extends AsyncTask<Void,Bitmap,Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Void... bm)
        {
            URL img_value = null;
            Bitmap mIcon1=null;
            try {
                img_value = new URL("http://graph.facebook.com/"+facebookID+"/picture");
                //  mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                mIcon1 = BitmapFactory.decodeStream((InputStream) new URL(img_value.toString()).getContent());
                return mIcon1;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);
            ImageView user_picture= (ImageView)findViewById(R.id.imageViewProfile);
            user_picture.setImageBitmap(bitmap);
        }
    }
    */


    public void callFireBaseRetrieveService() throws Exception
    {
//        for(int i=0;i<counter;i++)
            FireBaseRetriveData(id);
    }
    public void addRecord(JSONArray data) throws Exception
    {
        try
        {
            for(int i=0;i<data.length();i++)
            {
                id.add(counter, data.getJSONObject(i).getString("id"));

                counter++;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void FireBaseRetriveData(final ArrayList id) throws  Exception
    {
        myFirebaseRef.child("/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
               try {
                    fb_listview=(ListView)getActivity().findViewById(R.id.listview_fblist);
                    Map<String, Object> root = (Map<String,Object>) snapshot.getValue();
                    gmap.clear();

                   Map<String,String> mapValue=new HashMap<String, String>();
                   Map<String, Object> newPost;
                   String[] arrayUser = new String[id.size()];
                   int j=0;
                   for (int p = 0; p < id.size(); p++,j++) {
                       if((Map<String, Object>) root.get(id.get(p))!=null)
                       {
                           mapValue.put(((Map<String, Object>) root.get(id.get(p))).get("NAME").toString(), id.get(p).toString());
                           arrayUser[j]=((Map<String, Object>) root.get(id.get(p))).get("NAME").toString();
                       }
                       else
                               j--;
                   }
                   final int sizeArray=j;

                   String[] sortedArray= Arrays.copyOf(arrayUser,sizeArray);
                   Arrays.sort(sortedArray);

                   Map<String, Object> finalList[]=new Map[sizeArray];
                   ArrayList<String> sortedId=new ArrayList<String>();
                   for(int i=0;i<sizeArray;i++){
                       sortedId.add(mapValue.get(sortedArray[i]).toString());
                   }

                   globalRoot = root;
                   globalsortedId = sortedId;
                   fbAdapter = new FblistAdapter(getActivity(), root, sortedId);
                   fb_listview.setAdapter(fbAdapter);
                   stopLoading=true;

                }catch(Exception e)
                {
                 e.printStackTrace();
                }

                try {
                    PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, true);
                    SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if (setNoti.getBoolean("fb_guide", true))
                        showActivityOverlay();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    private void showActivityOverlay() throws Exception
    {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, true);
        SharedPreferences setNoti = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setNoti.edit().putBoolean("fb_guide", false).commit();

        LinearLayout layout;
        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_view_alert);
        layout = (LinearLayout) dialog.findViewById(R.id.overlay_view_alert_id);
        ImageView imgv=(ImageView)dialog.findViewById(R.id.ivOverlayActivity);
        imgv.setImageDrawable(getResources().getDrawable(R.drawable.fb_overlay));
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(AccessToken.getCurrentAccessToken()!=null)
        {
            accessToken=AccessToken.getCurrentAccessToken();
            try {
                FB_USERID = accessToken.getUserId();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                myNewfriend();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            if(gmap!=null)
            gmap.clear();
  //          listVfb.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fb_menu,menu);
        SearchManager searchManager      = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView)searchMenuItem.getActionView();
        SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem,this);
    }



    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        if(fbAdapter!=null)
        fbAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(fbAdapter!=null)
        fbAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        //Toast.makeText(getActivity(),"Exapnacde",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        fbAdapter = new FblistAdapter(getActivity(), globalRoot, globalsortedId);
        fb_listview.setAdapter(fbAdapter);
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();

        hideListView();

    }

    public void hideListView(){
        if(FB_USERID==null)
        {
            TextView tv=new TextView(getActivity());
            tv.setText("NO Fb is set");
            LinearLayout ll =(LinearLayout)view.findViewById(R.id.friend_location_layout);
            ListView lv=(ListView)view.findViewById(R.id.listview_fblist);
            lv.setVisibility(View.GONE);
            // ll.addView(tv);
            if(userInfoView==null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                userInfoView = inflater.inflate(R.layout.userinfo_layout, null, false);
                ll.addView(userInfoView);
            }
            else
                userInfoView.setVisibility(View.VISIBLE);
        }
        else
        {
            ListView lv=(ListView)view.findViewById(R.id.listview_fblist);
            lv.setVisibility(View.VISIBLE);
            if(userInfoView!=null)
            userInfoView.setVisibility(View.GONE);
        }
    }

}
