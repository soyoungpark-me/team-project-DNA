package com.konkuk.dna.chat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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

import com.google.gson.JsonObject;
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
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.konkuk.dna.utils.JsonToObj.ChatAllJsonToObj;
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
    private LinearLayout mapSizeBtn, bestChatBtn, bestChatMargin;
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
        ChatSetAsyncTask csat = new ChatSetAsyncTask(this, radius, msgListView, bestChatAvatar, bestChatContent, bestChatNickname, bestChatDate, chatMessages);
        csat.execute(longitude, latitude);

        chatListAdapter = new ChatListAdapter(context, R.layout.chat_item_left, chatMessages);

        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatMessage cm = (ChatMessage) adapterView.getAdapter().getItem(i);

                SocketConnection.emit("like", dbhelper.getAccessToken(), cm.getMsg_idx());
                //mSocket.emit("like", dbhelper.getAccessToken(), cm.getMsg_idx());
            }
        });

        // TODO 베스트챗 내용 세팅해줘야 합니다!
//        Picasso.get()
//                .load("http://slingshotesports.com/wp-content/uploads/2017/07/34620595595_b4c90a2e22_b.jpg")
//                .into(bestChatAvatar);
//        bestChatContent.setText("아ㅠㅠ저희 아이가 사라졌어요!!5세 남아고 빨간 상의에 하얀 반바지를 입고있어요. 백화점 행사매대 구경하는 사이에 사라져서 어디로 갔는지 모르겠네요ㅠㅠ");
//        bestChatNickname.setText("낄렵");
//        bestChatDate.setText("오후 06:06");

        // TODO chatMessages 배열에 실제 메시지 추가해야 합니다.
//        chatMessages = new ArrayList<ChatMessage>();
//        chatMessages.add(new ChatMessage(0, "3457soso", null, "http://file3.instiz.net/data/cached_img/upload/2018/06/22/14/2439cadf98e7bebdabd174ed41ca0849.jpg", "오후 12:34", "0", TYPE_IMAGE, 127.07934279999995, 37.5407625));
//        chatMessages.add(new ChatMessage(1, "3457soso", null, "내용내용", "오후 12:34", "2", TYPE_LOUDSPEAKER, 127.0793427999999, 37.540762));
//        chatMessages.add(new ChatMessage(2, "3457soso", null, "내용내용내용내용내용", "오후 12:34", "1", TYPE_MESSAGE, 127.079342799995, 37.540625));
//        chatMessages.add(new ChatMessage(3, "3457soso", null, "내용내용내용내용내용", "오후 12:34", "1", TYPE_LOUDSPEAKER, 127.0734279999995, 37.5407625));
//        chatMessages.add(new ChatMessage(4, "3457soso", null, "내용내용내용", "오후 12:34", "0", TYPE_MESSAGE, 127.0794279999995, 37.507625));
//        chatMessages.add(new ChatMessage(5, "3457soso", null, "내용내용내용", "오후 12:34", "0", TYPE_MESSAGE, 127.0793427999995, 37.540625));
//        chatMessages.add(new ChatMessage(6, "3457soso", null, "{\"lat\":37.550544099999996,\"lng\":127.07221989999998}", "오후 12:34", "1", TYPE_LOCATION, 127.07934279999995, 37.540762));
//        chatMessages.add(new ChatMessage(7, "3457soso", null, "http://www.ohfun.net/contents/article/images/2016/0830/1472551795750578.jpeg", "오후 12:34", "2", TYPE_IMAGE, 127.079342799995, 37.5407625));
//        chatMessages.add(new ChatMessage(8, "3457soso", null, "내용내용", "오후 12:34", "2", TYPE_MESSAGE, 127.0793427999995, 37.540625));
//        chatMessages.add(new ChatMessage(10, "3457soso", null, "{\"lat\":37.550544099999,\"lng\":127.07221989999}", "오후 12:34", "1", TYPE_LOCATION, 127.07934279999995, 37.540762));
//
//        ArrayList<Integer> temp = new ArrayList<>();
//        chatMessages.add(new ChatMessage(25, "fakerzzang", null, "와 오늘 날씨 짱이네",
//                "오후 06:02", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "ㅇㅇ다 같은생각으로 놀러나왔나봄. 사람 엄청많네",
//                "오후 06:03", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(30, "heeyeon", "https://t1.daumcdn.net/cfile/tistory/2342E74059073F6F22",
//                "사거리에서 버스킹하는 사람들 되게 잘한다",
//                "오후 06:04", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(20, "낄렵", null,
//                "아 저 아 저희 아이가 사라졌는데 빨간 상의 입은 5세 남아에요!!혹시 보신분 계시면 DM주세요ㅠㅠㅠ",
//                "오후 06:05", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                " 확성기 사용하시는게 좋을것 같은데..!",
//                "오후 06:05", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(20, "낄렵", null,
//                "아ㅠㅠ저희 아이가 사라졌어요!!5세 남아고 빨간 상의에 하얀 반바지를 입고있어요. 백화점 행사매대 구경하는 사이에 사라져서 어디로 갔는지 모르겠네요ㅠㅠ",
//                "오후 06:06", "5", "LoudSpeaker", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "혹시 아이 사진 같은건 없나요??",
//                "오후 06:07", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(20, "낄렵", null,
//                "오늘 찍은건 아니긴한데 이렇게 생겼어요!",
//                "오후 06:09", "0", "Image", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(20, "낄렵", null,
//                "https://s3.ap-northeast-2.amazonaws.com/dna-edge/images/1540458541495_%C3%A1%C2%84%C2%8B%C3%A1%C2%85%C2%AA%C3%A1%C2%86%C2%BC%C3%A1%C2%84%C2%89%C3%A1%C2%85%C2%A5%C3%A1%C2%86%C2%A8%C3%A1%C2%84%C2%92%C3%A1%C2%85%C2%A7%C3%A1%C2%86%C2%AB%C3%A1%C2%84%C2%81%C3%A1%C2%85%C2%A9%C3%A1%C2%84%C2%86%C3%A1%C2%85%C2%A1.jpg",
//                "오후 06:09", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "fakerzzang", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "에구..저도 그 근처니까 한번 찾아볼게요!",
//                "오후 06:11", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "어 저 저 지금 본거같은데!!애기 혹시 무지개색 운동화 신은거 맞나요??",
//                "오후 06:20", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(20, "낄렵", null,
//                "헐 맞아요!!!저 죄송한데 혹시 애기 붙잡고 DM좀 주실수있나요??거기 어딘가요ㅠ갈게요ㅠㅠ",
//                "오후 06:20", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));

//
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "지금도 접속중이신 분들 꽤 있으시네요",
//                "오전 03:05", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "챔스 보시는 분?",
//                "오전 03:05", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "ㅋㅋ이런 경기는 봐야죠",
//                "오전 03:06", "2", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "세비야전에서 부상당했는데",
//                "오전 03:06", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "참고하세요 ㅠ 3주 아웃이라고 했어요",
//                "오전 03:06", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "와 방금 수아레즈 패스가",
//                "오전 03:25", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "흠ㅠㅠ 3주라니 챔스 조 1위 가능하려나",
//                "오후 03:26", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "(ㅋㅋ)",
//                "오전 03:26", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "메시 관중석에 있네요",
//                "오전 03:30", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "쟤도 공 차다 앉아있으려니 심심하겠다",
//                "오전 03:31", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "수아레즈 크로스가 진짜 좋았네",
//                "오후 04:15", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "진짜 택배인줄 발 앞에 딱 갖다줌ㅋㅋ",
//                "오전 04:15", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "박소영님 혹시 꾸레세요?",
//                "오전 04:15", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "바르샤가 뭔가 얄미워도 잘하긴 잘하는 듯",
//                "오전 04:16", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "넵ㅋㅋ",
//                "오후 04:17", "2", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "혹시 이번주에 엘클라시코 있는 것도 같이 보실래요??",
//                "오전 04:18", "1", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "오 좋아요 친추주세요~~",
//                "오후 04:18", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "부럽다ㅠㅠ 전 출근...",
//                "오전 04:20", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "이번 엘클이",
//                "오전 04:20", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "호날두 메시 둘 다 없다는데 그래서 기대됨ㅋ",
//                "오전 04:21", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "봉황당이라고 예전에 갔던 곳인데 엘클이라 여기서 중계해줄 것 같아요!",
//                "오전 04:24", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "흠 좀 멀긴한데 같이 갑시당",
//                "오후 04:25", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "같은 아파트니까 만나서 가여^^~",
//                "오후 04:25", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//
//        chatMessages.add(new ChatMessage(50, "who_sy", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540456685047_postman.png",
//                "담엔 저도ㅠㅠ!",
//                "오전 04:26", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(40, "messizzang", "https://s3.ap-northeast-2.amazonaws.com/dna-edge-profile/profile/1540454607080_cut.jpg",
//                "다른 후기들도 있으니까 한번 봐주세요~",
//                "오전 04:26", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));

//
//        chatMessages.add(new ChatMessage(30, "heeyeon", "https://t1.daumcdn.net/cfile/tistory/2342E74059073F6F22",
//                " 날씨 개좋다ㅠ과제 정말 죽어버리고만 싶고..",
//                "오후 03:04", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                " 시험 끝나니까 과제지옥이네 진심ㅋㅋㅋ",
//                "오후 03:05", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(50, "히짱", null,
//                "헐 님들 공C에서 불남!!!!꽤 큰데??공대 사람들 다 대피해야할듯",
//                "오후 03:05", "2", "LoudSpeaker", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(51, "faker", "https://www.thesuperplay.com/web/upload/NNEditor/20170908/17-08-22_FAKER_0186_shop1_004642.jpg",
//                "헐 공A인데 불 바로 안꺼질거같아요?",
//                "오후 03:06", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(50, "히짱", null,
//                "네네 저도 지금 짐챙겨서 대피하긴했는데 오늘 건조하고 바람도 쎄서 금방 번질거같아요 님도 얼른 가방챙겨 나오세요ㅠㅠ",
//                "오후 03:06", "-", "Message", 127.07221989999998,37.550544099999996, temp, 12));
//        chatMessages.add(new ChatMessage(5, "박소영", "http://blogfiles.naver.net/MjAxODAyMDlfMjE4/MDAxNTE4MTU0NTQ1OTg0.g4NXMX_nSSbOlNao9TdWsPCqBwvpzxg-jO8QNLUgP0og.7FBB9GplCKhCu0Wf84ow6RsASBqwWWP87x4qdVUmbwAg.JPEG.elsa0613/1517870953274.jpg",
//                "헐 공대 안그래도 건물 약한데 무너지는거 아님?",
//                "오후 03:07", "0", "Message", 127.07221989999998,37.550544099999996, temp, 12));




//         생성된 후 바닥으로 메시지 리스트를 내려줍니다.

//        scrollMyListViewToBottom();

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
                csat = new ChatSetAsyncTask(context, radius, msgListView, bestChatAvatar, bestChatContent, bestChatNickname, bestChatDate, chatMessages);
                csat.execute(longitude, latitude);
                break;
            case SOCKET_APPLY_LIKE:
                Log.e("Socket GET Like", "Apply Like COME!!!" + event.args);
                csat = new ChatSetAsyncTask(context, radius, msgListView, bestChatAvatar, bestChatContent, bestChatNickname, bestChatDate, chatMessages);
                csat.execute(longitude, latitude);
                break;
            case SOCKET_SPEAKER:
                Log.e("Socket PUSH", "PUSH COME!!!");
                break;
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
                    msgEditText.setText("서울시 광진구 화양동 1 건국대학교");
                    msgEditText.setEnabled(false);
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

//                    msgImageBtn.setTextColor(getResources().getColor(R.color.colorRipple));
//                    msgEditText.setText("Doraemon.png");
//                    msgEditText.setEnabled(false);
//                    messageType = TYPE_IMAGE;
                } else {
                    DialogSimple();
                    messageType = TYPE_MESSAGE;
                }
                break;

            case R.id.msgSendBtn: // 메시지 전송 버튼 클릭

                JsonObject sendMsgJson = SendMsgObjToJson(dbhelper, gpsTracker.getLongitude(), gpsTracker.getLatitude(), messageType, msgEditText.getText().toString());
                SocketConnection.emit("save_msg", sendMsgJson);

                msgEditText.setText("");
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
        finish();

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
            msgImageBtn.setTextColor(getResources().getColor(R.color.colorRipple));
            msgEditText.setText(selectedImage.toString());
            msgEditText.setEnabled(false);
            messageType = TYPE_IMAGE;
            //Bitmap bitmap = null;
        }
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

    private ArrayList<ChatMessage> chatMessages;
    private int now_pos=-1;

    private void scrollMyListViewToBottom() {
        msgListView.post(new Runnable() {
            @Override
            public void run() {
                msgListView.clearFocus();
                chatListAdapter.notifyDataSetChanged();
                msgListView.requestFocusFromTouch();

                if(now_pos>0){
                    msgListView.setSelection(now_pos);
                }else{
                    msgListView.setSelection(msgListView.getCount() - 1);
                }

            }
        });
    }

    public ChatSetAsyncTask(Context context, Integer radius,
                            ListView msgListView, ImageView bcAvatar, TextView bcContent, TextView bcNickname, TextView bcDate,
                            ArrayList<ChatMessage> chatMessages){
        this.context=context;
        this.radius=radius;
        this.msgListView = msgListView;
        this.bestChatAvatar = bcAvatar;
        this.bestChatContent = bcContent;
        this.bestChatDate = bcDate;
        this.bestChatNickname = bcNickname;
        this.chatMessages = chatMessages;
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

        String repBestChat = httpreq.requestHttpPostMsgAll(ServerURL.DNA_SERVER+ServerURL.PORT_SOCKET_API+"/best", m_token, doubles[0], doubles[1], radius);
        String repMsgAll = httpreq.requestHttpPostMsgAll(ServerURL.DNA_SERVER+ServerURL.PORT_SOCKET_API+"/messages", m_token, doubles[0], doubles[1], radius);

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
        bestMessages = ChatAllJsonToObj(dbhelper.getMyIdx(), resultArray.get(0));

        if(bestMessages.size()>0 && bestMessages != null) {
            Picasso.get()
                    .load(bestMessages.get(0).getAvatar())
                    .into(bestChatAvatar);
            bestChatContent.setText(bestMessages.get(0).getContents());
            bestChatNickname.setText(bestMessages.get(0).getUserName());
            bestChatDate.setText(bestMessages.get(0).getDate());
        }else{
            bestChatContent.setText("이 지역의 베스트챗이 존재하지 않아요ㅠ");
            bestChatNickname.setText("리보솜");
        }

        //Log.e("!!!!!!!!!!!!!!!!!!!!!!!!", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        /*
        * 전체 채팅 내용 세팅
        * */
        if(chatMessages!=null) {
            chatMessages.clear();
        }
        chatMessages = ChatAllJsonToObj(dbhelper.getMyIdx(), resultArray.get(1));

        //거꾸로 받아온 리스트를 역순으로 바꿈
        Collections.reverse(chatMessages);

        chatListAdapter = new ChatListAdapter(context, R.layout.chat_item_left, chatMessages);
        msgListView.setAdapter(chatListAdapter);

        // 생성된 후 바닥으로 메시지 리스트를 내려줍니다.
        scrollMyListViewToBottom();


    }
}