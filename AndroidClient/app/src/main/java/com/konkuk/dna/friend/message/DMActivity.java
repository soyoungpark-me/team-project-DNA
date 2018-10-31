package com.konkuk.dna.friend.message;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DMActivity extends BaseActivity {
    private DrawerLayout menuDrawer;

    private ListView dmListView;
    private EditText dmEditText;
    private TextView updatedAtText;
    private Button dmLocationBtn, dmImageBtn;
    private ArrayList<DMMessage> dmMessages;
    private DMListAdapter dmListAdapter;
    private int roomIdx;

    private SimpleDateFormat timeFormat;

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";     // 일반 메시지 전송
    private final String TYPE_LOCATION = "Location";   // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";         // 이미지 전송
    private final String TYPE_SHARE = "Share";         // 포스팅 공유
    private String messageType = TYPE_MESSAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_dm);

        init();
    }

    public void init() {
        menuDrawer = findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 1);

        dmListView = (ListView) findViewById(R.id.dmListView);
        dmEditText = (EditText) findViewById(R.id.dmEditText);
        dmLocationBtn = (Button) findViewById(R.id.dmLocationBtn);
        dmImageBtn = (Button) findViewById(R.id.dmImageBtn);
        updatedAtText = (TextView) findViewById(R.id.updatedAtText);

        dmMessages = new ArrayList<DMMessage>();
        roomIdx = getIntent().getIntExtra("roomIdx", -1);

        if (roomIdx != -1) {
            updatedAtText.setText(getIntent().getStringExtra("roomUpdated"));

            // TODO dmMessages 배열에 실제 메시지 추가해야 합니다. roomIdx로 가져오면 됩니다.
            dmMessages.add(new DMMessage(1, "http://file3.instiz.net/data/cached_img/upload/2018/06/22/14/2439cadf98e7bebdabd174ed41ca0849.jpg", "오후 12:34", TYPE_IMAGE));
            dmMessages.add(new DMMessage(0, "내용내용", "오후 12:34", TYPE_MESSAGE));
            dmMessages.add(new DMMessage(0, "내용내용내용내용내용", "오후 12:34", TYPE_MESSAGE));
            dmMessages.add(new DMMessage(0, "https://pbs.twimg.com/media/DbYfg2IWkAENdiS.jpg", "오후 12:34", TYPE_IMAGE));
            dmMessages.add(new DMMessage(1, "내용내용내용", "오후 12:34", TYPE_MESSAGE));
            dmMessages.add(new DMMessage(0, "내용내용내용", "오후 12:34", TYPE_MESSAGE));
            dmMessages.add(new DMMessage(1, "{\"lat\":37.550544099999996,\"lng\":127.07221989999998}", "오후 12:34", TYPE_LOCATION));
            dmMessages.add(new DMMessage(1, "http://www.ohfun.net/contents/article/images/2016/0830/1472551795750578.jpeg", "오후 12:34", TYPE_IMAGE));
            dmMessages.add(new DMMessage(0, "내용내용333", "오후 12:34", TYPE_SHARE));
        }

        dmListAdapter = new DMListAdapter(this, R.layout.chat_item_left, dmMessages);
        dmListView.setAdapter(dmListAdapter);

        // 생성된 후 바닥으로 메시지 리스트를 내려줍니다.
        scrollMyListViewToBottom();

        timeFormat = new SimpleDateFormat("a h:m", Locale.KOREA);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.backBtn:
                finish();
                break;

            case R.id.menuBtn: // 메뉴 버튼 클릭
                if (!menuDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    menuDrawer.openDrawer(Gravity.RIGHT);
                }
                break;

            case R.id.dmLocationBtn: // 장소 전송 버튼 클릭
                // TODO 현재 주소를 messageEditText에 채워줍니다.
                if (messageType.equals(TYPE_MESSAGE)) {
                    dmLocationBtn.setTextColor(getResources().getColor(R.color.colorRipple));
                    dmEditText.setText("서울시 광진구 화양동 1 건국대학교");
                    dmEditText.setEnabled(false);
                    messageType = TYPE_LOCATION;
                } else {
                    DialogSimple();
                    messageType = TYPE_MESSAGE;
                }
                break;

            case R.id.dmImageBtn: // 이미지 전송 버튼 클릭
                // TODO 현재 주소를 messageEditText에 채워줍니다.
                if (messageType.equals(TYPE_MESSAGE)) {
                    dmImageBtn.setTextColor(getResources().getColor(R.color.colorRipple));
                    dmEditText.setText("Doraemon.png");
                    dmEditText.setEnabled(false);
                    messageType = TYPE_IMAGE;
                } else {
                    DialogSimple();
                    messageType = TYPE_MESSAGE;
                }
                break;

            case R.id.dmSendBtn: // 메시지 전송 버튼 클릭
                break;
        }
    }

    private void scrollMyListViewToBottom() {
        dmListView.post(new Runnable() {
            @Override
            public void run() {
                dmListView.clearFocus();
                dmListAdapter.notifyDataSetChanged();
                dmListView.requestFocusFromTouch();
                dmListView.setSelection(dmListView.getCount() - 1);
            }
        });
    }

    private void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("메시지 타입을 초기화 하시겠습니까?").setCancelable(
                false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dmLocationBtn.setTextColor(getResources().getColor(R.color.concrete));
                        dmImageBtn.setTextColor(getResources().getColor(R.color.concrete));
                        dmEditText.setEnabled(true);
                        dmEditText.setText(null);
                        messageType = TYPE_MESSAGE;

                        dialog.cancel();
                    }
                }).setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
}
