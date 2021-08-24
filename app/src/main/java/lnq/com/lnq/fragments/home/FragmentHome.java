package lnq.com.lnq.fragments.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.adapters.MentionChatAdapter;
import lnq.com.lnq.adapters.SearchUsersInMapAdapter;
import lnq.com.lnq.api.Api;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.common.StringMethods;
import lnq.com.lnq.databinding.FragmentHomeBinding;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.model.MentionModel;
import lnq.com.lnq.model.event_bus_models.EventBusChangeMapTypes;
import lnq.com.lnq.model.event_bus_models.EventBusCloseMapProfile;
import lnq.com.lnq.model.event_bus_models.EventBusGridUsersList;
import lnq.com.lnq.model.event_bus_models.EventBusIsFragmentVisible;
import lnq.com.lnq.model.event_bus_models.EventBusLocationPermission;
import lnq.com.lnq.model.event_bus_models.EventBusMapGridViewChanged;
import lnq.com.lnq.model.event_bus_models.EventBusMapLocationUpdate;
import lnq.com.lnq.model.event_bus_models.EventBusMapMarkerChange;
import lnq.com.lnq.model.event_bus_models.EventBusMapProfileClick;
import lnq.com.lnq.model.event_bus_models.EventBusMapSearchTextUpdate;
import lnq.com.lnq.model.event_bus_models.EventBusProfileClose;
import lnq.com.lnq.model.event_bus_models.EventBusSendUsersList;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateFilters;
import lnq.com.lnq.model.event_bus_models.EventBusUpdateUserStatus;
import lnq.com.lnq.model.event_bus_models.EventBusUserMapSearch;
import lnq.com.lnq.model.event_bus_models.EventBusUserSession;
import lnq.com.lnq.model.event_bus_models.EventBusUserZoomLevel;
import lnq.com.lnq.model.gson_converter_models.LogOut;
import lnq.com.lnq.model.gson_converter_models.SearchUserInMapModel;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationMainObject;
import lnq.com.lnq.model.gson_converter_models.location.UserWithOutRadiusData;
import lnq.com.lnq.model.gson_converter_models.location.UserWithOutRadiusMainObject;
import lnq.com.lnq.model.gson_converter_models.pushnotifications.PushNotificationMainObject;
import lnq.com.lnq.model.gson_converter_models.registerandlogin.RegisterLoginMainObject;
import lnq.com.lnq.model.gson_converter_models.visibilitysettings.SetVisibilityMainObject;
import lnq.com.lnq.model.userprofile.GetUserProfileMainObject;
import lnq.com.lnq.roomdatabase.MultiProfileRepositry;
import lnq.com.lnq.roomdatabase.MultiProfileRoomModel;
import lnq.com.lnq.utils.ClusterRendererUtils;
import lnq.com.lnq.utils.FontUtils;
import lnq.com.lnq.utils.RegionUtils;
import lnq.com.lnq.utils.ValidUtils;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.SENSOR_SERVICE;
import static lnq.com.lnq.utils.MapUtils.animateMarker;
import static lnq.com.lnq.utils.MapUtils.getMapVisibleRegionRadius;
import static lnq.com.lnq.utils.MapUtils.getZoomLevel;

public class FragmentHome extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        ClusterManager.OnClusterClickListener<UpdateLocationData>,
        ClusterManager.OnClusterItemInfoWindowClickListener<UpdateLocationData>,
        ClusterManager.OnClusterItemClickListener<UpdateLocationData>,
        TextWatcher,
        TextView.OnEditorActionListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveStartedListener,
        CompassSensorEventListener.SensorChangeEvent,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    //    Constant fields....
    private static final String TAG = "FragmentHome";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4;

    //    Retrofit fields...
    private Call<UpdateLocationMainObject> callUpdateLocation;
    private Call<UserWithOutRadiusMainObject> callUserWithOutRadius;
    private Call<PushNotificationMainObject> callNotifications;
    private Call<SetVisibilityMainObject> callSetVisibility;
    private Call<RegisterLoginMainObject> callUnFrozen;
    private Call<GetUserProfileMainObject> callGetUserProfile;
    private Call<LogOut> callLogOut;

    //    Android fields....
    private FragmentHomeBinding homeBinding;
    private HomeClickHandler clickHandler;
    private GoogleMap googleMap;
    private LayoutInflater layoutInflater;
    private String searchSuggestion;
    String receiverProfileId;

    //    private MapView mapView;
    private AlertDialog dialog;
    private String mFlag = "";
    private boolean fromNotification = true;
    private boolean isSearhClicked;


    //    Map related fields....
    private SupportMapFragment supportMapFragment;
    private ClusterManager<UpdateLocationData> clusterManagerGrey, clusterManagerGreen, clusterManagerBlue;
    private ArrayList<Marker> usersMarkerArrayList = new ArrayList<>();
    private LatLng currentUserLatLng;
    private Timer cameraDragTimer;
    private int counter = 0;
    int current = 0;

    //    Instance fields....
    private List<UpdateLocationData> updateLocationDataList = new ArrayList<>();
    private List<UserWithOutRadiusData> userWithOutRadiusData = new ArrayList<>();
    private boolean shouldMapCameraChange = true;
    private String viewType;
    private boolean isCompassEnable = false;
    private boolean isTrackStarted;
    ArrayList<SearchUserInMapModel> searchUserInMapModelArrayList = new ArrayList<>();
    ArrayList<SearchUserInMapModel> tempSearchUserInMapModelArrayList = new ArrayList<>();
    private BottomSheetDialog bottomSheet;
    private BottomSheetDialog bottomSheetForFullView;
    private RecyclerView recyclerViewSearchUser;
    private SearchUsersInMapAdapter searchUsersInMapAdapter;

    //    Compass sensor fields....
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private Sensor accelerometer;
    private Sensor megFieldSensor;

    //    Animation
    private Animation mSlideUpAnimation, mSlideDownAnimation;

    //    DataBAse Fields....
    private MultiProfileRepositry multiProfileRepositry;
    private MultiProfileRoomModel currentProfilel;
    String profileId;

    private SlideUp slideUp;
    private AppCompatImageView imageViewSearchTopBar, imageViewDropdownContacts, imageViewContactQRTopBar, imageViewContactGridTopBar;
    CardView topBarLayout;
    //    Instance fields....
    private List<String> userFilter = new ArrayList<>();
    String mapType;

    //    Font fields....
    private FontUtils fontUtils;

    public FragmentHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.GONE);

//        mapView = view.findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//        mapView.onResume();
        try {
            if (getActivity() != null) {
                supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                if (supportMapFragment != null) {
                    supportMapFragment.getMapAsync(this);
                }
            }
//                MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (mapView != null)
//            mapView.getMapAsync(this);
        init();
        topBarLayout = homeBinding.topBarContact.topBarContactCardView;
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewContactSearchTopBar);
        imageViewContactQRTopBar = topBarLayout.findViewById(R.id.imageViewContactQRTopBar);
        imageViewContactGridTopBar = topBarLayout.findViewById(R.id.imageViewContactGridTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        TextView textViewHeading = topBarLayout.findViewById(R.id.textViewContactNameTopBar);
        textViewHeading.setText(R.string.explore);
        imageViewContactGridTopBar.setImageResource(R.mipmap.icon_grid_new);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewSearchTopBar.setVisibility(View.VISIBLE);
        imageViewContactQRTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
                ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
                EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
            }
        });
//        homeBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
        imageViewSearchTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp = new SlideUpBuilder(homeBinding.slideViewContacts)
                        .withListeners(new SlideUp.Listener.Events() {
                            @Override
                            public void onSlide(float percent) {
                            }

                            @Override
                            public void onVisibilityChanged(int visibility) {
                                if (visibility == View.GONE) {
                                    homeBinding.checkBoxFavourites.setChecked(false);
                                    homeBinding.checkBoxOutstandingTasks.setChecked(false);
                                    homeBinding.checkBoxPendingLNQs.setChecked(false);
                                    homeBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(homeBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(homeBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(homeBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(homeBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                        }
                                    }
                                    homeBinding.editTextSearch.requestFocus();
                                    homeBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
                                    homeBinding.viewHideTopBar.setVisibility(View.GONE);
                                    homeBinding.searchBarLayout.setVisibility(View.GONE);
                                } else {
                                    homeBinding.checkBoxFavourites.setChecked(false);
                                    homeBinding.checkBoxOutstandingTasks.setChecked(false);
                                    homeBinding.checkBoxPendingLNQs.setChecked(false);
                                    homeBinding.checkBoxVerifiedProfile.setChecked(false);
                                    if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                        String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                        if (userFilters != null) {
                                            if (userFilters.contains(Constants.FAVORITES)) {
                                                changeSelection(homeBinding.checkBoxFavourites);
                                                userFilter.add(Constants.FAVORITES);
                                            }
                                            if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                                changeSelection(homeBinding.checkBoxVerifiedProfile);
                                                userFilter.add(Constants.VERIFIED_PROFILE);
                                            }
                                            if (userFilters.contains(Constants.PENDING_LNQS)) {
                                                changeSelection(homeBinding.checkBoxPendingLNQs);
                                                userFilter.add(Constants.PENDING_LNQS);
                                            }
                                            if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                                changeSelection(homeBinding.checkBoxOutstandingTasks);
                                                userFilter.add(Constants.OUTSTANDING_TASKS);
                                            }
                                        }
                                    }
                                    homeBinding.editTextSearch.requestFocus();
                                    homeBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                                    homeBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                                    homeBinding.searchBarLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .withStartGravity(Gravity.TOP)
                        .withLoggingEnabled(true)
                        .withGesturesEnabled(true)
                        .withStartState(SlideUp.State.SHOWED)
                        .build();
            }
        });

        imageViewContactGridTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    if (v.getTag().equals("grid")) {
                        v.setTag("map");
                        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataList));
                                EventBus.getDefault().post(new EventBusUserSession("grid_view"));
                            }
                        }, 100);
                    } else {
                        v.setTag("grid");

                    }
                }
            }
        });
    }

    private void init() {
        multiProfileRepositry = new MultiProfileRepositry(getContext());
        //        Loading animations....
        mSlideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_anim);
        mSlideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down_anim);

        profileId = LnqApplication.getInstance().sharedPreferences.getString("activeProfile", "");

        //        Get All Data From DataBase
        multiProfileRepositry.getProfileData().observe(getActivity(), new Observer<List<MultiProfileRoomModel>>() {
            @Override
            public void onChanged(List<MultiProfileRoomModel> multiProfileRoomModels) {
                for (MultiProfileRoomModel data : multiProfileRoomModels) {
                    if (data.getId().equalsIgnoreCase(LnqApplication.getInstance().sharedPreferences.getString("activeProfile", ""))) {
                        currentProfilel = data;
                    }
                }
                if (currentProfilel.getHome_default_view() != null) {
                    viewType = currentProfilel.getHome_default_view();
                    if (viewType.equals(Constants.GridClicked)) {
                        ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
                    }
                    if (currentProfilel.getVisible_to() == null && currentProfilel.getVisible_at() == null) {
                        reqSetVisibility(Constants.NEAR_BY, Constants.CITY, currentProfilel.getId());
                    }
                }
            }
        });

        Bundle bundle = getArguments();
        String senderId;
        if (bundle != null) {
            mFlag = bundle.getString("mFlag", "");
            senderId = bundle.getString(EndpointKeys.USER_ID, "");
            receiverProfileId = bundle.getString(EndpointKeys.PROFILE_ID, "");
            if (receiverProfileId != null && !receiverProfileId.isEmpty()) {
                reqGetUserProfile(senderId, profileId, receiverProfileId);
            }
        }
        if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.IS_FROZEN, "0").equals("1")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your account was frozen on " + LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.STATUS_DATE, "") + ". unfreeze account to continue.");
            builder.setPositiveButton("Unfreeze", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    unFreeze("1");
                }
            });
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
//        Registering event bus for different triggers....
        EventBus.getDefault().register(this);
//        EventBus.getDefault().post(new EventBusUpdateChatCount(0));
//        Setting default values for views....
        ((MainActivity) getActivity()).mBind.mImgBack.setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).mBind.mTopBar.setVisibility(View.GONE);
//        ((MainActivity) getActivity()).mBind.mViewBgBottomBar.setVisibility(View.GONE);
//        changeButtonDrawable(homeBinding.layoutMap, homeBinding.layoutGrid);

//        Showing notification dialog
        if (LnqApplication.getInstance().sharedPreferences.getBoolean(EndpointKeys.SHOW_NOTIFICATION_DIALOG, true)) {
            LnqApplication.getInstance().editor.putBoolean(EndpointKeys.SHOW_NOTIFICATION_DIALOG, false).apply();
            fnDialogNotifications();
        }

        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
            String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
            if (userFilters != null) {
                if (userFilters.contains(Constants.FAVORITES)) {
                    changeSelection(homeBinding.checkBoxFavourites);
                    userFilter.add(Constants.FAVORITES);
                }
                if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                    changeSelection(homeBinding.checkBoxVerifiedProfile);
                    userFilter.add(Constants.VERIFIED_PROFILE);
                }
                if (userFilters.contains(Constants.PENDING_LNQS)) {
                    changeSelection(homeBinding.checkBoxPendingLNQs);
                    userFilter.add(Constants.PENDING_LNQS);
                }
                if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                    changeSelection(homeBinding.checkBoxOutstandingTasks);
                    userFilter.add(Constants.OUTSTANDING_TASKS);
                }
            }
        }

        homeBinding.checkBoxFavourites.setOnCheckedChangeListener(this);
        homeBinding.checkBoxOutstandingTasks.setOnCheckedChangeListener(this);
        homeBinding.checkBoxPendingLNQs.setOnCheckedChangeListener(this);
        homeBinding.checkBoxVerifiedProfile.setOnCheckedChangeListener(this);

        homeBinding.imageViewBack.setOnClickListener(this);
        homeBinding.textViewClearAll.setOnClickListener(this);
        homeBinding.mBtnApply.setOnClickListener(this);
        homeBinding.textViewFavorites.setOnClickListener(this);
        homeBinding.textViewVerifiedProfiles.setOnClickListener(this);
        homeBinding.textViewPendingLnq.setOnClickListener(this);
        homeBinding.textViewOutstandingTasks.setOnClickListener(this);

//        Setting default visibility if not set....
//        if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VISIBLE_TO, "").isEmpty()
//                && LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.VISIBLE_AT, "").isEmpty()) {
//            reqSetVisibility(Constants.NEAR_BY, Constants.CITY);
//        }

        //        Setting custom font to android views....
        setCustomFont();

//        Checking runtime location permission....
        if (((MainActivity) getActivity()).fnCheckLocationPermission()) {
            ((MainActivity) getActivity()).fnLocationTracking();
        } else {
            ((MainActivity) getActivity()).fnRequestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE);
        }

        toggleFilterButtonBackground();
        homeBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));

        if (homeBinding.editTextSearch.getText().toString().isEmpty()) {
            homeBinding.imageViewClose.setVisibility(View.GONE);
        } else {
            homeBinding.imageViewClose.setVisibility(View.VISIBLE);
        }

//        All event listeners....
        homeBinding.editTextSearch.addTextChangedListener(this);
        homeBinding.editTextSearch.setOnEditorActionListener(this);

//        Setting click handler for data binding....
        clickHandler = new HomeClickHandler();
        homeBinding.setClickHandler(clickHandler);

//        searchSuggestion = LnqApplication.getInstance().sharedPreferences.getString("search_suggestion", "");
//        if (!searchSuggestion.isEmpty()) {
//            List<String> suggestionArray = Arrays.asList(searchSuggestion.split(","));
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, suggestionArray);
//            homeBinding.editTextSearch.setAdapter(arrayAdapter);
//            homeBinding.editTextSearch.setThreshold(1);
//        }

        homeBinding.root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.root);
                return false;
            }
        });

        homeBinding.viewHideTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideUp.isVisible()) {
                    slideUp.hide();
                    homeBinding.searchBarLayout.setVisibility(View.GONE);
                }
            }
        });

        homeBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    current = progress / 5;
                    shouldMapCameraChange = true;
                    final LatLng latLng = googleMap.getCameraPosition().target;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, current);
                    googleMap.animateCamera(cameraUpdate);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackStarted = true;
                Log.d("TrackData", true + "");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isTrackStarted = false;
                    }
                }, 1500);
            }
        });

        slideUp = new SlideUpBuilder(homeBinding.slideViewContacts)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            homeBinding.checkBoxFavourites.setChecked(false);
                            homeBinding.checkBoxOutstandingTasks.setChecked(false);
                            homeBinding.checkBoxPendingLNQs.setChecked(false);
                            homeBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(homeBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(homeBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(homeBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(homeBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                }
                            }
                            homeBinding.topBarContact.topBarContactCardView.setVisibility(View.VISIBLE);
                            homeBinding.viewHideTopBar.setVisibility(View.GONE);
                            homeBinding.searchBarLayout.setVisibility(View.GONE);
                        } else {
                            homeBinding.checkBoxFavourites.setChecked(false);
                            homeBinding.checkBoxOutstandingTasks.setChecked(false);
                            homeBinding.checkBoxPendingLNQs.setChecked(false);
                            homeBinding.checkBoxVerifiedProfile.setChecked(false);
                            if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "").isEmpty()) {
                                String userFilters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                if (userFilters != null) {
                                    if (userFilters.contains(Constants.FAVORITES)) {
                                        changeSelection(homeBinding.checkBoxFavourites);
                                        userFilter.add(Constants.FAVORITES);
                                    }
                                    if (userFilters.contains(Constants.VERIFIED_PROFILE)) {
                                        changeSelection(homeBinding.checkBoxVerifiedProfile);
                                        userFilter.add(Constants.VERIFIED_PROFILE);
                                    }
                                    if (userFilters.contains(Constants.PENDING_LNQS)) {
                                        changeSelection(homeBinding.checkBoxPendingLNQs);
                                        userFilter.add(Constants.PENDING_LNQS);
                                    }
                                    if (userFilters.contains(Constants.OUTSTANDING_TASKS)) {
                                        changeSelection(homeBinding.checkBoxOutstandingTasks);
                                        userFilter.add(Constants.OUTSTANDING_TASKS);
                                    }
                                }
                            }
                            homeBinding.topBarContact.topBarContactCardView.setVisibility(View.INVISIBLE);
                            homeBinding.viewHideTopBar.setVisibility(View.VISIBLE);
                            homeBinding.searchBarLayout.setVisibility(View.GONE);
                        }
                    }
                })
                .withStartGravity(Gravity.TOP)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(homeBinding.viewScroll)
                .build();

        homeBinding.mTvAccountHeading1.setOnClickListener(this);
        homeBinding.imageViewBack.setOnClickListener(this);
        homeBinding.mBtnApply.setOnClickListener(this);
        homeBinding.textViewClearAll.setOnClickListener(this);
    }

    private void configureCompass() {
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        megFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorEventListener = new CompassSensorEventListener(sensorManager, homeBinding.buttonCompass, this);
    }

    private void registerCompassListener() {
        if (sensorEventListener != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(sensorEventListener, megFieldSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    //    TODO: Activity lifecycle methods starts here....
    @Override
    public void onResume() {
        super.onResume();
        if (isCompassEnable)
            registerCompassListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isCompassEnable)
            sensorManager.unregisterListener(sensorEventListener);
    }

    private void setCustomFont() {
        fontUtils = FontUtils.getFontUtils(getActivity());
        fontUtils.setEditTextSemiBold(homeBinding.editTextSearch);
        fontUtils.setButtonSemiBold(homeBinding.textViewNearbyUsersCount);
        fontUtils.setTextViewRegularFont(homeBinding.clearTextViewDistance);
        fontUtils.setTextViewRegularFont(homeBinding.textViewShowOnly);
        fontUtils.setTextViewRegularFont(homeBinding.textViewFavorites);
        fontUtils.setTextViewRegularFont(homeBinding.textViewVerifiedProfiles);
        fontUtils.setTextViewRegularFont(homeBinding.textViewPendingLnq);
        fontUtils.setTextViewRegularFont(homeBinding.textViewOutstandingTasks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (callNotifications != null && callNotifications.isExecuted()) {
            callNotifications.cancel();
        }
        if (callUpdateLocation != null && callUpdateLocation.isExecuted()) {
            callUpdateLocation.cancel();
        }
        if (callSetVisibility != null && callSetVisibility.isExecuted()) {
            callSetVisibility.cancel();
        }
//        mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        reqNotificationsSet();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).mBind.viewOnBottom.setVisibility(View.VISIBLE);
    }

    //    Method triggers when google map is ready to work....
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapType = LnqApplication.getInstance().sharedPreferences.getString("mapType", "");
        if (mapType != null && !mapType.isEmpty()){
            if (mapType.equalsIgnoreCase("satelite")){
                googleMap.setMapType(googleMap.MAP_TYPE_SATELLITE);
            }else if (mapType.equalsIgnoreCase("terrain")){
                googleMap.setMapType(googleMap.MAP_TYPE_TERRAIN);
            }else if  (mapType.equalsIgnoreCase("defualt")){
                googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
            }else {
                googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
            }
        }
        reqUpdateLocation(0f, 0f, 0, "", homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), getZoomLevel(googleMap.getCameraPosition().zoom), profileId);
        final float googleMapCurrentZoomLevel = googleMap.getCameraPosition().zoom;
        homeBinding.seekBar.setProgress((int) (googleMapCurrentZoomLevel * 5));
        configureCompass();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.snazzymaps_ultra_light_with_labels));
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

//        Setting default configurations for google map settings....
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

//        Setting google map markers,camera movie listeners....
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraMoveStartedListener(this);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                EventBus.getDefault().post(new EventBusCloseMapProfile());
            }
        });
    }

    //    Event bus method triggers when send request mapview or gridview chnaged by defualt settings
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusMapGridView(EventBusMapGridViewChanged eventBusMapGridViewChanged) {
        viewType = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.MAPVIEW, "");
//        viewType = currentProfilel.getHome_default_view();
        if (viewType.equals(Constants.GridClicked)) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
        } else if (viewType.equals(Constants.MapClicked)) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME, true, null);
            Toast.makeText(getActivity(), "MAP", Toast.LENGTH_SHORT).show();
        }
//        if (eventBusMapGridViewChanged.equals(Constants.GridClicked)) {
//            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
//        }
//        else if (eventBusMapGridViewChanged.equals(Constants.MapClicked)){
//            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME, true, null);
//            Toast.makeText(getActivity(), "MAP", Toast.LENGTH_SHORT).show();
//        }
//    }
    }

    //    Event bus method triggers when send request or cancel request with any user use to refresh map pins....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusRefreshUsers(EventBusUpdateUserStatus eventBusUpdateUserStatus) {
        refreshUsersOnMap(false);
    }

    //    Event bus method triggers when user allowed location permissions....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventLocationPermission(EventBusLocationPermission mObj) {
        ((MainActivity) getActivity()).fnLocationTracking();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        supportMapFragment.getMapAsync(this);
    }

    //    Event bus method triggers when user click on map profile card view....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMapProfileClick(EventBusMapProfileClick mObj) {
        ((MainActivity) getActivity()).fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (mObj.getClickType() == 0) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.MAP_FILTER, true, null);
        } else if (mObj.getClickType() == 1) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
        }
    }

    //    Event bus method triggers when user location is updated....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventLocationUpdate(EventBusMapLocationUpdate mObj) {
        if (mFlag.equalsIgnoreCase("near_you") && fromNotification) {
            currentUserLatLng = new LatLng(mObj.getLocation().getLatitude(), mObj.getLocation().getLongitude());
        } else {
            currentUserLatLng = new LatLng(mObj.getLocation().getLatitude(), mObj.getLocation().getLongitude());
            if (shouldMapCameraChange && googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLng, 2.99f));
            }
            double radius = getMapVisibleRegionRadius(googleMap);
            if (!(ValidUtils.isNetworkAvailable(getActivity()))) {
                return;
            }
            if (callUpdateLocation != null && callUpdateLocation.isExecuted()) {
                callUpdateLocation.cancel();
            }
            if (((MainActivity) getActivity()).sendLocationName) {
                getCountryName(currentUserLatLng.latitude, currentUserLatLng.longitude, radius);
            } else {
                reqUpdateLocation(currentUserLatLng.latitude, currentUserLatLng.longitude, radius, "", homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), getZoomLevel(googleMap.getCameraPosition().zoom), profileId);
            }
        }
    }

    //    Event bus method triggers when user filters are updated....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserFilters(EventBusUpdateFilters eventBusUpdateFilters) {
        if (getActivity() != null) {
            toggleFilterButtonBackground();
            refreshUsersOnMap(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUpdateSearchText(EventBusMapSearchTextUpdate eventBusMapSearchTextUpdate) {
        toggleFilterButtonBackground();
        homeBinding.editTextSearch.setText(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.SEARCH_TEXT, ""));
        refreshUsersOnMap(true);
    }

    //    Event bus method triggers when user click userB profile location name to show userB zoom level on map with pin....
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusGetZoomLevel(EventBusUserZoomLevel eventBusUserZoomLevel) {
        switch (eventBusUserZoomLevel.getZoomLevel()) {
            case Constants.GLOBAL:
                setCameraToUserPin(eventBusUserZoomLevel.getLatitude(), eventBusUserZoomLevel.getLongitude(), 1);
                break;
            case Constants.LOCAL:
                setCameraToUserPin(eventBusUserZoomLevel.getLatitude(), eventBusUserZoomLevel.getLongitude(), 5);
                break;
            case Constants.CITY:
                setCameraToUserPin(eventBusUserZoomLevel.getLatitude(), eventBusUserZoomLevel.getLongitude(), 10);
                break;
            case Constants.STREET:
                setCameraToUserPin(eventBusUserZoomLevel.getLatitude(), eventBusUserZoomLevel.getLongitude(), 15);
                break;
        }
        shouldMapCameraChange = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeMapMarkerCamera(EventBusMapMarkerChange eventBusMapMarkerChange) {
        int position = eventBusMapMarkerChange.getPosition();
        double latitude = Double.parseDouble(eventBusMapMarkerChange.getLatitude());
        double longitude = Double.parseDouble(eventBusMapMarkerChange.getLongitude());
        setCameraToUserPin(latitude, longitude, (int) googleMap.getCameraPosition().zoom);
        //  homeBinding.seekBar.setProgress((int) (googleMap.getCameraPosition().zoom*5));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusUserMapSearch(EventBusUserMapSearch mObj) {
        String userID = null;
        if (userWithOutRadiusData.get(mObj.getPosition()).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
            userID = userWithOutRadiusData.get(mObj.getPosition()).getReceiver_id();
        } else {
            userID = userWithOutRadiusData.get(mObj.getPosition()).getSender_id();
        }
        if (userWithOutRadiusData.get(mObj.getPosition()).getMatch_type().equalsIgnoreCase("name")
                && !userWithOutRadiusData.get(mObj.getPosition()).getUser_distance().isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString(EndpointKeys.USER_ID, userID);
            bundle.putString(EndpointKeys.PROFILE_ID, userWithOutRadiusData.get(mObj.getPosition()).getProfile_id());
            bundle.putString("mFlag", "near_you");
            ((MainActivity) getActivity()).mFScreenName = Constants.HOME;
            ((MainActivity) getActivity()).fnLoadFragReplace(Constants.HOME, false, bundle);
            bottomSheet.cancel();
            bottomSheetForFullView.cancel();
            homeBinding.editTextSearch.setText("");
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(EndpointKeys.USER_ID, userID);
            bundle.putString(EndpointKeys.PROFILE_ID, userWithOutRadiusData.get(mObj.getPosition()).getProfile_id());
            bundle.putString(Constants.REQUEST_FROM, "mapSearch");
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.LNQ_CONTACT_PROFILE_VIEW, true, bundle);
            bottomSheet.cancel();
            bottomSheetForFullView.cancel();
            homeBinding.editTextSearch.setText("");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusChangeMapType(EventBusChangeMapTypes mObj) {
        if (mObj.getMapType().equalsIgnoreCase("satelite")) {
            googleMap.setMapType(googleMap.MAP_TYPE_SATELLITE);
            LnqApplication.getInstance().editor.putString("mapType", "satelite").apply();
        } else if (mObj.getMapType().equalsIgnoreCase("terrain")) {
            googleMap.setMapType(googleMap.MAP_TYPE_TERRAIN);
            LnqApplication.getInstance().editor.putString("mapType", "terrain").apply();
        } else if (mObj.getMapType().equalsIgnoreCase("defualt")) {
            googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
            LnqApplication.getInstance().editor.putString("mapType", "defualt").apply();
        } else {
            googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
        }
    }

    private void setCameraToUserPin(double latitude, double longitude, int zoomLevel) {
        if (googleMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomLevel);
            googleMap.animateCamera(cameraUpdate);
            //  homeBinding.seekBar.setProgress((int) (googleMap.getCameraPosition().zoom*5));
        }
    }

    private void toggleFilterButtonBackground() {
        usersMarkerArrayList.clear();
        String filters = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
        if (filters != null)
            if (filters.isEmpty()) {
                homeBinding.buttonFilter.setVisibility(View.VISIBLE);
//                homeBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
//                homeBinding.horizontalScrollViewHomeFilters.setVisibility(View.GONE);
            } else {
                if (layoutInflater == null) {
                    layoutInflater = LayoutInflater.from(getContext());
                }
                homeBinding.linearLayoutHomeFilter.removeAllViews();
                List<String> filterList = new ArrayList<>(Arrays.asList(filters.split(",")));
                if (filterList.size() > 0) {
                    for (int i = 0; i < filterList.size(); i++) {
                        View filterView = layoutInflater.inflate(R.layout.row_filter, null);
                        TextView textViewFilter = filterView.findViewById(R.id.textViewFilterRow);
                        textViewFilter.setText(filterList.get(i));
                        ImageView imageViewCloseFilter = filterView.findViewById(R.id.imageViewCloseFilter);
                        imageViewCloseFilter.setOnClickListener(view -> {
                            homeBinding.linearLayoutHomeFilter.removeView(filterView);
                            filterList.remove(textViewFilter.getText().toString());
                            String filter = filterList.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "");
                            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, filter).apply();
                            EventBus.getDefault().post(new EventBusUpdateFilters());
                            EventBus.getDefault().post(new EventBusUserSession("connection_filter"));
                            if (filterList.size() == 0) {
                                homeBinding.buttonFilter.setVisibility(View.VISIBLE);
//                                homeBinding.clearTextViewFilter.setVisibility(View.INVISIBLE);
//                                homeBinding.horizontalScrollViewHomeFilters.setVisibility(View.GONE);
                            }
                        });
                        homeBinding.linearLayoutHomeFilter.addView(filterView);
                    }
                }
//                homeBinding.horizontalScrollViewHomeFilters.setVisibility(View.VISIBLE);
                homeBinding.buttonFilter.setVisibility(View.INVISIBLE);
//                homeBinding.clearTextViewFilter.setVisibility(View.VISIBLE);
            }
    }

    //    Method used to get country name using current location....
    public void getCountryName(double latitude, double longitude, double radius) {
        loadGeoCoderJson(latitude, longitude, radius, "Country Name");
    }

    private void changeButtonDrawable(LinearLayout buttonSelected, LinearLayout buttonDeselected) {
        buttonSelected.setSelected(true);
        buttonDeselected.setSelected(false);
//        buttonDeselected.setBackgroundColor(getResources().getColor(R.color.colorWhiteTransparent));
    }

    //    Method used to create custom markers using layout file....
    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
        View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_custom_marker, null);
        AppCompatImageView markerImageView = customMarkerView.findViewById(R.id.mImgMarker);

        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    //    Method use to show notification dialog....
    private void fnDialogNotifications() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setMessage("Do you want to enable notifications ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                reqNotificationsSet();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    //    Method triggers when any marker except current user marker is clicked on map....
    @Override
    public boolean onMarkerClick(Marker marker) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
        UpdateLocationData updateLocationData = (UpdateLocationData) marker.getTag();
        EventBus.getDefault().post(new EventBusUserSession("marker_tapped"));
        int index = -1;
        for (int i = 0; i < usersMarkerArrayList.size(); i++) {
            if (usersMarkerArrayList.get(i).equals(marker)) {
                index = i;
                break;
            }
        }
        if (updateLocationData != null) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.MAP_PROFILE, true, null);
            final int finalIndex = index;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new EventBusSendUsersList(updateLocationDataList, finalIndex));
                }
            }, 100);
        }
        return true;
    }

    //    Method to refresh users on map using reqUsersInRadius Request....
    private void refreshUsersOnMap(boolean isUpdateMapProfile) {
        if (googleMap != null) {
            final LatLng latLng = googleMap.getCameraPosition().target;
            reqUsersInRadius(latLng.latitude, latLng.longitude, getMapVisibleRegionRadius(googleMap), homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), isUpdateMapProfile, getZoomLevel(googleMap.getCameraPosition().zoom), profileId);
        }
    }

    //    Method to hit api to change notification status....
    private void reqNotificationsSet() {
        callNotifications = Api.WEB_SERVICE.notificationsSet(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), "1", LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.FCM_TOKEN, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.NOTIFICATION_STATUS, "0"), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.LAST_LOGIN, ""));
        callNotifications.enqueue(new Callback<PushNotificationMainObject>() {
            @Override
            public void onResponse(Call<PushNotificationMainObject> call, Response<PushNotificationMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (response.body().getLogoutFromOld() == 1) {
                                ((MainActivity) getActivity()).logOut();
                            }
                            break;
                        case 0:
                            if (response.body().getMessage() != null) {
                                if (response.body().getMessage().equals("Your account is suspended. Please contact administration")) {
                                    reqLogOut();
                                } else if (response.body().getMessage().equals("Your account is frozen.")) {
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.IS_FROZEN, "1").apply();
                                    if (LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.IS_FROZEN, "0").equals("1")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setMessage("Your account was frozen on " + LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.STATUS_DATE, "") + ". unfreeze account to continue.");
                                        builder.setPositiveButton("Unfreeze", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                unFreeze("1");
                                            }
                                        });
                                        dialog = builder.create();
                                        dialog.setCancelable(false);
                                        dialog.show();
                                    }
                                }
                            }
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PushNotificationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {

                    } else {

                    }
                } else {

                }
            }
        });
    }

    private void reqLogOut() {
        ((MainActivity) getActivity()).progressDialog.show();
//        callLogOut = Api.WEB_SERVICE.logout(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), "1", LnqApplication.getInstance().sharedPreferences.getString("fcm_token", ""));
        callLogOut = Api.WEB_SERVICE.logout(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), "1", LnqApplication.getInstance().sharedPreferences.getString("fcm_token", ""));
        callLogOut.enqueue(new Callback<LogOut>() {
            @Override
            public void onResponse(Call<LogOut> call, Response<LogOut> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case "1":
                            EventBus.getDefault().post(new EventBusUserSession("sign_out"));
                            FirebaseAuth.getInstance().signOut();
                            ((MainActivity) getActivity()).logOut();
                            break;
                        case "0":
                            FirebaseAuth.getInstance().signOut();
                            ((MainActivity) getActivity()).logOut();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<LogOut> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    //    Method to hit api to get users in current map radius....
    private void reqUsersInRadius(double latitude, double longitude, double radius, String searchKey, String searchFilter, final boolean isUpdateMapProfile, String zoomLevel, String profileId) {
        callUpdateLocation = Api.WEB_SERVICE.usersInRadius(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), profileId, String.valueOf(latitude), String.valueOf(longitude), String.valueOf(radius), searchKey, searchFilter, zoomLevel);
        callUpdateLocation.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                try {
                    if (getActivity() != null) {
                        if (response != null && response.isSuccessful()) {
                            updateLocationDataList.clear();
                            switch (response.body().getStatus()) {
                                case 1:
                                    updateLocationDataList = response.body().getUsersInRadius();
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTION_COUNT, response.body().getConnections_count()).apply();
                                    toggleCount();
                                    setUserMarkersWithCluster();
//                            setUsersMarkersOnMap();
                                    if (isUpdateMapProfile) {
                                        EventBus.getDefault().post(new EventBusSendUsersList(updateLocationDataList, 0));
                                        EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataList));
                                    }
                                    ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
                                    for (int i = 1; i < updateLocationDataList.size(); i++) {
                                        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(updateLocationDataList.get(i).getReceiver_id())) {
                                            mentionModelArrayList.add(new MentionModel(updateLocationDataList.get(i).getReceiver_id(), updateLocationDataList.get(i).getUser_name().toLowerCase(), updateLocationDataList.get(i).getUser_image(), updateLocationDataList.get(i).getProfile_id()));
                                        }
                                    }
                                    MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
                                    homeBinding.editTextSearch.setAdapter(mentionChatAdapter);
                                    homeBinding.editTextSearch.setThreshold(1);
                                    homeBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
                                            homeBinding.editTextSearch.setText(homeBinding.editTextSearch.getText());
                                            toggleFilterButtonBackground();
                                            isSearhClicked = true;
                                            reqUsersWithOutRadius(homeBinding.editTextSearch.getText().toString().trim(), profileId);
                                        }
                                    });
                                    break;
                                case 0:
                                    googleMap.clear();
//                            for (int i = 0; i < usersMarkerArrayList.size(); i++) {
//                                usersMarkerArrayList.get(i).remove();
//                            }
//                            usersMarkerArrayList.clear();
                                    toggleCount();
                                    homeBinding.textViewNearbyUsersCount.setVisibility(View.GONE);
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    private void reqUsersWithOutRadius(String searchKey, String profileId) {
        userWithOutRadiusData.clear();
        searchUserInMapModelArrayList.clear();
        tempSearchUserInMapModelArrayList.clear();
        callUserWithOutRadius = Api.WEB_SERVICE.usersWithOutRadius(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), profileId, searchKey);
        callUserWithOutRadius.enqueue(new Callback<UserWithOutRadiusMainObject>() {
            @Override
            public void onResponse(Call<UserWithOutRadiusMainObject> call, Response<UserWithOutRadiusMainObject> response) {
                try {
                    if (getActivity() != null) {
                        if (response != null && response.isSuccessful()) {
                            switch (response.body().getStatus()) {
                                case 1:
                                    userWithOutRadiusData = response.body().getUsersWithOutRadius();
//                                    if (isUpdateMapProfile) {
//                                        EventBus.getDefault().post(new EventBusSendUsersList(updateLocationDataList, 0));
//                                        EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataList));
//                                    }
                                    for (int i = 0; i < userWithOutRadiusData.size(); i++) {
                                        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(userWithOutRadiusData.get(i).getReceiver_id())) {
                                            searchUserInMapModelArrayList.add(new SearchUserInMapModel(userWithOutRadiusData.get(i).getReceiver_id(), userWithOutRadiusData.get(i).getUser_name(), userWithOutRadiusData.get(i).getUser_image(), userWithOutRadiusData.get(i).getUser_current_position(), userWithOutRadiusData.get(i).getUser_company(), userWithOutRadiusData.get(i).getUser_distance(),
                                                    userWithOutRadiusData.get(i).getMatch_type(), userWithOutRadiusData.get(i).getUser_tags(), userWithOutRadiusData.get(i).getUser_home_address(), userWithOutRadiusData.get(i).getUser_status_msg(),
                                                    userWithOutRadiusData.get(i).getUser_bio(), userWithOutRadiusData.get(i).getTask_description(), userWithOutRadiusData.get(i).getNote_description(), userWithOutRadiusData.get(i).getUser_interests()));
                                        }
                                    }
                                    for (int i = 0; i < userWithOutRadiusData.size(); i++) {
                                        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(userWithOutRadiusData.get(i).getReceiver_id())) {
                                            tempSearchUserInMapModelArrayList.add(new SearchUserInMapModel(userWithOutRadiusData.get(i).getReceiver_id(), userWithOutRadiusData.get(i).getUser_name(), userWithOutRadiusData.get(i).getUser_image(), userWithOutRadiusData.get(i).getUser_current_position(), userWithOutRadiusData.get(i).getUser_company(), userWithOutRadiusData.get(i).getUser_distance(),
                                                    userWithOutRadiusData.get(i).getMatch_type(), userWithOutRadiusData.get(i).getUser_tags(), userWithOutRadiusData.get(i).getUser_home_address(), userWithOutRadiusData.get(i).getUser_status_msg(),
                                                    userWithOutRadiusData.get(i).getUser_bio(), userWithOutRadiusData.get(i).getTask_description(), userWithOutRadiusData.get(i).getNote_description(), userWithOutRadiusData.get(i).getUser_interests()));
                                        }
                                        if (tempSearchUserInMapModelArrayList.size() == 2) {
                                            break;
                                        }
                                    }
                                    if (isSearhClicked) {
                                        int count = searchUserInMapModelArrayList.size();
                                        if (count == 1){
                                            String userID = null;
                                            if (userWithOutRadiusData.get(0).getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""))) {
                                                userID = userWithOutRadiusData.get(0).getReceiver_id();
                                            } else {
                                                userID = userWithOutRadiusData.get(0).getSender_id();
                                            }
                                            if (userWithOutRadiusData.get(0).getMatch_type().equalsIgnoreCase("name")
                                                    && !userWithOutRadiusData.get(0).getUser_distance().isEmpty()) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString(EndpointKeys.USER_ID, userID);
                                                bundle.putString(EndpointKeys.PROFILE_ID, userWithOutRadiusData.get(0).getProfile_id());
                                                bundle.putString("mFlag", "near_you");
                                                ((MainActivity) getActivity()).mFScreenName = Constants.HOME;
                                                ((MainActivity) getActivity()).fnLoadFragReplace(Constants.HOME, false, bundle);
//                                                bottomSheet.cancel();
//                                                bottomSheetForFullView.cancel();
                                                homeBinding.editTextSearch.setText("");
                                            }
                                        }
                                        searchUsersInMapAdapter = new SearchUsersInMapAdapter(getActivity(), tempSearchUserInMapModelArrayList, searchKey);
                                        bottomSheet = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
                                        View view = LayoutInflater.from(getContext()).inflate(R.layout.cus_bottomsheet_searchresults, null);
                                        recyclerViewSearchUser = view.findViewById(R.id.recyclerViewUsersInMap);
                                        AppCompatImageView imageCross = view.findViewById(R.id.imageViewCross);
                                        AppCompatTextView textViewTotalResults = view.findViewById(R.id.textViewTotalResults);
                                        AppCompatTextView textViewSearchType = view.findViewById(R.id.textViewSearchType);
                                        textViewTotalResults.setText(String.valueOf(count) + " Results");
                                        recyclerViewSearchUser.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        recyclerViewSearchUser.setAdapter(searchUsersInMapAdapter);
                                        bottomSheet.setContentView(view);
                                        bottomSheet.show();
                                        isSearhClicked = false;
                                        imageCross.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                bottomSheet.cancel();
                                            }
                                        });
                                        textViewSearchType.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                searchUsersInMapAdapter = new SearchUsersInMapAdapter(getActivity(), searchUserInMapModelArrayList, searchKey);
                                                bottomSheetForFullView = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
                                                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.cus_bottomsheet_searchresults2, null);
                                                recyclerViewSearchUser = view1.findViewById(R.id.recyclerViewUsersInMap);
                                                AppCompatImageView arrowImage = view1.findViewById(R.id.arrowImage);
                                                ConstraintLayout layoutBottomSheet = view1.findViewById(R.id.layoutBottomSheet1);
                                                AppCompatTextView textViewTotalResults = view1.findViewById(R.id.textViewTotalResults);
                                                AppCompatTextView textViewSearchType1 = view1.findViewById(R.id.textViewSearchType1);
                                                textViewTotalResults.setText(String.valueOf(count) + " Results");
                                                recyclerViewSearchUser.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                recyclerViewSearchUser.setAdapter(searchUsersInMapAdapter);
                                                bottomSheetForFullView.setContentView(view1);
                                                layoutBottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                                                bottomSheetForFullView.show();
                                                arrowImage.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        bottomSheetForFullView.cancel();
                                                    }
                                                });
                                                textViewSearchType1.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        bottomSheetForFullView.cancel();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    break;
                                case 0:
                                    googleMap.clear();
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserWithOutRadiusMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    private void setUserMarkersWithCluster() {
        googleMap.clear();

//        MarkerManager markerManager = new MarkerManager(googleMap);

        clusterManagerBlue = new ClusterManager<>(getActivity(), googleMap);
//        clusterManagerGreen = new ClusterManager<>(getActivity(), googleMap, markerManager);
//        clusterManagerGrey = new ClusterManager<>(getActivity(), googleMap, markerManager);

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                clusterManagerBlue.onCameraChange(cameraPosition);
//                clusterManagerGrey.onCameraChange(cameraPosition);
//                clusterManagerGreen.onCameraChange(cameraPosition);
            }
        });

        final ClusterRendererUtils blueRenderer = new ClusterRendererUtils(getActivity(), googleMap, clusterManagerBlue, "blue");
        clusterManagerBlue.setRenderer(blueRenderer);
//        final ClusterRendererUtils greyRenderer = new ClusterRendererUtils(getActivity(), googleMap, clusterManagerGrey, "grey");
//        clusterManagerGrey.setRenderer(greyRenderer);
//        final ClusterRendererUtils greenRenderer = new ClusterRendererUtils(getActivity(), googleMap, clusterManagerGreen, "green");
//        clusterManagerGreen.setRenderer(greenRenderer);

        for (int i = 0; i < updateLocationDataList.size(); i++) {
            UpdateLocationData updateLocationData = updateLocationDataList.get(i);
            updateLocationData.setIndex(i);
//            if (updateLocationData.getIs_connection().equals(Constants.CONNECTED)) {
//                clusterManagerGreen.addItem(updateLocationData);
//            } else if (updateLocationData.getIs_connection().equals(Constants.CONTACTED)) {
            clusterManagerBlue.addItem(updateLocationData);
//            } else {
//                clusterManagerGrey.addItem(updateLocationData);
//            }
        }

//        clusterManagerGrey.setOnClusterClickListener(this);
//        clusterManagerGrey.setOnClusterItemClickListener(this);
//        clusterManagerGrey.setOnClusterItemInfoWindowClickListener(this);
//        googleMap.setOnMarkerClickListener(clusterManagerGrey);
//        googleMap.setInfoWindowAdapter(clusterManagerGrey.getMarkerManager());
//        googleMap.setOnInfoWindowClickListener(clusterManagerGrey);

        clusterManagerBlue.setOnClusterClickListener(this);
        clusterManagerBlue.setOnClusterItemClickListener(this);
        clusterManagerBlue.setOnClusterItemInfoWindowClickListener(this);
        googleMap.setOnMarkerClickListener(clusterManagerBlue);
        googleMap.setInfoWindowAdapter(clusterManagerBlue.getMarkerManager());
        googleMap.setOnInfoWindowClickListener(clusterManagerBlue);

//        clusterManagerGreen.setOnClusterClickListener(this);
//        clusterManagerGreen.setOnClusterItemClickListener(this);
//        clusterManagerGreen.setOnClusterItemInfoWindowClickListener(this);
//        googleMap.setOnMarkerClickListener(clusterManagerGreen);
//        googleMap.setInfoWindowAdapter(clusterManagerGreen.getMarkerManager());
//        googleMap.setOnInfoWindowClickListener(clusterManagerGreen);

        clusterManagerBlue.cluster();
//        clusterManagerGrey.cluster();
//        clusterManagerGreen.cluster();
    }

    //    Method to hit api to update current location of user....
    private void reqUpdateLocation(double latitude, double longitude, double radius, String location, String searchKey, String searchFilter, String zoomLevel, String profileId) {
//        callUpdateLocation = Api.WEB_SERVICE.updateLocation(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), String.valueOf(latitude), String.valueOf(longitude), String.valueOf(radius), location, searchKey, searchFilter, zoomLevel);
        callUpdateLocation = Api.WEB_SERVICE.updateLocation(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString("id", ""), profileId, String.valueOf(latitude), String.valueOf(longitude), String.valueOf(radius), location, searchKey, searchFilter, zoomLevel);
        callUpdateLocation.enqueue(new Callback<UpdateLocationMainObject>() {
            @Override
            public void onResponse(Call<UpdateLocationMainObject> call, Response<UpdateLocationMainObject> response) {
                try {
                    if (shouldMapCameraChange) {
                        if (response != null && response.isSuccessful() && getActivity() != null) {
                            updateLocationDataList.clear();
                            switch (response.body().getStatus()) {
                                case 1:
                                    updateLocationDataList = response.body().getUpdateLocation();
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTION_COUNT, response.body().getConnections_count()).apply();
                                    toggleCount();
//                            setUsersMarkersOnMap();
                                    setUserMarkersWithCluster();
                                    if (viewType.equals(Constants.GridClicked)) {
                                        EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataList));
                                        EventBus.getDefault().post(new EventBusUserSession("grid_view"));
                                    }
                                    ArrayList<MentionModel> mentionModelArrayList = new ArrayList<>();
                                    for (int i = 1; i < updateLocationDataList.size(); i++) {
                                        if (!LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "").equalsIgnoreCase(updateLocationDataList.get(i).getReceiver_id())) {
                                            mentionModelArrayList.add(new MentionModel(updateLocationDataList.get(i).getReceiver_id(), updateLocationDataList.get(i).getUser_name().toLowerCase(), updateLocationDataList.get(i).getUser_image(), updateLocationDataList.get(i).getProfile_id()));
                                        }
                                    }
                                    MentionChatAdapter mentionChatAdapter = new MentionChatAdapter(getActivity(), R.layout.search_contact_row, mentionModelArrayList);
                                    homeBinding.editTextSearch.setAdapter(mentionChatAdapter);
                                    homeBinding.editTextSearch.setThreshold(1);
                                    homeBinding.editTextSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
                                            homeBinding.editTextSearch.setText(homeBinding.editTextSearch.getText());
                                            toggleFilterButtonBackground();
                                            reqUsersWithOutRadius(homeBinding.editTextSearch.getText().toString().trim(), profileId);
                                        }
                                    });
                                    break;
                                case 0:
                                    updateLocationDataList = response.body().getUpdateLocation();
                                    LnqApplication.getInstance().editor.putString(EndpointKeys.CONNECTION_COUNT, response.body().getConnections_count()).apply();
                                    googleMap.clear();
                                    toggleCount();
                                    homeBinding.textViewNearbyUsersCount.setVisibility(View.GONE);
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<UpdateLocationMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
//                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    private void setUsersMarkersOnMap() {
        if (updateLocationDataList.size() == 0) {
            for (int i = 0; i < usersMarkerArrayList.size(); i++) {
                usersMarkerArrayList.get(i).remove();
            }
            usersMarkerArrayList.clear();
        }
        for (int i = 0; i < usersMarkerArrayList.size(); i++) {
            UpdateLocationData updateLocationData = (UpdateLocationData) usersMarkerArrayList.get(i).getTag();
            String id = updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationData.getReceiver_id() : updateLocationData.getSender_id();
            int counter = 0;
            for (int j = 0; j < updateLocationDataList.size(); j++) {
                UpdateLocationData updateLocationData1 = updateLocationDataList.get(j);
                String userId = updateLocationData1.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationData1.getReceiver_id() : updateLocationData1.getSender_id();
                if (!id.equals(userId)) {
                    counter++;
                }
            }
            if (counter == updateLocationDataList.size()) {
                usersMarkerArrayList.get(i).remove();
                usersMarkerArrayList.remove(i);
            }
        }
        if (updateLocationDataList.size() > 0) {
            for (int i = 0; i < updateLocationDataList.size(); i++) {
                UpdateLocationData updateLocationData = updateLocationDataList.get(i);
                if (updateLocationData != null) {
                    if (updateLocationData.getUser_lat() != null && updateLocationData.getUser_long() != null && !updateLocationData.getUser_lat().isEmpty() && !updateLocationData.getUser_long().isEmpty()) {
                        String userId = updateLocationData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? updateLocationData.getReceiver_id() : updateLocationData.getSender_id();
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng start = new LatLng(Double.parseDouble(updateLocationData.getUser_lat()), Double.parseDouble(updateLocationData.getUser_long()));
                        for (int j = 0; j < usersMarkerArrayList.size(); j++) {

                            UpdateLocationData friendData = (UpdateLocationData) usersMarkerArrayList.get(j).getTag();
                            String friendId = friendData.getSender_id().equals(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "")) ? friendData.getReceiver_id() : friendData.getSender_id();
                            if (userId.equals(friendId)) {
                                start = usersMarkerArrayList.get(j).getPosition();
                                usersMarkerArrayList.get(j).remove();
                                usersMarkerArrayList.remove(j);
                                break;
                            }
                        }

                        markerOptions.position(new LatLng(Double.parseDouble(updateLocationData.getUser_lat()), Double.parseDouble(updateLocationData.getUser_long())));
                        if (updateLocationData.getIs_favorite().equals(Constants.FAVORITE)) {
                            if (updateLocationData.getIs_connection().equals("")) {
                                if (getActivity() != null) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.gray_fav_pin_trans)));
                                }
                            } else if (updateLocationData.getIs_connection().equals(Constants.CONTACTED)) {
                                if (getActivity() != null) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.pendind_fav_pin_trans)));
                                }
                            } else if (updateLocationData.getIs_connection().equals(Constants.CONNECTED)) {
                                if (getActivity() != null) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.fav_pin_trans)));
                                }
                            }
                        } else {
                            if (updateLocationData.getIs_connection().equals("")) {
                                if (getActivity() != null) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.pin_gray_trans)));
                                }
                            } else if (updateLocationData.getIs_connection().equals(Constants.CONTACTED)) {
                                if (getActivity() != null) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.pin_blue_trans)));
                                }
                            } else if (updateLocationData.getIs_connection().equals(Constants.CONNECTED)) {
                                if (getActivity() != null) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.pin_green_trans)));
                                }
                            }
                        }
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.setTag(updateLocationData);
                        animateMarker(googleMap, marker, start, new LatLng(Double.parseDouble(updateLocationData.getUser_lat()),
                                Double.parseDouble(updateLocationData.getUser_long())
                        ), false);
                        usersMarkerArrayList.add(marker);
                    }
                }
            }
        }
    }

    private void toggleCount() {
        if (updateLocationDataList != null) {
            homeBinding.textViewNearbyUsersCount.setVisibility(View.VISIBLE);
            int counter = 0;
            for (int i = 0; i < updateLocationDataList.size(); i++) {
                if (!updateLocationDataList.get(i).getUser_lat().isEmpty() && !updateLocationDataList.get(i).getUser_long().isEmpty() && !updateLocationDataList.get(i).getUser_distance().isEmpty()) {
                    counter++;
                }
            }
            homeBinding.textViewNearbyUsersCount.setText(String.valueOf(counter));
        }
    }

    private void loadGeoCoderJson(final double latitude, final double longitude, final double radius, final String tag) {
        if (getActivity() != null) {
            if (((MainActivity) getActivity()).sendLocationName)
                ((MainActivity) getActivity()).sendLocationName = false;

            AndroidNetworking.get(EndpointUrls.GEO_CODER_BASE_URL)
                    .addQueryParameter(EndpointKeys.LATLNG, String.valueOf(latitude + " " + longitude))
                    .addQueryParameter(EndpointKeys.SENSOR, String.valueOf(false))
                    .addQueryParameter(EndpointKeys.LANGUAGE, "en")
                    .addQueryParameter(EndpointKeys.KEY, getResources().getString(R.string.google_maps_server_key))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parseGeoCoderJson(response, latitude, longitude, radius, tag);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("GEOCODING", anError.getMessage());
                        }
                    });
        }
    }

    private void parseGeoCoderJson(JSONObject response, double latitude, double longitude, double radius, String tag) {
        if (response != null && response.length() > 0) {
            if (StringMethods.validateKeys(response, EndpointKeys.RESULTS)) {
                try {
                    JSONArray resultArray = response.getJSONArray(EndpointKeys.RESULTS);
                    JSONObject jsonObject = resultArray.getJSONObject(0);
                    String country = Constants.NO;
                    String city = Constants.NO;
                    String street = Constants.NO;
                    String premise = Constants.NO;
                    String street_no = Constants.NO;
                    String route = Constants.NO;
                    String sub_locality_1 = Constants.NO;
                    String sub_localit_2 = Constants.NO;
                    String postalCode = Constants.NO;
                    String province = Constants.NO;
                    String zipCode = Constants.NO;

                    if (StringMethods.validateKeys(jsonObject, EndpointKeys.ADDRESS_COMPONENTS)) {
                        JSONArray addressArray = jsonObject.getJSONArray(EndpointKeys.ADDRESS_COMPONENTS);
                        for (int i = 0; i < addressArray.length(); i++) {
                            JSONObject resultObject = addressArray.getJSONObject(i);
                            if (StringMethods.validateKeys(resultObject, EndpointKeys.TYPES)) {
                                JSONArray typeArray = resultObject.getJSONArray(EndpointKeys.TYPES);
                                for (int j = 0; j < typeArray.length(); j++) {
                                    String type = typeArray.getString(j);
                                    if (type.equals(EndpointKeys.ADMINISTRATIVE_AREA_LEVEL_2)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.SHORT_NAME)) {
                                            city = resultObject.getString(EndpointKeys.SHORT_NAME);
                                            if (tag.contains("City")) {
//                                                homeBinding.textViewCityName.setVisibility(View.VISIBLE);
//                                                homeBinding.textViewCityName.setText(city);
                                            }
                                        }
                                    } else if (type.equals(EndpointKeys.ADMINISTRATIVE_AREA_LEVEL_1)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.SHORT_NAME)) {
                                            province = resultObject.getString(EndpointKeys.SHORT_NAME);
                                        }
                                    } else if (type.equals(EndpointKeys.COUNTRY)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.LONG_NAME)) {
                                            country = resultObject.getString(EndpointKeys.LONG_NAME);
                                        }
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.SHORT_NAME)) {
                                            zipCode = resultObject.getString(EndpointKeys.SHORT_NAME);
                                        }
                                    } else if (type.equals(EndpointKeys.POSTAL_CODE)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.SHORT_NAME)) {
                                            postalCode = resultObject.getString(EndpointKeys.SHORT_NAME);
                                        }
                                    } else if (type.equalsIgnoreCase(EndpointKeys.PREMISE)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.LONG_NAME)) {
                                            premise = resultObject.getString(EndpointKeys.LONG_NAME + ", ");
                                        }
                                    } else if (type.equalsIgnoreCase(EndpointKeys.STREET_NO)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.LONG_NAME)) {
                                            street_no = resultObject.getString(EndpointKeys.LONG_NAME);
                                        }
                                    } else if (type.equalsIgnoreCase(EndpointKeys.ROUTE)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.LONG_NAME)) {
                                            route = resultObject.getString(EndpointKeys.LONG_NAME + ",");
                                        }
                                    } else if (type.equalsIgnoreCase(EndpointKeys.SUB_LOCALITY_2)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.LONG_NAME)) {
                                            sub_localit_2 = resultObject.getString(EndpointKeys.LONG_NAME);
                                        }
                                    } else if (type.equalsIgnoreCase(EndpointKeys.SUB_LOCALITY_1)) {
                                        if (StringMethods.validateKeys(resultObject, EndpointKeys.LONG_NAME)) {
                                            sub_locality_1 = resultObject.getString(EndpointKeys.LONG_NAME);
                                        }
                                    }
                                }
                            }
                        }
                        if (tag.contains("City")) {
//                            homeBinding.textViewCityName.setVisibility(View.VISIBLE);
//                                homeBinding.textViewCityName.setText(city);
                        } else {
                            street = premise + " " + street_no + " " + route + " " + sub_localit_2 + " " + sub_locality_1;
                            JSONObject locationObject = new JSONObject();
                            try {
                                locationObject.put("continent", RegionUtils.getContinentName(zipCode));
                                locationObject.put("street", street);
                                locationObject.put("state", province);
                                locationObject.put("city", city);
                                locationObject.put("country", country);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            reqUpdateLocation(latitude, longitude, radius / 1000, locationObject.toString(), homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), getZoomLevel(googleMap.getCameraPosition().zoom), profileId);
                        }
                    }
                } catch (JSONException e) {
                    if (e.getMessage().equalsIgnoreCase("Index 0 out of range [0..0)")) {
                        if (!tag.equals("City"))
                            reqUpdateLocation(latitude, longitude, radius / 1000, "", homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), getZoomLevel(googleMap.getCameraPosition().zoom), profileId);
                    }
                }
            }
        }
    }

    private void reqSetVisibility(final String visibleTo, final String visibleAt, String profileId) {
        callSetVisibility = Api.WEB_SERVICE.setVisibility(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), visibleTo, visibleAt, profileId);
        callSetVisibility.enqueue(new Callback<SetVisibilityMainObject>() {
            @Override
            public void onResponse(Call<SetVisibilityMainObject> call, Response<SetVisibilityMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            currentProfilel.setVisible_to(visibleTo);
                            currentProfilel.setVisible_at(visibleAt);
                            multiProfileRepositry.updateTask(currentProfilel);
                            final LatLng latLng = googleMap.getCameraPosition().target;
                            reqUsersInRadius(latLng.latitude, latLng.longitude, getMapVisibleRegionRadius(googleMap), homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), false, getZoomLevel(googleMap.getCameraPosition().zoom), profileId);
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<SetVisibilityMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    @Override
    public boolean onClusterClick(Cluster<UpdateLocationData> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        Collection<UpdateLocationData> venueMarkers = cluster.getItems();
        EventBus.getDefault().post(new EventBusUserSession("cluster_tapped"));
        for (ClusterItem item : venueMarkers) {
            LatLng venuePosition = item.getPosition();
            builder.include(venuePosition);
        }

        final LatLngBounds bounds = builder.build();

        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception error) {
        }

        return true;
    }

    @Override
    public void onClusterItemInfoWindowClick(UpdateLocationData updateLocationData) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusProfileClose(EventBusProfileClose eventBusProfileClose) {
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusMapSetting(EventBusMapSettingChanged eventBusMapSettingChanged){
        viewType = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.MAPVIEW, "");
        if (viewType.equals(Constants.GridClicked)) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataList));
                    EventBus.getDefault().post(new EventBusUserSession("grid_view"));
                }
            }, 100);
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onClusterItemClick(final UpdateLocationData updateLocationData) {
        ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
        if (updateLocationData != null) {
            double latitude = Double.parseDouble(updateLocationData.getUser_lat());
            double longitude = Double.parseDouble(updateLocationData.getUser_long());
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            googleMap.setPadding(0, (int) (height - getResources().getDimension(R.dimen._278sdp)), 0, 0);
            if (!((MainActivity) getActivity()).isFragmentVisible(Constants.MAP_PROFILE)) {
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));
                ((MainActivity) getActivity()).fnLoadFragAdd(Constants.MAP_PROFILE, true, null);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventBusSendUsersList(updateLocationDataList, updateLocationData.getIndex()));
                    }
                }, 100);
            } else {
                String currentUserId = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, "");
                if (currentUserId.equals(updateLocationData.getSender_id())) {
                    EventBus.getDefault().post(new EventBusIsFragmentVisible(updateLocationData.getReceiver_id()));
                } else {
                    EventBus.getDefault().post(new EventBusIsFragmentVisible(updateLocationData.getSender_id()));
                }
            }
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().isEmpty()) {
            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
            toggleFilterButtonBackground();
            refreshUsersOnMap(false);
        }
        if (homeBinding.editTextSearch.getText().toString().isEmpty()) {
            homeBinding.imageViewClose.setVisibility(View.GONE);
        } else {
            homeBinding.imageViewClose.setVisibility(View.VISIBLE);
        }
        LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, s.toString()).apply();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
            slideUp.hide();
            LatLng latLng = getLocationFromAddress(homeBinding.editTextSearch.getText().toString());
            if (latLng != null) {
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.99f));
                isSearhClicked = true;
                reqUsersWithOutRadius(homeBinding.editTextSearch.getText().toString().trim(), profileId);
            } else {
                isSearhClicked = true;
                reqUsersWithOutRadius(homeBinding.editTextSearch.getText().toString().trim(), profileId);
                EventBus.getDefault().post(new EventBusUserSession("map_search"));
            }
            return true;
        }
        return false;
    }

    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            if (address.size() > 0) {
                Address location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                return null;
            }
//            final float googleMapCurrentZoomLevel = googleMap.getCameraPosition().zoom;
//            final double radius = getMapVisibleRegionRadius(googleMap);
//            final LatLng centerLatLng = googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
//            final double lat = centerLatLng.latitude;
//            final double lng = centerLatLng.longitude;
//            reqUsersInRadius(centerLatLng.latitude, centerLatLng.longitude, radius, homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), false, getZoomLevel(googleMapCurrentZoomLevel));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return p1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCameraMove() {
        if (!((MainActivity) getActivity()).fnIsisOnline()) {
            return;
        }
        if (cameraDragTimer != null) {
            cameraDragTimer.purge();
            cameraDragTimer.cancel();
        }
        cameraDragTimer = new Timer();
        final double radius = getMapVisibleRegionRadius(googleMap);
        final LatLng centerLatLng = googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        final double lat = centerLatLng.latitude;
        final double lng = centerLatLng.longitude;

        final float googleMapCurrentZoomLevel = googleMap.getCameraPosition().zoom;
        if (!isTrackStarted)
            homeBinding.seekBar.setProgress((int) (googleMapCurrentZoomLevel * 5));

        final Handler handler = new Handler();
        if (cameraDragTimer != null)
            cameraDragTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    loadGeoCoderJson(lat, lng, radius, "City");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (String.valueOf(radius).length() > 4)
                                    homeBinding.textViewRadius.setText(String.valueOf(radius).substring(0, 4) + " miles");
                            } catch (Exception e) {

                            }
                        }
                    });
                    if (mFlag.equalsIgnoreCase("near_you") && fromNotification) {
                        fromNotification = false;
                    } else {
                        reqUsersInRadius(centerLatLng.latitude, centerLatLng.longitude, radius, homeBinding.editTextSearch.getText().toString(), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, ""), false, getZoomLevel(googleMapCurrentZoomLevel), profileId);
                    }
                    cameraDragTimer.cancel();
                }
            }, 1000);
        counter = 0;
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == REASON_GESTURE) {
            shouldMapCameraChange = false;
        }
    }

    @Override
    public void onSensorChange(float degree) {
//        if (currentUserLatLng != null) {
        if (googleMap != null) {
            LatLng centerLatLng = googleMap.getCameraPosition().target;
            if (centerLatLng != null) {
                CameraPosition pos = CameraPosition.builder().target(centerLatLng).zoom(googleMap.getCameraPosition().zoom).bearing(degree).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                //        homeBinding.seekBar.setProgress((int) (googleMap.getCameraPosition().zoom*5));
            }
        }
//        }
    }

    public class HomeClickHandler {

        public void onFilterClick(View view) {
            ((MainActivity) getActivity()).fnHideKeyboardForcefully(homeBinding.getRoot());
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.MAP_FILTER, true, null);
        }

        public void onGridClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.HOME_GRID, true, null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new EventBusGridUsersList(updateLocationDataList));
                    EventBus.getDefault().post(new EventBusUserSession("grid_view"));
                }
            }, 100);
        }

        public void onCloseClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
            homeBinding.editTextSearch.setText("");
//            LnqApplication.getInstance().editor.putString(EndpointKeys.SEARCH_TEXT, "").apply();
//            LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
//            toggleFilterButtonBackground();
//            refreshUsersOnMap(false);
        }

        public void onZoomOutButtonClick(View view) {
            googleMap.animateCamera(CameraUpdateFactory.zoomOut());
        }

        public void onZoomInButtonClick(View view) {
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        }

        public void onMyLocationClick(View view) {
            if (counter == 0) {
                if (currentUserLatLng != null) {
                    shouldMapCameraChange = true;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentUserLatLng, 10.0f);
                    googleMap.animateCamera(cameraUpdate);
                }
                counter++;
            } else {
                homeBinding.compassLayout.startAnimation(mSlideUpAnimation);
                homeBinding.compassLayout.setVisibility(View.VISIBLE);
                registerCompassListener();
                homeBinding.buttonCompass.setImageResource(R.drawable.orient_me_icon);
                counter--;
            }
        }

        public void onCompassLockClick(View view) {
            if (isCompassEnable) {
                if (sensorManager != null) {
                    sensorManager.unregisterListener(sensorEventListener);
                }
                homeBinding.buttonCompass.setImageResource(R.drawable.orient_me_icon);
                homeBinding.compassLayout.startAnimation(mSlideDownAnimation);
                homeBinding.compassLayout.setVisibility(View.INVISIBLE);
            } else {
                registerCompassListener();
                homeBinding.buttonCompass.setImageResource(R.drawable.orient_me_icon);
            }
            isCompassEnable = !isCompassEnable;
        }

        public void onSearchClick(View view) {

//            if (getLocationFromAddress(homeBinding.editTextSearch.getText().toString()) != null){
//
//                Double lat = getLocationFromAddress()
//                Double long = getLocationFromAddress(homeBinding.editTextSearch.getText().toString()).get;
//
//
//            }else {

            ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
//            refreshUsersOnMap(false);
            reqUsersWithOutRadius(homeBinding.editTextSearch.getText().toString().trim(), profileId);
        }


        public void onQrCodeClick(View view) {
            ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
//            if (((MainActivity) getActivity()).fnCheckCameraPermission()) {
            ((MainActivity) getActivity()).fnLoadFragAdd("SHARE QR CODE", true, null);
            EventBus.getDefault().post(new EventBusUserSession("QrCode_clicked"));
//            } else {
//                ((MainActivity) getActivity()).fnRequestCameraPermission(8);
//            }
        }

        public void onMenuClick(View view) {
            ((MainActivity) getActivity()).fnLoadFragAdd(Constants.POPUP_MAP_TYPES, true, null);
        }

    }


    private void unFreeze(String unFreeze) {
        ((MainActivity) getActivity()).progressDialog.show();
//        callUnFrozen = Api.WEB_SERVICE.unFreeze(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), unFreeze);
        callUnFrozen = Api.WEB_SERVICE.unFreeze(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), unFreeze);
        callUnFrozen.enqueue(new Callback<RegisterLoginMainObject>() {
            @Override
            public void onResponse(Call<RegisterLoginMainObject> call, Response<RegisterLoginMainObject> response) {
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            LnqApplication.getInstance().sharedPreferences.edit().putString(EndpointKeys.IS_FROZEN, "0").apply();
                            EventBus.getDefault().post(new EventBusUserSession("unfreeze_account"));
                            dialog.dismiss();
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    private void reqGetUserProfile(final String userId, String senderProfileid, String receiverProfileid) {
//        callGetUserProfile = Api.WEB_SERVICE.getUserProfile(EndpointKeys.X_API_KEY, DateUtils.getCurrentTime(), Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId);
        callGetUserProfile = Api.WEB_SERVICE.getUserActiveProfile(EndpointKeys.X_API_KEY, Credentials.basic(LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_EMAIL, ""), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_PASSWORD, "")), LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.ID, ""), userId, senderProfileid, receiverProfileid);
        callGetUserProfile.enqueue(new Callback<GetUserProfileMainObject>() {
            public void onResponse(Call<GetUserProfileMainObject> call, Response<GetUserProfileMainObject> response) {
                if (response != null && response.isSuccessful()) {
                    switch (response.body().getStatus()) {
                        case 1:
                            if (!response.body().getGetUserProfile().getUser_lat().equalsIgnoreCase("")
                                    && !response.body().getGetUserProfile().getUser_long().equalsIgnoreCase("")) {
                                double userLat = Double.parseDouble(response.body().getGetUserProfile().getUser_lat());
                                double userLong = Double.parseDouble(response.body().getGetUserProfile().getUser_long());
                                double userRadius = getMapVisibleRegionRadius(googleMap);
                                String userSearch = response.body().getGetUserProfile().getUser_fname() + " " + response.body().getGetUserProfile().getUser_lname();
                                String userFilter = LnqApplication.getInstance().sharedPreferences.getString(EndpointKeys.USER_FILTERS, "");
                                String zoomLevel = response.body().getGetUserProfile().getVisible_at();
                                reqUsersInRadius(userLat, userLong, userRadius, userSearch, userFilter, false, zoomLevel, profileId);
                                EventBus.getDefault().post(new EventBusUserZoomLevel(userId, userLat, userLong, zoomLevel));
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileMainObject> call, Throwable error) {
                if (call.isCanceled() || "Canceled".equals(error.getMessage())) {
                    return;
                }
                ((MainActivity) getActivity()).progressDialog.dismiss();
                if (error != null) {
                    if (error.getMessage() != null && error.getMessage().contains("No address associated with hostname")) {
                        ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                    } else {
                        ValidUtils.showCustomToast(getContext(), "Poor internet connection");
                    }
                } else {
                    ValidUtils.showCustomToast(getContext(), "Network connection was lost");
                }
            }
        });
    }

    private void changeSelection(AppCompatCheckBox compatCheckBox) {
        compatCheckBox.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewFavorites:
                homeBinding.checkBoxFavourites.setChecked(!userFilter.contains(Constants.FAVORITES));
                break;
            case R.id.textViewOutstandingTasks:
                homeBinding.checkBoxOutstandingTasks.setChecked(!userFilter.contains(Constants.OUTSTANDING_TASKS));
                break;
            case R.id.textViewPendingLnq:
                homeBinding.checkBoxPendingLNQs.setChecked(!userFilter.contains(Constants.PENDING_LNQS));
                break;
            case R.id.textViewVerifiedProfiles:
                homeBinding.checkBoxVerifiedProfile.setChecked(!userFilter.contains(Constants.VERIFIED_PROFILE));
                break;
            case R.id.mTvAccountHeading1:
            case R.id.imageViewBack:
                slideUp.hide();
                break;
            case R.id.viewCloseFilter:
                getActivity().onBackPressed();
                break;
            case R.id.mBtnApply:
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, userFilter.toString().replace(", ", ", ").replaceAll("[\\[.\\]]", "")).apply();
                EventBus.getDefault().post(new EventBusUpdateFilters());
                EventBus.getDefault().post(new EventBusUserSession("map_filter"));
                ValidUtils.hideKeyboardFromFragment(getActivity(), homeBinding.getRoot());
                slideUp.hide();
                LatLng latLng = getLocationFromAddress(homeBinding.editTextSearch.getText().toString());
                if (latLng != null) {
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.99f));
                    isSearhClicked = true;
                    reqUsersWithOutRadius(homeBinding.editTextSearch.getText().toString().trim(), profileId);
                } else {
                    isSearhClicked = true;
                    reqUsersWithOutRadius(homeBinding.editTextSearch.getText().toString().trim(), profileId);
                    EventBus.getDefault().post(new EventBusUserSession("map_search"));
                }
                break;
            case R.id.textViewClearAll:
                userFilter.clear();
                LnqApplication.getInstance().editor.putString(EndpointKeys.USER_FILTERS, "").apply();
                homeBinding.checkBoxFavourites.setChecked(false);
                homeBinding.checkBoxOutstandingTasks.setChecked(false);
                homeBinding.checkBoxPendingLNQs.setChecked(false);
                homeBinding.checkBoxVerifiedProfile.setChecked(false);
                EventBus.getDefault().post(new EventBusUpdateFilters());
                break;
        }
    }

    private void isChecked(String key, boolean isSelected) {
        if (isSelected) {
            userFilter.add(key);
        } else {
            userFilter.remove(key);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == homeBinding.checkBoxFavourites) {
            isChecked(Constants.FAVORITES, homeBinding.checkBoxFavourites.isChecked());
        } else if (buttonView == homeBinding.checkBoxOutstandingTasks) {
            isChecked(Constants.OUTSTANDING_TASKS, homeBinding.checkBoxOutstandingTasks.isChecked());
        } else if (buttonView == homeBinding.checkBoxPendingLNQs) {
            isChecked(Constants.PENDING_LNQS, homeBinding.checkBoxPendingLNQs.isChecked());
        } else if (buttonView == homeBinding.checkBoxVerifiedProfile) {
            isChecked(Constants.VERIFIED_PROFILE, homeBinding.checkBoxVerifiedProfile.isChecked());
        }
    }
}