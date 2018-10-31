package com.konkuk.dna.friend.manage;


import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.konkuk.dna.friend.manage.Request;
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

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (type == TYPE_RECEIVED) {
                v = layoutInflater.inflate(R.layout.friend_item_request_received, null);
            } else if (type == TYPE_ACCEPTED) {
                v = layoutInflater.inflate(R.layout.friend_item_request_accepted, null);
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
        }
        return v;
    }
}
