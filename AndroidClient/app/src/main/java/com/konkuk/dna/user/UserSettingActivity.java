package com.konkuk.dna.user;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.support.v7.widget.SwitchCompat;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.konkuk.dna.MainActivity;
import com.konkuk.dna.auth.LoginActivity;
import com.konkuk.dna.friend.message.DMMessage;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.ServerURL;
import com.konkuk.dna.utils.SocketConnection;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.konkuk.dna.map.MapFragment;

import java.util.ArrayList;

import static com.konkuk.dna.utils.HttpReqRes.requestHttpPutSetting;
import static com.konkuk.dna.utils.ObjToJson.StoreObjToJson;

public class UserSettingActivity extends BaseActivity {
    protected DrawerLayout menuDrawer;
    private MapFragment mapFragment;
    private SeekBar radiusSeekbar;
    private TextView radiusText;
    private SwitchCompat isAnonymity, isFindable;
    private Spinner bestChatCycle;
    private int radius;
    private int int_anony, int_search;
    private boolean isanony, issearchable;
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
        isanony = dbhelper.getMyAnonymity()==1 ? true : false;
        issearchable = dbhelper.getMySearchable()==1 ? true : false;
        int_anony = dbhelper.getMyAnonymity();
        int_search = dbhelper.getMySearchable();

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

        isAnonymity.setChecked(isanony);
        isAnonymity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    isanony = true;
                    int_anony = 1;
                }else{
                    isanony = false;
                    int_anony = 0;
                }
            }
        });

        isFindable.setChecked(issearchable);
        isFindable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    issearchable = true;
                    int_search = 1;
                }else{
                    issearchable = false;
                    int_search = 0;
                }
            }
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
                dbhelper.updateAnonymity(int_anony);
                dbhelper.updateSearchable(int_search);

                JsonObject storeJson = StoreObjToJson(dbhelper, gpsTracker.getLongitude(), gpsTracker.getLatitude());
                SocketConnection.emit("store", storeJson);

                SettingAsyncTask sat = new SettingAsyncTask(this);
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                    sat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, radius, int_anony, int_search);
                }else{
                    sat.execute(radius, int_anony, int_search);
                }

                Intent intent = new Intent(UserSettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //menuDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                //InitHelpers.initDrawer(this, menuDrawer, 2);
                //InitHelpers.updateDrawer(this, menuDrawer);

                //finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.initMapCenter(longitude, latitude, radius);
    }
}

class SettingAsyncTask extends AsyncTask<Integer, Integer, String> {

    private Context context;
    private String m_token;
    private Dbhelper dbhelper;
    private int radius;
    private int anonymity;
    private int searchable;

    public SettingAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... integers) {
        HttpReqRes httpreq = new HttpReqRes();
        dbhelper = new Dbhelper(context);
        m_token = dbhelper.getAccessToken();
        radius = integers[0];
        anonymity = integers[1];
        searchable = integers[2];

        String result = httpreq.requestHttpPutSetting(ServerURL.DNA_SERVER+ServerURL.PORT_USER_API+"/users/setting"
                , m_token, radius, anonymity, searchable);

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}