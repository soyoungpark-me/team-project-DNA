package com.konkuk.dna.chat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.konkuk.dna.post.Post;
import com.konkuk.dna.post.PostDetailActivity;
import com.konkuk.dna.utils.EventListener;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.ServerURL;
import com.konkuk.dna.utils.SocketConnection;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.AnimHelpers;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.MainActivity;
import com.konkuk.dna.R;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.map.MapFragment;
import com.konkuk.dna.utils.helpers.NameHelpers;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.konkuk.dna.utils.HttpReqRes.requestHttpPostLambda;
import static com.konkuk.dna.utils.JsonToObj.ChatAllJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.PostingCntJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.PostingJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.getLocationContents;
import static com.konkuk.dna.utils.ObjToJson.LocationObjToJson;
import static com.konkuk.dna.utils.ObjToJson.SendMsgObjToJson;
import static com.konkuk.dna.utils.ObjToJson.StoreObjToJson;

public class ChatActivity extends BaseActivity {
    private DrawerLayout menuDrawer;
    private MapFragment mapFragment;
    private View mapFragmentView;
    private RelativeLayout barLayout;

    private ListView msgListView;
    private EditText msgEditText;
    private Button msgSpeakerBtn, msgLocationBtn, msgImageBtn;
    private LinearLayout mapSizeBtn, bestChatBtn, bestChatMargin, msgListEmpty;
    private RelativeLayout bestChatWrapper;
    private TextView mapSizeAngle, bestChatAngle, bestChatContent, bestChatNickname, bestChatDate;
    private ImageView bestChatAvatar;

    private ChatListAdapter chatListAdapter;
    private ArrayList<ChatMessage> chatMessages;

    private SimpleDateFormat timeFormat;

    private ValueAnimator slideAnimator;
    private AnimatorSet set;
    private int height, radius;
    private double longitude, latitude;
    private boolean mapIsOpen = true, bestChatIsOpen = true;

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";          // 일반 메시지 전송
    private final String TYPE_LOUDSPEAKER = "LoudSpeaker";  // 확성기 전송
    private final String TYPE_LOCATION = "Location";        // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";              // 이미지 전송
    private final String TYPE_SHARE = "Share";              // 포스팅 공유
    private String messageType = TYPE_MESSAGE;

    private Dbhelper dbhelper;
    //private Socket mSocket;
    private Context context = this;

    private final int GET_FROM_GALLERY = 3;
    private Uri selectedImage;

    private static final int SOCKET_NEW_MSG = 3;
    private static final int SOCKET_APPLY_LIKE = 4;
    private static final int SOCKET_SPEAKER = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        EventBus.getDefault().register(this);

        init();
        //socketInit();
    }

    public void init() {

        menuDrawer = findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 0);

        chatMessages = new ArrayList<ChatMessage>();
        mapFragmentView = (View) findViewById(R.id.mapFragment);
        barLayout = (RelativeLayout) findViewById(R.id.barLayout);
        msgListView = (ListView) findViewById(R.id.msgListView);
        msgEditText = (EditText) findViewById(R.id.msgEditText);
        msgSpeakerBtn = (Button) findViewById(R.id.msgSpeakerBtn);
        msgLocationBtn = (Button) findViewById(R.id.msgLocationBtn);
        msgImageBtn = (Button) findViewById(R.id.msgImageBtn);
        mapSizeBtn = (LinearLayout) findViewById(R.id.mapSizeBtn);
        mapSizeAngle = (TextView) findViewById(R.id.mapSizeAngle);
        bestChatBtn = (LinearLayout) findViewById(R.id.bestChatBtn);
        bestChatAngle = (TextView) findViewById(R.id.bestChatAngle);
        bestChatWrapper = (RelativeLayout) findViewById(R.id.bestChatWrapper);
        bestChatMargin = (LinearLayout) findViewById(R.id.bestChatMargin);
        //msgListEmpty = (LinearLayout) findViewById(R.id.msgListEmpty);

        /*
        * GPS 받아오기, 반경 설정하기
        * */
        longitude = gpsTracker.getLongitude();
        latitude = gpsTracker.getLatitude();

        dbhelper = new Dbhelper(this);
        radius = dbhelper.getMyRadius();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        bestChatContent = (TextView) findViewById(R.id.bestChatContent);
        bestChatNickname = (TextView) findViewById(R.id.bestChatNickname);
        bestChatDate = (TextView) findViewById(R.id.bestChatDate);
        bestChatAvatar = (ImageView) findViewById(R.id.bestChatAvatar);

        //베스트 챗, 채팅 불러오기
        ChatSetAsyncTask csat = new ChatSetAsyncTask(this, radius, msgListView, bestChatAvatar,
                bestChatContent, bestChatNickname, bestChatDate, msgListEmpty, chatMessages, 0);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            csat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, longitude, latitude);
        }else{
            csat.execute(longitude, latitude);
        }

        chatListAdapter = new ChatListAdapter(context, R.layout.chat_item_left, chatMessages);


        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO : 채팅 중 하나를 눌렀을 경우 동작 구현
                ChatMessage clicked_msg = (ChatMessage) adapterView.getAdapter().getItem(i);
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

                        getSelectedPostAsync gspa = new getSelectedPostAsync(context);
                        gspa.execute(Integer.parseInt(parse[idx]));

                        break;

                    default:
                        break;
                }
            }
        });

        //TODO : 채팅 길게 눌렀을 때 구현
        msgListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(50);

                ChatMessage clicked_msg = (ChatMessage) adapterView.getAdapter().getItem(i);

                if(clicked_msg.getIdx() != dbhelper.getMyIdx()) {
                    FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    ChatUserDetailFragment chatUserDetailFragment = new ChatUserDetailFragment();

                    //TODO : 해당 유저 정보 받아서 세팅하기
                    chatUserDetailFragment.setData(new ChatUser(clicked_msg.getIdx(), clicked_msg.getUserName(), clicked_msg.getAvatar(), clicked_msg.getAnonymity(), true));
                    chatUserDetailFragment.show(fragmentManager, "chatUserDetailFragment");
                }else{
                    Toast.makeText(context, "스스로가 왜 궁금하시죠?^~^", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

//        생성된 후 바닥으로 메시지 리스트를 내려줍니다.
//        scrollMyListViewToBottom();

        // TODO: 공유 일경우, 메세지 세팅하기
        String postTitle = getIntent().getStringExtra("postTitle");
        int postNum = getIntent().getIntExtra("postNum",-1);
        if(postNum!=-1){
            msgEditText.setText(postTitle+"_"+postNum);
            msgEditText.setEnabled(false);
            msgEditText.setBackgroundColor(getResources().getColor(R.color.concrete));
            messageType = TYPE_SHARE;
        }


        timeFormat = new SimpleDateFormat("a h:m", Locale.KOREA);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;

        slideAnimator = ValueAnimator
                .ofInt(AnimHelpers.dpToPx(this, 150), height).setDuration(400);

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mapFragmentView.getLayoutParams();
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) barLayout.getLayoutParams();
                params.height = value.intValue();
                int divider = (height - AnimHelpers.dpToPx(getApplicationContext(), 150)) / 50;
                params2.setMargins(0, -1 * AnimHelpers.dpToPx(getApplicationContext(), value - AnimHelpers.dpToPx(getApplicationContext(), 150))/divider, 0, 0);
                mapFragmentView.requestLayout();
            }
        });

        slideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(0, R.anim.fade_out);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        set = new AnimatorSet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventListener(EventListener event){
        ChatSetAsyncTask csat;
        switch (event.message){
            case SOCKET_NEW_MSG:
                Log.e("Socket GET MESSAGE", "MSG COME!!!");
                csat = new ChatSetAsyncTask(context, radius, msgListView, bestChatAvatar, bestChatContent,
                        bestChatNickname, bestChatDate, msgListEmpty, chatMessages, 0);

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                    csat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, longitude, latitude);
                }else{
                    csat.execute(longitude, latitude);
                }

                break;
            case SOCKET_APPLY_LIKE:
                Log.e("Socket GET Like", "Apply Like COME!!!" + event.args);
//                csat = new ChatSetAsyncTask(context, radius, msgListView, bestChatAvatar, bestChatContent,
//                        bestChatNickname, bestChatDate, msgListEmpty, chatMessages,1);
//
//                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
//                    csat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, longitude, latitude);
//                }else{
//                    csat.execute(longitude, latitude);
//                }
                break;
//            case SOCKET_SPEAKER:
//                Log.e("Socket PUSH", "PUSH COME!!!");
//
//                break;
            default:
                break;
        }
    }

    public void socketInit(){
//        // TODO: 새로운 메시지가 오면 화면을 새로고침 할 것
//        SocketConnection.getSocket().on("new_msg", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Log.e("Socket GET MESSAGE", "MSG COME!!!");
//
//                ChatSetAsyncTask csat = new ChatSetAsyncTask(context, radius, msgListView, bestChatAvatar, bestChatContent, bestChatNickname, bestChatDate, chatMessages);
//                csat.execute(longitude, latitude);
//                //scrollMyListViewToBottom();
//            }
//        });
//        // TODO: 좋아요 신호가 오면 화면을 새로고침 할 것
//        SocketConnection.getSocket().on("apply_like", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Log.e("Socket GET Like", "Apply Like COME!!!"+args[0].toString());
//
//                ChatSetAsyncTask csat = new ChatSetAsyncTask(context, radius, msgListView, bestChatAvatar, bestChatContent, bestChatNickname, bestChatDate, chatMessages);
//                csat.execute(longitude, latitude);
//                //scrollMyListViewToBottom();
//            }
//        });
//        // TODO: push가 오면 push 알림을 띄울 것
//        SocketConnection.getSocket().on("speaker", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Log.e("Socket PUSH", "PUSH COME!!!");
//
////                ChatSetAsyncTask csat = new ChatSetAsyncTask(context, radius, msgListView, bestChatAvatar, bestChatContent, bestChatNickname, bestChatDate);
////                csat.execute(longitude, latitude);
//            }
//        });

    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.msgSearchBtn: // 검색 버튼 클릭
                break;

            case R.id.backBtn: // 뒤로가기 버튼 클릭
                this.onBackPressed();
                break;

            case R.id.menuBtn: // 메뉴 버튼 클릭
                if (!menuDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    menuDrawer.openDrawer(Gravity.RIGHT);
                }
                break;

            case R.id.mapSizeBtn: // 지도 크기 조정 버튼 클릭
                if (mapIsOpen) {
                    mapSizeAngle.animate().rotation(180).setDuration(400L).start();
                    AnimHelpers.animateViewHeight(this, mapFragmentView, AnimHelpers.dpToPx(this, 150), 0);
                    AnimHelpers.animateMargin(this, mapSizeBtn, "top", 200L,
                            AnimHelpers.dpToPx(this, 100), 0);
                    AnimHelpers.animateMargin(this, bestChatMargin, "right", 200L,
                            AnimHelpers.dpToPx(this,35), AnimHelpers.dpToPx(this,80));
                    AnimHelpers.animateMargin(this, bestChatBtn, "chat", 200L, 50, 0);
                } else {
                    mapSizeAngle.animate().rotation(360).setDuration(400L).start();
                    AnimHelpers.animateViewHeight(this, mapFragmentView, 0, AnimHelpers.dpToPx(this, 150));
                    AnimHelpers.animateMargin(this, mapSizeBtn, "top", 200L,
                            0, AnimHelpers.dpToPx(this, 100));
                    AnimHelpers.animateMargin(this, bestChatMargin, "right", 200L,
                            AnimHelpers.dpToPx(this,80), AnimHelpers.dpToPx(this,35));
                    AnimHelpers.animateMargin(this, bestChatBtn, "chat", 200L, 0, 50);
                }
                mapIsOpen = !mapIsOpen;
                break;
            case R.id.bestChatBtn: // 베스트챗 버튼 클릭
                if (bestChatIsOpen) {
                    bestChatAngle.setText(getResources().getString(R.string.fa_crown));
                    AnimHelpers.animateViewHeight(this, bestChatWrapper, AnimHelpers.dpToPx(this, 50), 0);
                } else {
                    bestChatAngle.setText(getResources().getString(R.string.fa_x));
                    AnimHelpers.animateViewHeight(this, bestChatWrapper, 0, AnimHelpers.dpToPx(this, 50));
                }
                bestChatIsOpen = !bestChatIsOpen;
                break;

            case R.id.msgSpeakerBtn: // 확성기 버튼 클릭
                // TODO 현재 유저의 포인트를 계산해서 사용 가능할 경우에만 활성화해야 합니다.
                if (messageType.equals(TYPE_LOUDSPEAKER)) { // 확성기 모드일 경우 다시 누르면 취소됩니다.
                    msgSpeakerBtn.setTextColor(getResources().getColor(R.color.concrete));
                    messageType = TYPE_MESSAGE;
                } else {
                    msgSpeakerBtn.setTextColor(getResources().getColor(R.color.red));
                    messageType = TYPE_LOUDSPEAKER;
                }
                break;

            case R.id.msgLocationBtn: // 장소 전송 버튼 클릭
                // TODO 현재 주소를 messageEditText에 채워줍니다.
                if (messageType.equals(TYPE_MESSAGE) || messageType.equals(TYPE_LOUDSPEAKER)) {
                    msgLocationBtn.setTextColor(getResources().getColor(R.color.colorRipple));
                    msgEditText.setText(dbhelper.getMyAddress());
                    msgEditText.setEnabled(false);
                    msgEditText.setBackgroundColor(getResources().getColor(R.color.concrete));
                    messageType = TYPE_LOCATION;
                } else {
                    DialogSimple();
                    messageType = TYPE_MESSAGE;
                }
                break;

            case R.id.msgImageBtn: // 이미지 전송 버튼 클릭
                // TODO 현재 주소를 messageEditText에 채워줍니다.
                if (messageType.equals(TYPE_MESSAGE) || messageType.equals(TYPE_LOUDSPEAKER)) {

                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                    // Guide : activityResult로 가시오.
//                    msgImageBtn.setTextColor(getResources().getColor(R.color.colorRipple));
//                    msgEditText.setText("사진 첨부됨");
//                    msgEditText.setBackgroundColor(Color.GRAY);
//                    msgEditText.setEnabled(false);
//                    messageType = TYPE_IMAGE;
                } else {
                    DialogSimple();
                    messageType = TYPE_MESSAGE;
                }
                break;

            case R.id.msgSendBtn: // 메시지 전송 버튼 클릭

                if(messageType == TYPE_LOCATION){
                    JsonObject jdata = LocationObjToJson(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                    msgEditText.setText(jdata.toString());
                }
                JsonObject sendMsgJson = SendMsgObjToJson(dbhelper, gpsTracker.getLongitude(), gpsTracker.getLatitude(), messageType, msgEditText.getText().toString());
                SocketConnection.emit("save_msg", sendMsgJson);

                msgSpeakerBtn.setTextColor(getResources().getColor(R.color.concrete));
                msgEditText.setText("");
                msgEditText.setBackgroundColor(Color.WHITE);
                msgEditText.setEnabled(true);
                messageType = TYPE_MESSAGE;
                break;
        }
    }



    private void scrollMyListViewToBottom() {
        msgListView.post(new Runnable() {
            @Override
            public void run() {
                msgListView.clearFocus();
                chatListAdapter.notifyDataSetChanged();
                msgListView.requestFocusFromTouch();
                msgListView.setSelection(msgListView.getCount() - 1);
            }
        });
    }

    private void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("메시지 타입을 초기화 하시겠습니까?").setCancelable(
                false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        msgLocationBtn.setTextColor(getResources().getColor(R.color.concrete));
                        msgImageBtn.setTextColor(getResources().getColor(R.color.concrete));
                        msgEditText.setBackgroundColor(Color.WHITE);
                        msgEditText.setEnabled(true);
                        msgEditText.setText(null);
                        messageType = (messageType == TYPE_LOUDSPEAKER) ? TYPE_LOUDSPEAKER : TYPE_MESSAGE;

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
    public void onBackPressed() {
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

        int top = AnimHelpers.dpToPx(this, 5);
        if (mapIsOpen) {
            top = AnimHelpers.dpToPx(this, 105);
        }

        AnimHelpers.animateMargin(this, mapSizeBtn, "top", 200L,
                top, AnimHelpers.dpToPx(this, -40));

        // TODO 1번 누르면 ChatActivity가 destroy되지 않고 그냥 mainActivity가 보임
        // TODO 2번쨰, 3번째 눌렀을때 앱이 종료 되도록 만들기
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.initMapCenter(longitude, latitude, radius);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK){
            selectedImage = data.getData();

            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(selectedImage, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            String imagepath = cursor.getString(column_index);

//            Log.e("onActivityResult", imagepath);
            getLambdaImageAsyncTask glat = new getLambdaImageAsyncTask(context);
            glat.execute(imagepath);

//            msgImageBtn.setTextColor(getResources().getColor(R.color.colorRipple));
//            msgEditText.setText(selectedImage.toString());
//            msgEditText.setEnabled(false);
//            messageType = TYPE_IMAGE;
            //Bitmap bitmap = null;
        }
    }
}


class getLambdaImageAsyncTask extends AsyncTask<String, Integer, String>{
    private Context context;
    private ProgressDialog dialogImage;

    public getLambdaImageAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialogImage = new ProgressDialog(context);
        dialogImage.setCancelable(false);
        dialogImage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogImage.setMessage("사진 첨부 중 입니다..");
        // show dialog
        dialogImage.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = requestHttpPostLambda(ServerURL.AWS_LAMBDA_API_URL, strings[0]);

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("doInBack", s+"!");
        dialogImage.dismiss();

    }
}

/*
* 비동기 Http 연결 작업 클래스
* */
class ChatSetAsyncTask extends AsyncTask <Double, Integer, ArrayList<String>>  {
    private Context context;
    private String m_token;
    private Integer radius;
    private Dbhelper dbhelper;

    private ChatListAdapter chatListAdapter;
    private ListView msgListView;
    private TextView bestChatContent, bestChatNickname, bestChatDate;
    private ImageView bestChatAvatar;
    private LinearLayout msgListEmpty;

    private ArrayList<ChatMessage> chatMessages;
    private int now_pos=-1;

    private int mode;
    private static final int MODE_RENEW = 0;
    private static final int MODE_LIKE = 1;

    public ChatSetAsyncTask(Context context, Integer radius,
                            ListView msgListView, ImageView bcAvatar, TextView bcContent, TextView bcNickname,
                            TextView bcDate, LinearLayout msgListEmpty,
                            ArrayList<ChatMessage> chatMessages, int mode){
        this.context=context;
        this.radius=radius;
        this.msgListView = msgListView;
        this.bestChatAvatar = bcAvatar;
        this.bestChatContent = bcContent;
        this.bestChatDate = bcDate;
        this.bestChatNickname = bcNickname;
        this.chatMessages = chatMessages;
        this.msgListEmpty = msgListEmpty;
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        now_pos = msgListView.getLastVisiblePosition();
        //now_pos = msgListView.getFirstVisiblePosition();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(Double... doubles) {

        //ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

        ArrayList<String> resultArray = new ArrayList<>();

        HttpReqRes httpreq = new HttpReqRes();
        dbhelper = new Dbhelper(context);
        m_token = dbhelper.getAccessToken();
        Double lng = doubles[0];
        Double lat = doubles[1];

        String repBestChat = httpreq.requestHttpPostBestChat(ServerURL.DNA_SERVER+ServerURL.PORT_SOCKET_API+"/best", m_token, lng, lat, radius);
        String repMsgAll = httpreq.requestHttpPostMsgAll(ServerURL.DNA_SERVER+ServerURL.PORT_SOCKET_API+"/messages/", m_token, lng, lat, radius);

        resultArray.add(0, repBestChat);
        resultArray.add(1, repMsgAll);

        return resultArray;
    }

    @Override
    protected void onPostExecute(ArrayList<String> resultArray) {
        super.onPostExecute(resultArray);

        /*
        * 베스트챗 내용 세팅
        * */
        ArrayList<ChatMessage> bestMessages = new ArrayList<ChatMessage>();
        if(resultArray.get(0)!=null) {
            bestMessages = ChatAllJsonToObj(dbhelper.getMyIdx(), resultArray.get(0));
        }

        if (bestMessages != null && bestMessages.size() > 0) {
            ChatMessage bestMessage = bestMessages.get(0);
            if (bestMessage.getAvatar() != null && bestMessage.getAnonymity() != 1) {
                Picasso.get()
                        .load(bestMessage.getAvatar())
                        .into(bestChatAvatar);
            }

            bestChatContent.setText(bestMessage.getContents());

            String nickname = "";
            if (bestMessage.getAnonymity() == 1) {
                nickname = NameHelpers.makeName(bestMessage.getIdx());
            } else {
                nickname = bestMessage.getUserName();
            }

            bestChatNickname.setText(nickname);
            bestChatDate.setText(bestMessage.getDate());
        }else{
            bestChatContent.setText("근처에 아직 작성된 베스트챗이 없습니다.");
            bestChatNickname.setText("from DNA.");
        }

        Log.e("!!!!!!!!!!!!", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        /*
        * 전체 채팅 내용 세팅
        * */
        if(chatMessages!=null) {
           // chatMessages.clear();
        }

        if(resultArray.get(1)!=null) {
            chatMessages = ChatAllJsonToObj(dbhelper.getMyIdx(), resultArray.get(1));
            //거꾸로 받아온 리스트를 역순으로 바꿈
            Collections.sort(chatMessages);
            //Collections.reverse(chatMessages);
        }

        if (chatMessages == null || chatMessages.size() == 0) {
            if (msgListView != null) msgListView.setVisibility(View.GONE);
            if (msgListEmpty != null) msgListEmpty.setVisibility(View.VISIBLE);
            return;
        }

        chatListAdapter = new ChatListAdapter(context, R.layout.chat_item_left, chatMessages);
        msgListView.setAdapter(null);
        msgListView.setAdapter(chatListAdapter);

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
        msgListView.post(new Runnable() {
            @Override
            public void run() {
                msgListView.clearFocus();
                chatListAdapter.notifyDataSetChanged();
                msgListView.requestFocusFromTouch();

                msgListView.setSelection(msgListView.getCount() - 1);
            }
        });
    }

    private void scrollMyListViewToMemory() {
        msgListView.post(new Runnable() {
            @Override
            public void run() {
                msgListView.clearFocus();
                chatListAdapter.notifyDataSetChanged();
                msgListView.requestFocusFromTouch();

                msgListView.setSelection(now_pos);
            }
        });
    }
}

class getSelectedPostAsync extends AsyncTask<Integer, Void, Post>{

    private Context context;
    private Dbhelper dbhelper;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public getSelectedPostAsync(Context context){
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
