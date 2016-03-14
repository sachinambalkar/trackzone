package cs442.com.fragment;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.login.widget.ProfilePictureView;
import cs442.com.Activity.MainActivity;
import cs442.com.pulse.R;
import cs442.com.service.NotificationService;
public class NavigationDrawerFragment extends Fragment {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private int mCurrentSelectedPosition = 0;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private LinearLayout linear_map_layout;
    private ProfilePictureView profilePictureView;
    AccessToken accessTokenDrawer;
    TextView username_tv;
    public NavigationDrawerFragment() {}

/*** OVERRIDDEN FUNCTIONS ***/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessTokenDrawer = AccessToken.getCurrentAccessToken();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        username_tv = (TextView)v.findViewById(R.id.username_TV_drawer);
        username_tv.setText(((MainActivity)getActivity()).getUsername());
        profilePictureView=(ProfilePictureView)v.findViewById(R.id.profilePicture_Drawer);

        if(accessTokenDrawer !=null){
            profilePictureView.setProfileId(accessTokenDrawer.getUserId());        
        }

        mDrawerListView = (ListView)v.findViewById(R.id.listview_drawer);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(new ArrayAdapter<>(
            getActionBar().getThemedContext(),
            android.R.layout.simple_list_item_activated_1,
            android.R.id.text1,
            new String[]{
            getString(R.string.title_section1),
            getString(R.string.title_section2),
            getString(R.string.title_section3),
            getString(R.string.title_section4),
                    getString(R.string.title_section5),
            getString(R.string.title_section6),
                    getString(R.string.title_section7),

        }));

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        accessTokenDrawer= AccessToken.getCurrentAccessToken();
        
        if(accessTokenDrawer !=null&&profilePictureView!=null){
            username_tv.setText(((MainActivity) getActivity()).getUsername());
            profilePictureView.setProfileId(accessTokenDrawer.getUserId());}
        else if(accessTokenDrawer ==null&&profilePictureView!=null) {
            profilePictureView.setProfileId(null);
            username_tv.setText("Guest");
        }

        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }

        Animation an,an2;
        an = AnimationUtils.loadAnimation(getActivity(), R.anim.dp_animation);
        getActivity().findViewById(R.id.profilePicture_Drawer).startAnimation(an);
        an2 = AnimationUtils.loadAnimation(getActivity(), R.anim.username_animation);
        getActivity().findViewById(R.id.username_TV_drawer).startAnimation(an2);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.notification_item)
        {
            getActivity().stopService(new Intent(getActivity(), NotificationService.class));
            Toast.makeText(getActivity(),"Notification snoozed",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


/*** CUSTOM FUNCTIONS ***/
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, LinearLayout layout_map) {
        linear_map_layout = layout_map;
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
        getActivity(),                    /* host Activity */
        mDrawerLayout,                    /* DrawerLayout object */
        R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
        R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
        R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu();

                switch (mCurrentSelectedPosition) {
                    case 0:
                        actionBar.setTitle(R.string.title_section1);
                        break;
                    case 1:
                        actionBar.setTitle(R.string.title_section2);
                        break;
                    case 2:
                        actionBar.setTitle(R.string.title_section3);
                        break;
                    case 3:
                        actionBar.setTitle(R.string.title_section4);
                        break;
                    case 4:
                        actionBar.setTitle(R.string.title_section5);
                        break;
                    case 5:
                        actionBar.setTitle(R.string.title_section6);
                        break;
                    case 6:
                        actionBar.setTitle(R.string.title_section7);
                        break;
                    default:
                        actionBar.setTitle(R.string.title_section1);
                        break;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        switch (position) {
            case 0:
                actionBar.setTitle(R.string.title_section1);
////                if(homef==null)
    //                homef=new HomeFragment();

                fragmentManager.beginTransaction().replace(R.id.container, new AlertNotificationFragment()).commit();
                break;
            case 1:
                actionBar.setTitle(R.string.title_section2);
                fragmentManager.beginTransaction().replace(R.id.container,new NewAlertFragment()).commit();
                break;
            case 2:
                actionBar.setTitle(R.string.title_section3);
                fragmentManager.beginTransaction().replace(R.id.container,new ViewAllAlertsFragment()).commit();
                break;
            case 3:
                actionBar.setTitle(R.string.title_section4);
                fragmentManager.beginTransaction().replace(R.id.container,new FriendLocation()).commit();
                break;
            case 4:
                actionBar.setTitle(R.string.title_section5);
                fragmentManager.beginTransaction().replace(R.id.container,new settingFragment()).commit();

                break;

            case 5:
                actionBar.setTitle(R.string.title_section6);
                fragmentManager.beginTransaction().replace(R.id.container,new HelpFragment()).commit();

                break;
            case 6:
                actionBar.setTitle(R.string.title_section7);
                fragmentManager.beginTransaction().replace(R.id.container,new AboutFragment()).commit();

                break;

            default:
                actionBar.setTitle(R.string.title_section1);
                fragmentManager.beginTransaction().replace(R.id.container, new AlertNotificationFragment()).commit();
  ///              ((MainActivity)getActivity()).clearMap();

//                Toast.makeText(getActivity(), "Navigation Drawer Tracker", Toast.LENGTH_SHORT).show();
//                fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
                break;
        }
    }

    public void hideMap() {
        linear_map_layout.setVisibility(View.INVISIBLE);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
 //       actionBar.setBackgroundDrawable(new ColorDrawable(0x2f477a));
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    }


