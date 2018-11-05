package com.konkuk.dna.user;

import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.support.v7.widget.SwitchCompat;
import android.widget.Spinner;
import android.widget.TextView;

import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.konkuk.dna.map.MapFragment;

public class UserSettingActivity extends BaseActivity {
    protected DrawerLayout menuDrawer;
    private MapFragment mapFragment;
    private SeekBar radiusSeekbar;
    private TextView radiusText;
    private SwitchCompat isAnonymity, isFindable;
    private Spinner bestChatCycle;
    private int radius;
    private double longitude, latitude;

    private Dbhelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        init();
    }

    public void init() {
        menuDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 2);

        // TODO 반경, 위치 초기값 설정해줘야 합니다!
        dbhelper = new Dbhelper(this);
        radius = dbhelper.getMyRadius();
        longitude = gpsTracker.getLongitude();
        latitude = gpsTracker.getLatitude();

        // TODO switch 메뉴들 기존 값으로 초기화해줘야 합니다.
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.chatMapFragment);

        isAnonymity = (SwitchCompat) findViewById(R.id.isAnonymity);
        isFindable = (SwitchCompat) findViewById(R.id.isFindable);
        bestChatCycle = (Spinner) findViewById(R.id.bestChatCycle);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.best_chat, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bestChatCycle.setAdapter(arrayAdapter);

        radiusText = (TextView) findViewById(R.id.radiusText);
        radiusText.setText(radius +"");

        radiusSeekbar = (SeekBar) findViewById(R.id.radiusSeekbar);
        radiusSeekbar.setProgress(radius);
        radiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radius = i;
                radiusText.setText(radius + "");
                mapFragment.updateRadiusCircle(longitude, latitude, radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logoutBtn:
                dbhelper.logoutUser();
                //TODO 실행중인 모든 액티비티를 종료하고 로그인액티비티로 이동해야함.
                break;

            case R.id.backBtn:
                finish();
                break;

            case R.id.menuBtn: // 메뉴 버튼 클릭
                if (!menuDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    menuDrawer.openDrawer(Gravity.RIGHT);
                }
                break;

            case R.id.settingSaveBtn: // 저장 버튼 클릭
                dbhelper.updateRadius(radius);

                //TODO: Drawer에 적혀있는 '현재 채팅 환경' update가 작동하지 않음.
                //InitHelpers.initDrawer(this, menuDrawer, 2);
                //InitHelpers.updateDrawer(this, menuDrawer);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.initMapCenter(longitude, latitude, radius);
    }
}
