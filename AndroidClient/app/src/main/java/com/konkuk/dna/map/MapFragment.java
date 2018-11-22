package com.konkuk.dna.map;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.konkuk.dna.MainActivity;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.GPSTracker;
import com.konkuk.dna.R;
import com.konkuk.dna.post.Post;
import com.konkuk.dna.post.PostDetailActivity;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapCircleData;
import com.nhn.android.maps.overlay.NMapCircleStyle;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.konkuk.dna.utils.JsonToObj.PostingCntJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.PostingJsonToObj;
import static com.konkuk.dna.utils.helpers.InitHelpers.updateDrawer;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment
        implements NMapView.OnMapStateChangeListener, NMapPOIdataOverlay.OnStateChangeListener, NMapLocationManager.OnLocationChangeListener
{
    private NMapContext mapContext;
    private NMapView mapView;
    private NMapController mapController;
    private MapContainerView mMapContainerView;

    private NMapOverlayManager mOverlayManager;
    private NMapResourceProvider mMapViewerResourceProvider;

    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;

    private NMapPOIdata poiData;
    private NMapCircleData circleData;
    private NMapCircleStyle circleStyle;
    private NMapPlacemark placemark;
    private JSONObject centerPosition;
    private GPSTracker gpsTracker;

    private LinearLayout mapLocBtn;

    private static final String CLIENT_ID = "d58JXyIkF7YXEmOLrYSD"; // 애플리케이션 클라이언트 아이디 값

    public MapFragment() {}

    public JSONObject getCenterPosition() {
        return this.centerPosition;
    }

    public JSONObject getMarkerPosition() {
        JSONObject markerPostition = new JSONObject();
        NGeoPoint nGeoPoint = poiData.getPOIitem(0).getPoint();

        try {
            markerPostition.put("longitude", nGeoPoint.longitude);
            markerPostition.put("latitude", nGeoPoint.latitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return markerPostition;
    }

    public void initMapCenter(double lng, double lat, float radius) {
        updatePositionMarker(lng, lat);
        updateRadiusCircle(lng, lat, radius);
        mapController.setZoomLevel(11);
    }

    public void drawPostLocations(final ArrayList<Post> posts) {
        NMapPOIdata postPoiData = new NMapPOIdata(1, mMapViewerResourceProvider);

        for(Post post: posts) {
            NMapPOIitem item = postPoiData.addPOIitem(post.getLongitude(), post.getLatitude(), post.getTitle(),
                    NMapPOIflagType.POST, postPoiData.count());
            item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
        }

        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(postPoiData, null);
        poiDataOverlay.setOnStateChangeListener(new NMapPOIdataOverlay.OnStateChangeListener() {
            @Override
            public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {}

            @Override
            public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
                Log.v("mapgragment", "posting idx? : " + posts.get(nMapPOIitem.getId()).getPostingIdx());

//                Intent postIntent = new Intent(getActivity(), PostDetailActivity.class);
//                postIntent.putExtra("post", (Post) posts.get(nMapPOIitem.getId()));
//                postIntent.putExtra("pidx", posts.get(nMapPOIitem.getId()).getPostingIdx());
//                getActivity().startActivity(postIntent);
//                nMapPOIdataOverlay.setHidden(true);
                Intent postIntent = new Intent(getActivity(), PostDetailActivity.class);
                postIntent.putExtra("post", (Post) posts.get(nMapPOIitem.getId()));
//                postIntent.putExtra("pidx", (Integer) posts.get(nMapPOIitem.getId()).getPostingIdx());
                getActivity().startActivity(postIntent);
                nMapPOIdataOverlay.setHidden(true);
            }

        });
    }

    public void updatePositionMarker(double lng, double lat) {
        if (poiData != null && poiData.getPOIitem(0) != null) {
            poiData.getPOIitem(0).setPoint(new NGeoPoint(lng, lat));
        } else {
            if (getActivity().getClass().getSimpleName().equals("PostDetailActivity")) {
                poiData.addPOIitem(lng, lat, "", NMapPOIflagType.PIN, 0);
            } else if (getActivity().getClass().getSimpleName().equals("PostFormActivity")) {
                NMapPOIitem item = poiData.addPOIitem(lng, lat, "", NMapPOIflagType.PIN, 0);
                item.setPoint(mapController.getMapCenter());
                item.setFloatingMode(NMapPOIitem.FLOATING_TOUCH | NMapPOIitem.FLOATING_DRAG);
            } else {
                poiData.addPOIitem(lng, lat, "", NMapPOIflagType.SPOT, 0);
            }
        }

        poiData.endPOIdata();

        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        mapController.setMapCenter(new NGeoPoint(lng, lat), 11);
    }

    public void updateRadiusCircle(double lng, double lat, float radius) {
        mOverlayManager.clearOverlays();
        updatePositionMarker(lng, lat);
        circleData = new NMapCircleData(1);
        circleData.initCircleData();
        circleData.addCirclePoint(lng, lat, radius);
        circleData.setCircleStyle(circleStyle);
        NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay();
        pathDataOverlay.addCircleData(circleData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapContext = new NMapContext(super.getActivity());
        mapContext.onCreate();

        centerPosition = new JSONObject();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = (NMapView)getView().findViewById(R.id.mapView);
        mapView.setClientId(CLIENT_ID);
        mapContext.setupMapView(mapView);
        mapLocBtn = (LinearLayout) getView().findViewById(R.id.mapLocationBtn);
    }

    @Override
    public void onStart(){
        super.onStart();
        mapContext.onStart();

        mapView.setClickable(true);
        mapView.displayZoomControls(true);
        mapView.setEnabled(true);

        // DetailView에서 만들었을 경우에는 클릭하지 못하게 막습니다. (스크롤 문제)
        if (getActivity().getClass().getSimpleName().equals("PostDetailActivity")) {
            mapView.setClickable(false);
        } else {
            mapView.setClickable(true);
            mapView.setOnMapStateChangeListener(OnMapViewStateChangeListener); //리스너 등록
        }
        mMapViewerResourceProvider = new NMapViewerResourceProvider(getActivity());
        mOverlayManager = new NMapOverlayManager(getActivity(),mapView,mMapViewerResourceProvider);

        mapController = mapView.getMapController();
        mMapContainerView = new MapContainerView(getActivity());
        mMapLocationManager = new NMapLocationManager(getActivity());
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        mMapCompassManager = new NMapCompassManager(getActivity());
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
        circleData = new NMapCircleData(0);
        circleData.initCircleData();
        circleStyle = new NMapCircleStyle(getActivity());
        circleStyle.setStrokeColor(getResources().getColor(R.color.grayLight), 50);
        circleStyle.setStrokeWidth(0.5F);
        circleStyle.setFillColor(getResources().getColor(R.color.red), 50);
        circleData.setCircleStyle(circleStyle);

        OnMapViewStateChangeListener.onMapInitHandler(mapView, null);

        mapLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dbhelper dbhelper = new Dbhelper(getActivity());
                gpsTracker = new GPSTracker(getActivity());
                updateRadiusCircle(gpsTracker.getLongitude(), gpsTracker.getLatitude(), dbhelper.getMyRadius());

//                ArrayList<Post> posts = null;
//                try {
//                    posts = new showPostingAll2Async(getActivity()).execute().get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
                MainActivity ac = (MainActivity) getActivity();
                drawPostLocations(ac.getPosts());

            }
        });

        mapContext.setMapDataProviderListener(onDataProviderListener);
        gpsTracker = new GPSTracker(getActivity());
        mapContext.findPlacemarkAtLocation(gpsTracker.getLongitude(), gpsTracker.getLatitude());

    }


    private void startMyLocation() {
        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }

            if (mMapLocationManager.isMyLocationEnabled()) {
                if (!mapView.isAutoRotateEnabled()) {
                    mMyLocationOverlay.setCompassHeadingVisible(true);
                    mMapCompassManager.enableCompass();
                    mapView.setAutoRotateEnabled(true, false);
                    mMapContainerView.requestLayout();
                } else {
                    stopMyLocation();
                }

                mapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(getActivity(), "위치를 활성화해야 이용하실 수 있습니다.",
                            Toast.LENGTH_LONG).show();
                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
            }
        }
    }

    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mapView.setAutoRotateEnabled(false, false);

                mMapContainerView.requestLayout();
            }
        }
    }


    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
            if (mapController!= null) {
                mapController.animateTo(myLocation);
            }
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
            Runnable runnable = new Runnable() {
                public void run() {
                    stopMyLocation();
                }
            };
            runnable.run();
            Toast.makeText(getActivity(), "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            stopMyLocation();
        }
    };

    public NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {
        @Override
        public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) {
            Dbhelper dbhelper = new Dbhelper(getActivity());
            if(nMapPlacemark!=null) {
                dbhelper.updateAddress(nMapPlacemark.toString());
                Log.e("LocationListen", nMapPlacemark.toString());
            }

        }
    };

    public NMapView.OnMapStateChangeListener OnMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            if (nMapError == null) {
                mapController.setZoomLevel(11);
            } else {
                Log.d("초기화 중 에러 발생", nMapError.toString());
            }
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
            try {
                centerPosition.put("longitude", nGeoPoint.longitude);
                centerPosition.put("latitude", nGeoPoint.latitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {}

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {}

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {}
    };

    @Override
    public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
        Log.d("MapFragment_test", nGeoPoint.toString());

        return true;
    }

    @Override
    public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {}
    @Override
    public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {}

    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }

            if (changed) {
                mOverlayManager.onSizeChanged(width, height);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpecWidth = widthMeasureSpec;
            int sizeSpecHeight = heightMeasureSpec;

            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);

                if (view instanceof NMapView) {
                    if (mapView.isAutoRotateEnabled()) {
                        int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
                        sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
                        sizeSpecHeight = sizeSpecWidth;
                    }
                }

                view.measure(sizeSpecWidth, sizeSpecHeight);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapContext.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapContext.onPause();
        mOverlayManager.clearOverlays();
    }
    @Override
    public void onStop() {
        stopMyLocation();
        mapContext.onStop();
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        mapContext.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {}

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {}

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {}

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {}

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {}

    @Override
    public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {}

    @Override
    public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {return;}
}


class showPostingAll2Async extends AsyncTask<Void, Void, ArrayList<Post>> {

    private Context context;
    private Dbhelper dbhelper;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public showPostingAll2Async(Context context){
        this.context = context;
    }

    @Override
    protected ArrayList<Post> doInBackground(Void... voids){
        ArrayList<Post> postings = new ArrayList<>();
        int[] pidx;

        HttpReqRes httpReqRes = new HttpReqRes();
        dbhelper = new Dbhelper(context);

        String result = httpReqRes.requestHttpGetWASPIwToken("https://dna.soyoungpark.me:9013/api/posting/showAll/", dbhelper.getAccessToken());

//        Log.v("mainactivity", "show allr httpreq result" + result);
        pidx = PostingCntJsonToObj(result);

        for(int i=0;i<pidx.length;i++){
            String result1 = httpReqRes.requestHttpGetPosting("https://dna.soyoungpark.me:9013/api/posting/show/" + pidx[i]);
            postings.add(PostingJsonToObj(result1, 2).get(0));

        }
//        postings = PostingJsonToObj(result, 2);

        return postings;
    }

    @Override
    protected void onPostExecute(ArrayList<Post> postings) {

        super.onPostExecute(postings);
    }
}
