package com.konkuk.dna.utils.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konkuk.dna.MainActivity;
import com.konkuk.dna.R;
import com.konkuk.dna.auth.LoginActivity;
import com.konkuk.dna.chat.ChatUser;
import com.konkuk.dna.chat.ChatUserAdapter;
import com.konkuk.dna.chat.ChatUserDetailFragment;
import com.konkuk.dna.friend.FriendActivity;
import com.konkuk.dna.friend.manage.FriendDetailFragment;
import com.konkuk.dna.user.MyPageActivity;
import com.konkuk.dna.user.UserSettingActivity;
import com.konkuk.dna.utils.EventListener;
import com.konkuk.dna.utils.SocketConnection;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

import static android.support.v4.app.ActivityCompat.finishAffinity;
import static android.view.View.GONE;
import static com.konkuk.dna.utils.JsonToObj.ConnectUserJsonToObj;
import static com.konkuk.dna.utils.helpers.InitHelpers.updateDrawer;

public class InitHelpers {

    private static NMapPlacemark placemark = new NMapPlacemark();

    private static Dbhelper dbhelper;
    private static ArrayList<ChatUser> cu = new ArrayList<>();
    private static ChatUserAdapter chatUserAdapter;
    private static ListView ccuListView;

    private static Context cont;
    private static View view;

    private static final int SOCKET_GEO = 6;
    private static final int SOCKET_DIRECT = 7;

    public static void setProfile(View v) {
        // TODO 현재 유저의 정보를 초기화해줍니다.

        ImageView pfAvatar = (ImageView) v.findViewById(R.id.PfAvatar);
        TextView pfNickname = (TextView) v.findViewById(R.id.PfNickname);
        TextView pfID = (TextView) v.findViewById(R.id.PfID);

       // ;

        if (dbhelper.getMyAvatar() != null) { // TODO 현재 유저의 프로필 url이 null이 아닐 경우를 조건으로 줘야 합니다.
            Picasso.get().load(dbhelper.getMyAvatar()).into(pfAvatar);
        }

        pfNickname.setText(dbhelper.getMyNickname());
        pfID.setText(dbhelper.getMyId());
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void OnEventListener(EventListener event) {
//        DrawyerAsyncTask dat = new DrawyerAsyncTask(cont, ccuListView, view);
//        switch (event.message) {
//            case SOCKET_GEO:
//                Log.e("Socket ON", "geo");
//                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
//                    dat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event.args, String.valueOf(dbhelper.getMyIdx()));
//                }else{
//                    dat.execute(event.args, String.valueOf(dbhelper.getMyIdx()));
//                }
//                break;
//            case SOCKET_DIRECT:
//                Log.e("Socket ON", "direct");
//                break;
//            default:
//                break;
//        }
//    }

    public static void initDrawer(final Context context, final View v, int type) {
        cont = context;
        view = v;
        dbhelper = new Dbhelper(context);


        setProfile(v);
        LinearLayout drawerForUserList = (LinearLayout) v.findViewById(R.id.drawerForUserList);
        LinearLayout drawerForFriend = (LinearLayout) v.findViewById(R.id.drawerForFriend);

        LinearLayout myPageBtn = (LinearLayout) v.findViewById(R.id.myPageBtn);
        LinearLayout homeBtn = (LinearLayout) v.findViewById(R.id.homeBtn);
        LinearLayout logoutBtn = (LinearLayout) v.findViewById(R.id.logoutBtn);
        RelativeLayout setChatBtn = (RelativeLayout) v.findViewById(R.id.setChatBtn);
        RelativeLayout setFriendBtn = (RelativeLayout) v.findViewById(R.id.setFriendBtn);

        TextView drawerPosition = v.findViewById(R.id.drawerPosition);
        TextView drawerRadius = v.findViewById(R.id.drawerRadius);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
                alt_bld.setMessage("정말 로그아웃 하실건가요?").setCancelable(
                        false).setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbhelper.logoutUser();
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                dialog.cancel();
                            }
                        }).setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MyPageActivity.class);
                context.startActivity(intent);
            }
        });

        setChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserSettingActivity.class);
                context.startActivity(intent);
            }
        });

        setFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FriendActivity.class);
                context.startActivity(intent);
            }
        });

        /*
        * 현재 채팅 환경
        * */
//        drawerPosition.setText("서울특별시 광진구 능동로 120 건국대학교");
        drawerPosition.setText(dbhelper.getMyAddress());
        drawerRadius.setText(dbhelper.getMyRadius()+"m");


        if (type == 0) {
            drawerForFriend.setVisibility(GONE);

            // TODO chatUsers 배열에 실제 접속중인 유저 리스트 추가해야 합니다.

            ccuListView = (ListView) v.findViewById(R.id.ccuList);
            final ArrayList<ChatUser> chatUsers = new ArrayList<ChatUser>();

            SocketConnection.getSocket().on("geo", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //Log.e("Socket Ping-geo", args[0].toString());
                    Log.e("Socket Ping-geo", "geo");
                    DrawyerAsyncTask dat = new DrawyerAsyncTask(context, ccuListView, v);

                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                        dat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args[0].toString(), String.valueOf(dbhelper.getMyIdx()));
                    }else{
                        dat.execute(args[0].toString(), String.valueOf(dbhelper.getMyIdx()));
                    }

                }
            });

            chatUserAdapter = new ChatUserAdapter(context, R.layout.chat_item_ccu, cu);
            ccuListView.setAdapter(chatUserAdapter);
        } else if (type == 1){
            drawerForUserList.setVisibility(GONE);
            drawerForFriend.setVisibility(GONE);

//            SocketConnection.getSocket().on("direct", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.e("Socket Ping-direct", args[0].toString());
//                    DrawyerAsyncTask dat = new DrawyerAsyncTask(context, ccuListView, v);
//
////                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
////                        dat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args[0].toString(), String.valueOf(dbhelper.getMyIdx()));
////                    }else{
////                        dat.execute(args[0].toString(), String.valueOf(dbhelper.getMyIdx()));
////                    }
//                }
//            });
            // TODO 해당 친구의 프로필을 입력해줘야 합니다.
//            ImageView friendAvatar = (ImageView) v.findViewById(R.id.friendAvatar);
//            TextView friendNickname = (TextView) v.findViewById(R.id.friendNickname);
//            TextView friendNicknameText = (TextView) v.findViewById(R.id.friendNicknameText);
//            TextView friendInfo = (TextView) v.findViewById(R.id.friendInfo);

//            friendNickname.setText("fakerzzang");
//            friendNicknameText.setText("fakerzzang"); // 바로 위에꺼랑 같은 값으로 세팅해주세요!
//            friendInfo.setText("이번 롤드컵에 페이커가 출전하지 못해서 굉장히 유감입니다. 사실 롤을 본 지는 오래 돼서 지금 봐도 뭐가 뭔지는 모릅니다.");
        } else if (type == 2) {
            drawerForUserList.setVisibility(GONE);
            drawerForFriend.setVisibility(GONE);
        }

        dbhelper.close();
    }

    public static void updateDrawer(final Context context, View v) {
        TextView drawerPosition = v.findViewById(R.id.drawerPosition);
        TextView drawerRadius = v.findViewById(R.id.drawerRadius);

        /*
         * 현재 채팅 환경
         * */
        drawerPosition.setText(dbhelper.getMyAddress());
        drawerRadius.setText(dbhelper.getMyRadius()+"m");
        v.invalidate();
    }
    public static void getPermission(Activity activity) {
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            //권한이 없을 경우
//            //최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) &&
//                    ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                // 사용자가 임의로 권한을 취소시킨 경우
//                // 권한 재요청
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//                return;
//            } else {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//                return;
//            }

        ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
}

/*
 * 비동기 Http 연결 작업 클래스
 * */
class DrawyerAsyncTask extends AsyncTask<String, Integer, ArrayList<ChatUser>> {
    private Context context;
    private ListView ccuListView;
    private ChatUserAdapter chatUserAdapter;
    private View v;

    public DrawyerAsyncTask(Context context, ListView ccuListView, View v) {
        this.context = context;
        this.ccuListView = ccuListView;
        this.v = v;
        //super();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected ArrayList<ChatUser> doInBackground(String... strings) {
        ArrayList<ChatUser> cu = ConnectUserJsonToObj(strings[0], Integer.parseInt(strings[1]));
        return cu;
    }

    @Override
    protected void onPostExecute(final ArrayList<ChatUser> arCU) {
        super.onPostExecute(arCU);

        chatUserAdapter = new ChatUserAdapter(context, R.layout.chat_item_ccu, arCU);
        ccuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                ChatUserDetailFragment chatUserDetailFragment = new ChatUserDetailFragment();

                chatUserDetailFragment.setData(arCU.get(i));
                chatUserDetailFragment.show(fragmentManager, "chatUserDetailFragment");
            }
        });
        ccuListView.setAdapter(chatUserAdapter);
        chatUserAdapter.notifyDataSetChanged();

        updateDrawer(context, v);
    }

}