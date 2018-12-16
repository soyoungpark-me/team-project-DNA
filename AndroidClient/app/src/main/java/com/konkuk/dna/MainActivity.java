package com.konkuk.dna;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.konkuk.dna.utils.EventListener;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.SocketConnection;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.chat.ChatActivity;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.utils.helpers.AnimHelpers;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.map.MapFragment;
import com.konkuk.dna.post.Comment;
import com.konkuk.dna.post.Post;
import com.konkuk.dna.post.PostFormActivity;
import com.konkuk.dna.utils.helpers.NameHelpers;
import com.nhn.android.maps.NMapView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.konkuk.dna.utils.JsonToObj.PostingCntJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.PostingJsonToObj;
import static com.konkuk.dna.utils.JsonToObj.PushJsonToObj;
import static com.konkuk.dna.utils.ObjToJson.StoreObjToJson;

public class MainActivity extends BaseActivity {
    private DrawerLayout menuDrawer;
    private MapFragment mapFragment;
    private View mapFragmentView;
    private FloatingActionButton gotoChatBtn, postWriteBtn;

    public ArrayList<Post> getPosts() {
        return posts;
    }

    private ArrayList<Post> posts;

    private ValueAnimator slideAnimator;
    private AnimatorSet set;
    private int height, radius;
    private double longitude, latitude;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    private Dbhelper dbhelper;

    private ProgressDialog dialogWaitSocket;

    private static final int SOCKET_CONNECT = 0;
    private static final int SOCKET_PING = 1;
    private static final int SOCKET_NEW_DM = 2;
    private static final int SOCKET_NEW_MSG = 3;
    private static final int SOCKET_APPLY_LIKE = 4;
    private static final int SOCKET_SPEAKER = 5;
    private static final int SOCKET_GEO = 6;
    private static final int SOCKET_DIRECT = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitHelpers.getPermission(this);

        setContentView(R.layout.activity_main);

        // firebase (push)
        FirebaseMessaging.getInstance().subscribeToTopic("chat");
        Log.d("MainActivity", "token : " + FirebaseInstanceId.getInstance().getToken());

        if(SocketConnection.getSocket()==null){
            dialogWaitSocket = new ProgressDialog(this);
            dialogWaitSocket.setCancelable(false);
            dialogWaitSocket.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialogWaitSocket.setMessage("서버 연동 중 입니다..");
            // show dialog
            dialogWaitSocket.show();
        }

        socketinit();
        init();
        //socketinit();
        FloatingActionButton myPost = (FloatingActionButton)findViewById(R.id.postWriteBtn);
        myPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createNewPosting(v);
            }
        });
    }

    public void socketinit(){
        dbhelper = new Dbhelper(this);

        //소켓을 사용하는 가장 첫번째 액티비티에서 소켓 커넥션을 실행한다.
        SocketConnection.initInstance();

        //TODO : MainActivity Listener
        // 소켓 연결되면 store 할 것
        SocketConnection.getSocket().on(SocketConnection.getSocket().EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("Socket Connected",SocketConnection.getSocket().connected()+"");
                JsonObject storeJson = StoreObjToJson(dbhelper, gpsTracker.getLongitude(), gpsTracker.getLatitude());
                SocketConnection.emit("store", storeJson);
                if(dialogWaitSocket!=null) {
                    dialogWaitSocket.dismiss();
                }
            }
        });
        // 핑이 오면 update 할 것
        SocketConnection.getSocket().on("ping", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("Socket ON", "ping, radius:"+dbhelper.getMyRadius());
                JsonObject updateJson = StoreObjToJson(dbhelper, gpsTracker.getLongitude(), gpsTracker.getLatitude());
                SocketConnection.emit("update", "geo", updateJson);
                SocketConnection.emit("update", "direct", updateJson);
            }
        });

        //TODO : DMActivity Listener
        SocketConnection.getSocket().on("new_dm", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("Socket ON", "new_dm");
                EventBus.getDefault().post(new EventListener(SOCKET_NEW_DM, null));
            }
        });

        //TODO : ChatActivity Listener
        // 새로운 메시지가 오면 화면을 새로고침 할 것
        SocketConnection.getSocket().on("new_msg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                EventBus.getDefault().post(new EventListener(SOCKET_NEW_MSG, null));
            }
        });
        // 좋아요 신호가 오면 화면을 새로고침 할 것
        SocketConnection.getSocket().on("apply_like", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                EventBus.getDefault().post(new EventListener(SOCKET_APPLY_LIKE, args[0].toString()));
            }
        });
        // push가 오면 push 알림을 띄울 것
        SocketConnection.getSocket().on("speaker", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                ArrayList<String> result = PushJsonToObj(args[0].toString());

                //TODO : add Asynctask
                String name = "";
                String avatar = null;
                String contents = result.get(3);

                if(Integer.parseInt(result.get(2))==0){
                    //when anonymity = 0
                    name = result.get(0);
                    avatar = result.get(1);
                }else{
                    //when anonymity = 1
                    name = NameHelpers.makeName(Integer.parseInt(result.get(4)));
                    avatar = null;
                }
                new generatePictureStyleNotification(getApplicationContext(), name, contents,
                        avatar).execute();

                EventBus.getDefault().post(new EventListener(SOCKET_SPEAKER, null));
            }
        });

        //TODO: InitHelpers Listener
//        SocketConnection.getSocket().on("geo", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                EventBus.getDefault().post(new EventListener(SOCKET_GEO, args[0].toString()));
//            }
//        });
//
        SocketConnection.getSocket().on("direct", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                //Log.e("Socket direct", args[0].toString());
                EventBus.getDefault().post(new EventListener(SOCKET_DIRECT, args[0].toString()));
            }
        });
    }

    public void init() {
        //dbhelper = new Dbhelper(this);

        menuDrawer = findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 0);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;

        mapFragmentView = (View) findViewById(R.id.mapFragment);
        gotoChatBtn = (FloatingActionButton) findViewById(R.id.gotoChatBtn);
        postWriteBtn = (FloatingActionButton) findViewById(R.id.postWriteBtn);

        AnimHelpers.animateMargin(this, gotoChatBtn, "main", 400L,
                AnimHelpers.dpToPx(this, -80), AnimHelpers.dpToPx(this, 95));
        AnimHelpers.animateMargin(this, postWriteBtn, "main", 400L,
                AnimHelpers.dpToPx(this, -80), AnimHelpers.dpToPx(this, 25));

        // TODO 반경, 위치 초기값 설정해줘야 합니다!
        radius = dbhelper.getMyRadius();
        dbhelper.close();
        // 에뮬레이터가 위치를 못잡아서 임시로 넣어놨슴다
//        longitude = 127.17934280;
//        latitude = 37.56076250;
        longitude = gpsTracker.getLongitude();
        latitude = gpsTracker.getLatitude();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

//        posts = new ArrayList<Post>();

        try {
            posts = new showPostingAllAsync(this).execute().get();
        } catch (Exception e){
            e.printStackTrace();
//        new showPostingAsync().execute(posts)
        };
        // TODO 포스트의 리스트를 서버에서 불러와서 넣어줘야 합니다.

        slideAnimator = ValueAnimator
            .ofInt(height, AnimHelpers.dpToPx(this, 150)).setDuration(500);

        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mapFragmentView.getLayoutParams();
                params.height = value.intValue();
                int divider = height / 50;
                params.setMargins(0, AnimHelpers.dpToPx(getApplicationContext(), (AnimHelpers.dpToPx(getApplicationContext(), 150) + height-value)/divider), 0, 0);
                mapFragmentView.requestLayout();
            }
        });

        slideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(chatIntent);
                overridePendingTransition(0, R.anim.fade_out);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        set = new AnimatorSet();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msgMenuBtn: // 메뉴 버튼 클릭
                if (!menuDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    menuDrawer.openDrawer(Gravity.RIGHT);
                }

                break;

            case R.id.gotoChatBtn:
                set.play(slideAnimator);
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.start();
                AnimHelpers.animateMargin(this, gotoChatBtn, "main", 400L,
                        AnimHelpers.dpToPx(this, 95), AnimHelpers.dpToPx(this, -80));
                AnimHelpers.animateMargin(this, postWriteBtn, "main", 400L,
                        AnimHelpers.dpToPx(this, 25), AnimHelpers.dpToPx(this, -80));
                break;

//            case R.id.postWriteBtn:
//                Intent formIntent = new Intent(this, PostFormActivity.class);
//                startActivity(formIntent);
//                break;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        mapFragmentView.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        mapFragmentView.requestLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapFragment.initMapCenter(longitude, latitude, dbhelper.getMyRadius());
        mapFragment.drawPostLocations(posts);
    }

    //뒤로가기를 누르면 화면이 깨지지 않고, 2번 누르면 앱이 종료되도록 만들기
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            finish();
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "2초내에 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void finish() {
        super.finish();
        Log.e("Acivity", "finish");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(SocketConnection.getSocket()!=null) {
            SocketConnection.getSocket().off("ping");
            SocketConnection.getSocket().off("new_msg");
            SocketConnection.getSocket().off("new_dm");
            SocketConnection.getSocket().off("speaker");
            SocketConnection.getSocket().off("apply_like");
            SocketConnection.getSocket().off("geo");
            SocketConnection.getSocket().off("direct");
        }
        SocketConnection.disconnect();

        Log.e("App", "destroy");
        //Log.e("Socket", SocketConnection.getSocket().connected()+"");

    }

    public void createNewPosting(View v){
        Intent newIntent = new Intent(this, PostFormActivity.class);
        startActivity(newIntent);
    }


}

class showPostingAllAsync extends AsyncTask<Void, Void, ArrayList<Post>>{

    private Context context;
    private Dbhelper dbhelper;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public showPostingAllAsync(Context context){
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

        dbhelper.close();

        return postings;
    }

    @Override
    protected void onPostExecute(ArrayList<Post> postings) {

        super.onPostExecute(postings);
    }
}

class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

    private Context mContext;
    private String user, message, imageUrl;

    public generatePictureStyleNotification(Context context, String user, String message, String imageUrl) {
        super();
        this.mContext = context;
        this.user = user;
        this.message = message;
        this.imageUrl = imageUrl;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        InputStream in;
        if(imageUrl!=null) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.avatar);
            return icon;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.putExtra("key", "value");
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);


        long[] vPattern = {100, 1000, 200, 1000}; //쉼,진동,쉼,진동

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setVibrate(vPattern)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.dna)
                        .setLargeIcon(result)
                        .setContentTitle(user+"님의 확성기") //nickname
                        .setContentText(message);

        NotificationManager mnm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        mnm.notify(0, mBuilder.build());
    }
}