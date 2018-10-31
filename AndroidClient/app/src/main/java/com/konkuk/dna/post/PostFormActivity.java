package com.konkuk.dna.post;

import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.konkuk.dna.map.MapFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class PostFormActivity extends BaseActivity {
    private DrawerLayout menuDrawer;
    private MapFragment mapFragment;
    private EditText postTitleEdit, postContentEdit;
    private SwitchCompat isOnlyMe;
    private double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_form);

        init();
    }

    public void init() {
        menuDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 2);

        postTitleEdit = (EditText) findViewById(R.id.postTitleEdit);
        postContentEdit = (EditText) findViewById(R.id.postContentEdit);
        isOnlyMe = (SwitchCompat) findViewById(R.id.isOnlyMe);

        // TODO 위치 초기값 설정해줘야 합니다!
        longitude = 127.07934279999995;
        latitude = 37.5407625;
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;

            case R.id.menuBtn: // 메뉴 버튼 클릭
                if (!menuDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    menuDrawer.openDrawer(Gravity.RIGHT);
                }
                break;

            case R.id.saveBtn: // 저장 버튼 클릭
                // 위치 정보는 아래와 같이 저장됩니다.
                JSONObject position = mapFragment.getMarkerPosition();
                try {
                    Log.d("test", "lat :"  + position.get("latitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.initMapCenter(longitude, latitude, 0);
    }
}
