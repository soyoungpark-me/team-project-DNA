package com.konkuk.dna.post;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.konkuk.dna.MainActivity;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.konkuk.dna.map.MapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostFormActivity extends BaseActivity {
    private DrawerLayout menuDrawer;
    private MapFragment mapFragment;
    private EditText postTitleEdit, postContentEdit;
    private SwitchCompat isOnlyMe;
    private double longitude, latitude;
    private Post post;
    private boolean isChecked = false;
    private Date dt;
    private SimpleDateFormat sdf;

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
        isOnlyMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.getId() == R.id.isOnlyMe){
                    if(isChecked){
                        isChecked = false;
                    }
                    else{
                        isChecked = true;
                    }
                }
            }
        });

        // TODO 위치 초기값 설정해줘야 합니다!
        longitude = gpsTracker.getLongitude();
        latitude = gpsTracker.getLatitude();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        post = new Post();
//        isChecked = false;
        dt = new Date();
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
                post.setTitle(postTitleEdit.getText().toString());
                post.setContent(postContentEdit.getText().toString());
                post.setLongitude(longitude);
                post.setLatitude(latitude);
                post.setOnlyme(isChecked);
                post.setDate(sdf.format(dt).toString());

                new writePostingAsync(this).execute(post);
                Intent newIntent = new Intent(this, MainActivity.class);
                startActivity(newIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.initMapCenter(longitude, latitude, 0);
    }
}

class writePostingAsync extends AsyncTask<Post, Post, String> {
    private Context context;
    private Dbhelper dbhelper;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    public writePostingAsync(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Post... posts){
        HttpReqRes httpReqRes = new HttpReqRes();
        dbhelper = new Dbhelper(context);
        try{
            httpReqRes.requestHttpPostWritePosting("https://dna.soyoungpark.me:9013/api/posting/", dbhelper, posts[0]);
        }finally {
        }
        dbhelper.close();
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        super.onPostExecute(result);
    }
}