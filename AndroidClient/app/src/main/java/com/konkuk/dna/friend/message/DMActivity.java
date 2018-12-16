package com.konkuk.dna.friend.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.konkuk.dna.chat.ChatListMapFragment;
import com.konkuk.dna.post.Post;
import com.konkuk.dna.post.PostDetailActivity;
import com.konkuk.dna.utils.EventListener;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.ServerURL;
import com.konkuk.dna.utils.SocketConnection;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.socket.emitter.Emitter;

import static com.konkuk.dna.utils.ConvertType.DatetoStr;
import static com.konkuk.dna.utils.JsonToObj.ChatAllJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.DMMsgJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.PostingJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.getLocationContents;
import static com.konkuk.dna.utils.ObjToJson.SendDMObjToJson;
import static com.konkuk.dna.utils.ObjToJson.SendMsgObjToJson;

public class DMActivity extends BaseActivity {
    private DrawerLayout menuDrawer;

    private Context context = this;
    private Dbhelper dbhelper;

    private ListView dmListView;
    private EditText dmEditText;
    private TextView updatedAtText;
    private TextView sentWhoText;
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

    private static final int SOCKET_NEW_DM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_dm);
        EventBus.getDefault().register(this);

        init();
        //socketInit();
    }

    public void init() {
        menuDrawer = findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 1);

        dmListView = (ListView) findViewById(R.id.dmListView);
        dmEditText = (EditText) findViewById(R.id.dmEditText);
        dmLocationBtn = (Button) findViewById(R.id.dmLocationBtn);
        dmImageBtn = (Button) findViewById(R.id.dmImageBtn);
        updatedAtText = (TextView) findViewById(R.id.updatedAtText);
        sentWhoText = (TextView) findViewById(R.id.friendNicknameText);

        dbhelper = new Dbhelper(this);

        dmMessages = new ArrayList<DMMessage>();
        roomIdx = getIntent().getIntExtra("roomIdx", -1);

        if (roomIdx != -1) {
            updatedAtText.setText(getIntent().getStringExtra("roomUpdated"));
            sentWhoText.setText((getIntent().getStringExtra("roomWho")));

            DMSetAsyncTask dsat = new DMSetAsyncTask(this, dmListView, 0);

            //DM채팅 불러오기
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                dsat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(roomIdx), getIntent().getStringExtra("roomWho"));
            }else{
                dsat.execute(String.valueOf(roomIdx), getIntent().getStringExtra("roomWho"));
            }

            // TODO dmMessages 배열에 실제 메시지 추가해야 합니다. roomIdx로 가져오면 됩니다.
//            dmMessages.add(new DMMessage(1, "http://file3.instiz.net/data/cached_img/upload/2018/06/22/14/2439cadf98e7bebdabd174ed41ca0849.jpg", "오후 12:34", TYPE_IMAGE));
//            dmMessages.add(new DMMessage(0, "내용내용", "오후 12:34", TYPE_MESSAGE));
//            dmMessages.add(new DMMessage(0, "내용내용내용내용내용", "오후 12:34", TYPE_MESSAGE));
//            dmMessages.add(new DMMessage(0, "https://pbs.twimg.com/media/DbYfg2IWkAENdiS.jpg", "오후 12:34", TYPE_IMAGE));
//            dmMessages.add(new DMMessage(1, "내용내용내용", "오후 12:34", TYPE_MESSAGE));
//            dmMessages.add(new DMMessage(0, "내용내용내용", "오후 12:34", TYPE_MESSAGE));
//            dmMessages.add(new DMMessage(1, "{\"lat\":37.550544099999996,\"lng\":127.07221989999998}", "오후 12:34", TYPE_LOCATION));
//            dmMessages.add(new DMMessage(1, "http://www.ohfun.net/contents/article/images/2016/0830/1472551795750578.jpeg", "오후 12:34", TYPE_IMAGE));
//            dmMessages.add(new DMMessage(0, "내용내용333", "오후 12:34", TYPE_SHARE));

            String postTitle = getIntent().getStringExtra("postTitle");
            int postNum = getIntent().getIntExtra("postNum",-1);
            if(postNum!=-1){
                dmEditText.setText(postTitle+"_"+postNum);
                dmEditText.setEnabled(false);
                dmEditText.setBackgroundColor(Color.GRAY);
                messageType = TYPE_SHARE;
            }
        }

        dmListAdapter = new DMListAdapter(this, R.layout.chat_item_left, dmMessages);

        dmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DMMessage clicked_msg = (DMMessage) adapterView.getAdapter().getItem(i);
                String clicked_type = clicked_msg.getType();

                switch (clicked_type){
                    case TYPE_LOCATION:
                        //TODO : 지도 위치 보여주기
                        ArrayList<Double> loc = getLocationContents(clicked_msg.getContents());
                        if(loc!=null){
                            FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                            ChatListMapFragment chatListMapFragment = ChatListMapFragment.newInstance(loc.get(1), loc.get(0));
                            chatListMapFragment.show(fragmentManager, "chatListMapFragment");
                        }
                        break;

                    case TYPE_IMAGE:
                        //TODO : 사진 확대하기(할 수있으면)
                        break;

                    case TYPE_SHARE:
                        //TODO : 공유된 포스팅 들어가기
                        String[] parse = clicked_msg.getContents().split("_");
                        int idx = parse.length - 1;
                        Log.e("check", parse[idx]);

                        DMgetSelectedPostAsync gspa = new DMgetSelectedPostAsync(context);
                        gspa.execute(Integer.parseInt(parse[idx]));
                        break;

                    default:
                        break;
                }
            }
        });
//        dmListView.setAdapter(dmListAdapter);
//
//        // 생성된 후 바닥으로 메시지 리스트를 내려줍니다.
//        scrollMyListViewToBottom();

        timeFormat = new SimpleDateFormat("a h:m", Locale.KOREA);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventListener(EventListener event) {
        DMSetAsyncTask dsat;
        switch (event.message){
            case SOCKET_NEW_DM:
                Log.e("Socket ON", "new_dm");
                dsat = new DMSetAsyncTask(context, dmListView, 1);
                dsat.execute(String.valueOf(roomIdx), getIntent().getStringExtra("roomWho"));
                scrollMyListViewToBottom();
                break;
            default:
                break;
        }
    }

    public void socketInit() {
        // TODO: 새로운 메시지가 오면 화면을 새로고침 할 것
//        SocketConnection.getSocket().on("new_dm", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Log.e("Socket GET D_MESSAGE", "D_MSG COME!!!");
//
//                DMSetAsyncTask dsat = new DMSetAsyncTask(context, dmListView);
//                dsat.execute(String.valueOf(roomIdx), getIntent().getStringExtra("roomWho"));
//                scrollMyListViewToBottom();
//            }
//        });
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
                    dmEditText.setText(dbhelper.getMyAddress());
                    dmEditText.setEnabled(false);
                    dmEditText.setBackgroundColor(getResources().getColor(R.color.concrete));
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
                JsonObject sendMsgJson
                        = SendDMObjToJson(roomIdx ,messageType, dmEditText.getText().toString());

                SocketConnection.emit("save_dm", dbhelper.getAccessToken(), sendMsgJson);

//                DMSetAsyncTask dsat = new DMSetAsyncTask(context, dmListView, 0);
//                dsat.execute(String.valueOf(roomIdx), getIntent().getStringExtra("roomWho"));
//                scrollMyListViewToBottom();

                dmEditText.setText("");
                dmEditText.setEnabled(true);
                dmEditText.setBackgroundColor(Color.WHITE);
                messageType = TYPE_MESSAGE;
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

    @Override
    protected void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbhelper.close();
        EventBus.getDefault().unregister(this);
    }
}



/*
 * 비동기 Http 연결 작업 클래스
 * */
class DMSetAsyncTask extends AsyncTask<String, Integer, ArrayList<DMMessage>> {
    private Context context;
    private String m_token;
    private Dbhelper dbhelper;

    private DMListAdapter dmListAdapter;
    private ListView dmListView;

    private int mode;

    private ArrayList<DMMessage> dmMessages;
    private int now_pos=-1;
    private static final int MODE_RENEW = 0;
    private static final int MODE_LIKE = 1;

    public DMSetAsyncTask(Context context, ListView dmListView, int mode){
        this.context=context;
        this.dmListView = dmListView;
        this.mode = mode;
        //this.dmMessages = dmMessages;
    }

    @Override
    protected void onPreExecute() {
        now_pos = dmListView.getFirstVisiblePosition();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<DMMessage> doInBackground(String... args) {

        HttpReqRes httpreq = new HttpReqRes();
        dbhelper = new Dbhelper(context);
        m_token = dbhelper.getAccessToken();

        String repMsgAll = httpreq.requestHttpGETDmMsgs
                (ServerURL.DNA_SERVER+ServerURL.PORT_SOCKET_API+"/room/"+args[0]+"/messages/", m_token);

        ArrayList<DMMessage> dmMessages = new ArrayList<DMMessage>();

        //내 idx, 상대방 닉네임, 메세지 전문
        dmMessages = DMMsgJsonToObj(dbhelper.getMyIdx(), args[1], repMsgAll);
        dbhelper.close();

        return dmMessages;
    }

    @Override
    protected void onPostExecute(ArrayList<DMMessage> dmMessages) {
        super.onPostExecute(dmMessages);

        //거꾸로 받아온 리스트를 역순으로 바꿈
        //Collections.reverse(dmMessages);

        dmListAdapter = new DMListAdapter(context, R.layout.chat_item_left, dmMessages);
        dmListView.setAdapter(dmListAdapter);

        // 생성된 후 바닥으로 메시지 리스트를 내려줍니다.
        switch (mode){
            case MODE_RENEW:
                scrollMyListViewToBottom();
                break;
            case MODE_LIKE:
                scrollMyListViewToMemory();
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

    private void scrollMyListViewToMemory() {
        dmListView.post(new Runnable() {
            @Override
            public void run() {
                dmListView.clearFocus();
                dmListAdapter.notifyDataSetChanged();
                dmListView.requestFocusFromTouch();
                dmListView.setSelection(now_pos);
            }
        });
    }
}

class DMgetSelectedPostAsync extends AsyncTask<Integer, Void, Post>{

    private Context context;
    private Dbhelper dbhelper;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public DMgetSelectedPostAsync(Context context){
        this.context = context;
    }

    @Override
    protected Post doInBackground(Integer... integers){

        HttpReqRes httpReqRes = new HttpReqRes();
        String result1 = httpReqRes.requestHttpGetPosting(ServerURL.DNA_SERVER+ServerURL.PORT_WAS_API+"/posting/show/" + integers[0]);

        //Log.e("URL", ServerURL.PORT_WAS_API+"/posting/show/" + integers);
        return PostingJsonToObj(result1, 2).get(0);
    }

    @Override
    protected void onPostExecute(Post posting) {

        Intent postIntent = new Intent(context, PostDetailActivity.class);
        postIntent.putExtra("post", posting);
        context.startActivity(postIntent);
        super.onPostExecute(posting);
    }
}