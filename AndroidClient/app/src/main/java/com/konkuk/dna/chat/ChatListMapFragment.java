package com.konkuk.dna.chat;

import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konkuk.dna.R;
import com.konkuk.dna.map.NMapPOIflagType;
import com.konkuk.dna.map.NMapViewerResourceProvider;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapProjection;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapCircleData;
import com.nhn.android.maps.overlay.NMapCircleStyle;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
//public class ChatListMapFragment extends Fragment
public class ChatListMapFragment extends DialogFragment
{
    private NMapContext mapContext;
    private NMapView mapView;
    private NMapController mapController;

    private NMapPOIdata poiData;
    private NMapOverlayManager mOverlayManager;
    private NMapResourceProvider mMapViewerResourceProvider;

    private static final String CLIENT_ID = "d58JXyIkF7YXEmOLrYSD"; // 애플리케이션 클라이언트 아이디 값

    public ChatListMapFragment() {}

    public static ChatListMapFragment newInstance(double lng, double lat) {
        ChatListMapFragment f = new ChatListMapFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putDouble("lng", lng);
        args.putDouble("lat", lat);
        f.setArguments(args);

        return f;
    }

    public double getLng() {
        if (getArguments() != null) {
            return getArguments().getDouble("lng", 0);
        }
        return 0;
    }

    public double getLat() {
        if (getArguments() != null) {
            return getArguments().getDouble("lat", 0);
        }
        return 0;
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

        init();
    }

    public void init() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = (NMapView)getView().findViewById(R.id.mapView);
        mapView.setClientId(CLIENT_ID);
        mapContext.setupMapView(mapView);
        mapController = mapView.getMapController();
        Log.d("test 1", mapContext.toString());
        Log.d("test 2", mapController.toString());
        Log.d("test 3", mapView.toString());

        mMapViewerResourceProvider = new NMapViewerResourceProvider(getActivity());
        poiData = new NMapPOIdata(0, mMapViewerResourceProvider);
        mOverlayManager = new NMapOverlayManager(getActivity(),mapView,mMapViewerResourceProvider);
    }

    @Override
    public void onStart(){
        super.onStart();
        mapContext.onStart();
        mapView.setClickable(false);
        mapView.displayZoomControls(false);
        mapView.setEnabled(false);
        mapController.setMapCenter(new NGeoPoint(getLng(), getLat()), 11);
        mapController.setZoomLevel(11);

        updatePositionMarker(getLng(), getLat());
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

    @Override
    public void onResume() {
        super.onResume();
        mapContext.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapContext.onPause();
    }
    @Override
    public void onStop() {
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
}
