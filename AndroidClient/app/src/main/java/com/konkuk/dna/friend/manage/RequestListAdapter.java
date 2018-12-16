package com.konkuk.dna.friend.manage;


import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.konkuk.dna.friend.FriendActivity;
import com.konkuk.dna.friend.manage.Request;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestListAdapter extends ArrayAdapter<Request> {
    Context context;
    ArrayList<Request> requests;
    String currentUserId;
    Boolean isMyMessage = false;

    private int type;              // 보여줄 요청의 타입을 지정해 타입에 해당하는 뷰를 보여줍니다.
    private int TYPE_RECEIVED = 1; // 받은 요청
    private int TYPE_ACCEPTED = 2; // 상대방이 수락해 친구 관계가 성립된 경우
    private int TYPE_SENEDED = 3;  // 보냈는데 아직 상대방이 수락/거절 하지 않은 요청

    private static Typeface NSEB;
    private static Typeface NSB;
    private static Typeface NSR;
    private static Typeface fontAwesomeR;
    private static Typeface fontAwesomeS;

    /* 메시지의 타입을 구분하기 위한 변수들입니다 */
    private final String TYPE_MESSAGE = "Message";     // 일반 메시지 전송
    private final String TYPE_LOCATION = "Location";    // 현재 위치 전송
    private final String TYPE_IMAGE = "Image";       // 이미지 전송

    public RequestListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Request> objects, int type) {
        super(context, resource, objects);

        this.context = context;
        this.requests = objects;
        this.type = type;

        init();
    }

    public void init() {
        if(NSEB == null) {
            NSEB = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareEB.ttf");
        }
        if(NSB == null) {
            NSB = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareB.ttf");
        }
        if(NSR == null) {
            NSR = Typeface.createFromAsset(context.getAssets(), "fonts/NanumSquareR.ttf");
        }
        if(fontAwesomeR == null) {
            fontAwesomeR = Typeface.createFromAsset(context.getAssets(), "fonts/fa-regular-400.ttf");
        }
        if(fontAwesomeS == null) {
            fontAwesomeS = Typeface.createFromAsset(context.getAssets(), "fonts/fa-solid-900.ttf");
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
        Request request = requests.get(position);
        final int idx = request.getIdx();

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (type == TYPE_RECEIVED) {
                v = layoutInflater.inflate(R.layout.friend_item_request_received, null);
            } else if (type == TYPE_SENEDED) {
                v = layoutInflater.inflate(R.layout.friend_item_request_sended, null);
            }

            ImageView reqAvatar = (ImageView) v.findViewById(R.id.reqAvatar);
            if (request.getAvatar() != null) {
                Picasso.get().load(request.getAvatar()).into(reqAvatar);
            }

            TextView reqNickname = (TextView) v.findViewById(R.id.reqNickname);
            TextView reqDate = (TextView) v.findViewById(R.id.reqDate);
            reqNickname.setText(request.getNickname());
            reqNickname.setTypeface(NSEB);
            reqDate.setText(request.getDate());
            reqDate.setTypeface(NSB);

            ImageButton acceptBtn, denyBtn, deleteBtn;

            if (type == TYPE_RECEIVED) {
                acceptBtn = (ImageButton) v.findViewById(R.id.acceptBtn);
                acceptBtn.setVisibility(View.VISIBLE);
                denyBtn = (ImageButton) v.findViewById(R.id.denyBtn);
                denyBtn.setVisibility(View.VISIBLE);
                acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("Clicked", "accept friend");
//                    DialogSimple();
                    new NotifyFriendAsync(context).execute(idx, 1);
                    }
                });
                denyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("Clicked", "deny friend");
                        new NotifyFriendAsync(context).execute(idx, 2);
//                    DialogSimple();
                    }
                });
            } else if (type == TYPE_SENEDED) {
                deleteBtn = (ImageButton) v.findViewById(R.id.deleteBtn);
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("Clicked", "delete friend");
                        new NotifyFriendAsync(context).execute(idx, 2);
//                    DialogSimple();
                    }
                });
            }

        }
        return v;
    }
}

class NotifyFriendAsync extends AsyncTask<Integer, String, Integer> {
    private Context context;
    private Dbhelper dbhelper;

    //    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
    public NotifyFriendAsync(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        HttpReqRes httpReqRes = new HttpReqRes();
        dbhelper = new Dbhelper(context);

        httpReqRes.requestHttpNotifyFriend("https://dna.soyoungpark.me:9013/api/friends/" + ints[0], dbhelper.getAccessToken(), ints[1]);

        dbhelper.close();
        return ints[1];
    }

    @Override
    protected void onPostExecute(Integer num) {
        FriendActivity fa = (FriendActivity)context;
        fa.init();

        super.onPostExecute(num);

    }
}